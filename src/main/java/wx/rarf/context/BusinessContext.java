package wx.rarf.context;

import org.springframework.stereotype.Component;
import rx.Observable;
import wx.rarf.resource.bag.ResourceBag;
import wx.rarf.resource.bag.UniResourceBag;
import wx.rarf.utils.InjectableWorker;
import wx.rarf.utils.throwable.LFThrowable;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apple on 16/4/28.
 */
@Component
public class BusinessContext {

    /**
     * @region 公共属性定义区域
     */

    //静态属性
    public enum StepType {
        FIRST,
        ALL
    }

    /**
     * @region 公共方法定义区域
     */

    public BusinessContext() {
        this(new UniResourceBag());
    }

    public BusinessContext(UniResourceBag uniResourceBag) {

        this.currentStepIndex = 0; //初始化时当前处理的步骤为0

        this.sessionMap = new ConcurrentHashMap<>();

    }

    /**
     * @param worker
     * @return
     * @function 作为第一个调用的请求处理器
     */
    public BusinessContext requestHandler(InjectableWorkerWithUniResourceBag worker) {

        Observable<Action> observable = Observable.create(subscriber -> {
            try {
                //将本步骤的结果发射到下一步骤中
                subscriber.onNext(worker.doWork(uniResourceBag, new Action()));
            } catch (Throwable throwable) {
                //如果出现错误,则直接调用异常
                subscriber.onError(throwable);
            }
        });


        return this;
    }

    public BusinessContext step() {

        this.step("Step:" + this.currentStepIndex + 1, StepType.ALL);

        return this;
    }

    public BusinessContext step(String desc) {

        this.step(desc, StepType.ALL);

        return this;
    }

    public BusinessContext step(String desc, StepType stepType) {

        this.currentStepIndex++; //将当前的处理步骤序号加一

        //构建一个StepSession并且存入本地的序列中
        StepSession stepSession = new StepSession(desc, stepType);

        //将创建好的stepSession放入到SessionMap中
        this.sessionMap.put(this.currentStepIndex, stepSession);

        return this;
    }

    public BusinessContext reducer(String desc, InjectableWorkerWithUniResourceBag worker) {

        //构造一个当前处理的Observable
        Observable<Action> observable =
                //获取StepSession中对应的上一个Session
                this.sessionMap
                        .get(this.currentStepIndex - 1)
                        .combinedReducer()
                        .flatMap(action -> {
                    return Observable.<Action>create(subscriber -> {
                        try {
                            //将本步骤的结果发射到下一步骤中
                            subscriber.onNext(worker.doWork(uniResourceBag, action));
                        } catch (Throwable throwable) {
                            //如果出现错误,则直接调用异常
                            subscriber.onError(throwable);
                        }
                    });
                });

        //将该observable添加到对应的StepSession中
        this.sessionMap.get(this.currentStepIndex);

        return this;
    }

    public BusinessContext reducerInTransaction(String desc, InjectableWorkerWithUniResourceBag worker) {
        return this;
    }

    public BusinessContext reducerInNewThread(String desc, InjectableWorkerWithUniResourceBag worker) {
        return this;
    }

    public void responseHandler(InjectableWorkerWithUniResourceBag worker) {

        //考虑到中途某一步可能会报错,在这里进行整体的容错
        try {

            sessionMap.get(currentStepIndex).combinedReducer().toBlocking();

        } catch (Throwable throwable) {

            //判断是否为自己抛出的异常
            if (throwable instanceof LFThrowable) {

            } else {
                //否则为程序执行出错
                throwable.printStackTrace();

                //线上系统应该屏蔽返回错误码,开发时可以直接返回
            }

        } finally {

            //构造返回结果

            //进行日志操作,打印请求包体

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

    /**
     * @region 私有方法区域
     */


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
