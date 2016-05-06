package wx.rarf.context;

/**
 * Created by apple on 16/4/28.
 */

import rx.Observable;

import java.util.*;

/**
 * @function 用于存放每个逻辑的处理Session
 * @description
 */
public class StepSession {


    private final Integer stepIndex;

    /**
     * @function 构造函数
     */
    public StepSession(Integer stepIndex, String desc, SyncContext.StepType stepType) {

        this.desc = desc;

        this.stepIndex = stepIndex;

        this.stepType = stepType;

        this.reducers = new ArrayList<>();

        this.reducerAction = new HashMap();

        this.reducerDesc = new HashMap<>();
    }

    public void addReducer(Observable<Action> observable, String desc, UUID uuid) {

        this.reducers.add(observable);

        reducerDesc.put(uuid, desc);

    }

    public void setReducerAction(UUID uuid, Action action) {
        this.reducerAction.put(uuid, action);
    }

    public Observable<Action> combinedReducer() {
        return Observable.merge(reducers).first();
    }

    public Boolean isStepComplete() {
        return reducers.size() == reducerAction.size();
    }

    /**
     * @return
     * @function 判断本步骤是否一个值都没有发射
     */
    public Boolean isStepDead() {

        Boolean result = true;

        if (!isStepComplete()) {

            result = false;

            return result;

        }

        for (Map.Entry entry : this.reducerAction.entrySet()) {
            if (((Action) entry.getValue()).isValid) {
                result = false;
            }
        }

        return result;
    }

    /**
     * @region 私有变量定义
     */

    //本步骤的类型
    private final SyncContext.StepType stepType;

    //本步骤的描述
    private String desc;

    //本步骤包含的所有Reducer
    private List<Observable<Action>> reducers;

    //本步骤包含的Observable的描述
    private Map<UUID, String> reducerDesc;

    //本步骤包含的Observable的返回结果
    private Map<UUID, Action> reducerAction;

    private long startTime;

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {

        //设置结束时间时,设置最大的值
        if (this.stopTime == 0 || stopTime > this.stopTime) {
            this.stopTime = stopTime;

        }

    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {

        if (this.startTime == 0 || startTime < this.startTime) {
            this.startTime = startTime;
        }
    }

    private long stopTime;

    public SyncContext.StepType getStepType() {
        return stepType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Observable<Action>> getReducers() {
        return reducers;
    }

    public void setReducers(List<Observable<Action>> reducers) {
        this.reducers = reducers;
    }

    public Map<UUID, String> getReducerDesc() {
        return reducerDesc;
    }

    public void setReducerDesc(Map<UUID, String> reducerDesc) {
        this.reducerDesc = reducerDesc;
    }

    public Map<UUID, Action> getReducerAction() {
        return reducerAction;
    }

    public void setReducerAction(Map<UUID, Action> reducerAction) {
        this.reducerAction = reducerAction;
    }
}
