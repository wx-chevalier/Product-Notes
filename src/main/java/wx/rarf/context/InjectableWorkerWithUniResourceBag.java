package wx.rarf.context;

import wx.rarf.resource.bag.UniResourceBag;
import wx.rarf.utils.throwable.LFThrowable;

/**
 * Created by apple on 16/4/28.
 */
@FunctionalInterface
public interface InjectableWorkerWithUniResourceBag {
    public Action doWork(UniResourceBag uniResourceBag, Action action) throws Exception;
}