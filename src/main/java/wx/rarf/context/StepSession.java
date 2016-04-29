package wx.rarf.context;

/**
 * Created by apple on 16/4/28.
 */

import rx.Observable;
import wx.rarf.resource.bag.UniResourceBag;

import java.util.ArrayList;
import java.util.List;

/**
 * @function 用于存放每个逻辑的处理Session
 * @description
 */
public class StepSession {


    /**
     * @function 构造函数
     */
    public StepSession(String desc, BusinessContext.StepType stepType) {

        this.desc = desc;

        this.stepType = stepType;

        this.reducers = new ArrayList<>();
    }

    public void addReducer(Observable<Action> observable) {
        this.reducers.add(observable);
    }

    public Observable<Action> combinedReducer() {
        return Observable.merge(reducers).first();
    }

    /**
     * @region 私有变量定义
     */

    //本步骤的类型
    private BusinessContext.StepType stepType;

    //本步骤的描述
    private String desc;

    //本步骤包含的所有Reducer
    private List<Observable<Action>> reducers;
}
