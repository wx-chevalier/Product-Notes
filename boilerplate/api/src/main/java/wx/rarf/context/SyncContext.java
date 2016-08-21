package wx.rarf.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;
import wx.rarf.resource.bag.UniResourceBag;
import wx.rarf.utils.ErrorConfig;
import wx.rarf.utils.throwable.RARFThrowable;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apple on 16/4/28.
 */
@Configuration
public class SyncContext {

    /**
     * @region 公共属性定义区域
     */

    //静态属性
    public enum StepType {
        FIRST,
        ALL
    }

    @Bean
    @Scope(value = "prototype")
    public static SyncContext syncContextFactory() {
        return new SyncContext();
    }

    /**
     * @region 公共方法定义区域
     */

    public SyncContext() {

        this(new UniResourceBag());

    }

    public SyncContext(UniResourceBag uniResourceBag) {

        this.uniResourceBag = uniResourceBag;

        this.currentStepIndex = 0; //初始化时当前处理的步骤为0

        this.sessionMap = new ConcurrentHashMap<>();

        this.startTime = System.currentTimeMillis();

    }

    /**
     * @param worker
     * @return
     * @function 作为第一个调用的请求处理器
     */
    public SyncContext requestHandler(InjectableWorkerWithUniResourceBag worker) {

        //生成本步骤对应的StepSession
        StepSession stepSession = new StepSession(0, "RequestHandler", StepType.FIRST);

        UUID uuid = UUID.randomUUID();

        Observable<Action> observable = Observable.create(subscriber -> {
            try {

                stepSession.setStartTime(System.currentTimeMillis());

                Action action = worker.doWork(uniResourceBag, new Action());

                stepSession.setStopTime(System.currentTimeMillis());

                stepSession.setReducerAction(uuid, action);

                //判断是否返回了有效的Action
                if (action.isValid) {

                    //将本步骤的结果发射到下一步骤中
                    subscriber.onNext(action);
                }

            } catch (Throwable throwable) {

                //如果出现错误,则直接调用异常
                subscriber.onError(throwable);
            }
        });

        //将本Reducer添加到StepSession中
        stepSession.addReducer(observable, "RequestHandler", uuid);

        //将该StepSession添加到Session列表中
        sessionMap.put(this.currentStepIndex, stepSession);

        return this;
    }

    public SyncContext step() {

        this.step("Step:" + this.currentStepIndex + 1, StepType.ALL);

        return this;
    }

    public SyncContext step(String desc) {

        this.step(desc, StepType.ALL);

        return this;
    }

    public SyncContext step(String desc, StepType stepType) {

        this.currentStepIndex++; //将当前的处理步骤序号加一

        //构建一个StepSession并且存入本地的序列中
        StepSession stepSession = new StepSession(this.currentStepIndex, desc, stepType);

        //将创建好的stepSession放入到SessionMap中
        this.sessionMap.put(this.currentStepIndex, stepSession);

        return this;
    }

    public SyncContext reducer(String desc, InjectableWorkerWithUniResourceBag worker) {

        //为每个Observable生成一个唯一的标识
        UUID uuid = UUID.randomUUID();

        Observable observable = this.generateObservableForReducer(worker, uuid);

        //将该observable添加到对应的StepSession中
        this.sessionMap.get(this.currentStepIndex).addReducer(observable, desc, uuid);

        return this;
    }

    public SyncContext reducerInTransaction(String desc, InjectableWorkerWithUniResourceBag worker) {

        //为每个Observable生成一个唯一的标识
        UUID uuid = UUID.randomUUID();

        Observable observable = this.generateObservableForReducer(worker, uuid);

        //将该observable添加到对应的StepSession中
        this.sessionMap.get(this.currentStepIndex).addReducer(observable, desc, uuid);

        return this;
    }

    public void responseHandler(InjectableWorkerWithUniResourceBag worker) {

        //考虑到中途某一步可能会报错,在这里进行整体的容错
        //如果在上述的某个Observable内调用了onError,则会转入到异常处理
        try {

            //使用toBlocking方法,触发所有的Observable进行执行
            sessionMap.get(currentStepIndex).combinedReducer().toList().toBlocking().first();

        } catch (Throwable throwable) {


            //判断是否为自己抛出的异常
            if (throwable.getCause() instanceof RARFThrowable) {


                this.uniResourceBag.setResponseData((RARFThrowable) throwable.getCause());

            } else {

                throwable.printStackTrace();

                //否则为程序执行出错

                StringBuffer stackTraceStringBuffer = new StringBuffer();

                stackTraceStringBuffer.append(throwable.getMessage() + "\r\n");

                Arrays.stream(throwable.getStackTrace()).forEach(stackTraceElement -> {
                    stackTraceStringBuffer.append(stackTraceElement.toString() + "\r\n");
                });

                //线上系统应该屏蔽返回错误码,开发时可以直接返回
                RARFThrowable lfThrowable = new RARFThrowable.Builder().code(ErrorConfig.CODE_INTERNEL_EXCEPTION).desc("内部错误").build();

                this.uniResourceBag.setResponseData(lfThrowable);

                //仅当发生错误时候才设置
                this.uniResourceBag.setStackTrace(stackTraceStringBuffer.toString());
            }

        } finally {

            //构造返回结果
            this.stopTime = System.currentTimeMillis();

            //进行日志操作,打印请求包体
            JSONObject jsonObject = new JSONObject();

            //设置整体运行时间
            jsonObject.put("Context Uptime", stopTime - startTime);

            //设置运行的几个Step的信息
            JSONArray steps = new JSONArray();

            this.getSessionMap().forEach((integer, stepSession) -> {
                JSONObject stepJSONObject = new JSONObject();

                //添加编号
                stepJSONObject.put("stepId", integer);

                //添加描述
                stepJSONObject.put("stepDesc", stepSession.getDesc());

                //添加运行时间
                stepJSONObject.put("stepRunTime", stepSession.getStopTime() - stepSession.getStartTime());

                //添加实际运行的Reducer名称
                UUID actualReducerUUID = null;

                for (Map.Entry entry : stepSession.getReducerAction().entrySet()) {
                    if (((Action) entry.getValue()).isValid) {
                        actualReducerUUID = (UUID) entry.getKey();

                        //添加实际运行的Reducer的返回值
                        stepJSONObject.put("actualReducerAction", ((Action) entry.getValue()).toJSONObject());

                    }
                }

                if (actualReducerUUID != null) {

                    stepJSONObject.put("actualReducerUUID", actualReducerUUID);

                    stepJSONObject.put("actualReducerDesc", stepSession.getReducerDesc().get(actualReducerUUID));

                }

                steps.add(stepJSONObject);

            });

            jsonObject.put("steps", steps);

            uniResourceBag.setRuntimeLog(jsonObject);

        }
    }


    /**
     * 存放本次业务处理所需要的全局资源包
     */
    private UniResourceBag uniResourceBag;

    //存放每一个Step对应的Session,这里设置为ConcurrentMap以防止有并发程序调用
    private ConcurrentHashMap<Integer, StepSession> sessionMap;

    //存放配置阶段当前Step下标
    private Integer currentStepIndex;

    //记录本Context开始时间
    long startTime;

    //本Context结束时间
    long stopTime;

    /**
     * @region 私有方法区域
     */
    private Observable<Action> generateObservableForReducer(InjectableWorkerWithUniResourceBag workerWithUniResourceBag, UUID uuid) {

        final Integer fixedCurrentStepIndex = this.currentStepIndex;

        //构造一个当前处理的Observable
        Observable<Action> observable =
                //获取StepSession中对应的上一个Session
                this.sessionMap
                        .get(this.currentStepIndex - 1)
                        .combinedReducer()
                        .flatMap(action -> {
                            return Observable.create(subscriber -> {

                                try {
                                    //判断本步骤中的所有Reducer是否至少有一个设置了返回的Action
                                    StepSession stepSession = this.sessionMap.get(fixedCurrentStepIndex);

                                    stepSession.setStartTime(System.currentTimeMillis());

                                    Action returnAction = workerWithUniResourceBag.doWork(uniResourceBag, action);

                                    stepSession.setStopTime(System.currentTimeMillis());

                                    //将返回的Action添加到StepSession中
                                    stepSession.setReducerAction(uuid, returnAction);

                                    //判断是否返回了有效的Action
                                    if (returnAction.isValid) {

                                        //将本步骤的结果发射到下一步骤中
                                        subscriber.onNext(returnAction);
                                    }

                                    //判断是否本步骤的所有Reducer都已经执行完毕,如果没有一个发出信号的则抛出异常
                                    if (stepSession.isStepDead()) {
                                        subscriber
                                                .onError(new RARFThrowable
                                                        .Builder()
                                                        .code(ErrorConfig.CODE_1007)
                                                        .desc("在第" + fixedCurrentStepIndex + "步骤:" + stepSession.getDesc() + "没有任何Reducer响应").build());
                                    }

                                } catch (Throwable throwable) {
                                    //如果出现错误,则直接调用异常
                                    subscriber.onError(throwable);
                                }
                            });
                        });

        return observable;
    }


    /**
     * @region Getter/Setter区域
     */
    public UniResourceBag getUniResourceBag() {
        return uniResourceBag;
    }

    public void setUniResourceBag(UniResourceBag uniResourceBag) {
        this.uniResourceBag = uniResourceBag;
    }

    public ConcurrentHashMap<Integer, StepSession> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(ConcurrentHashMap<Integer, StepSession> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public Integer getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(Integer currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }
}
