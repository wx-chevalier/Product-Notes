package wx.rarf.context;

import wx.rarf.resource.bag.UniResourceBag;
import wx.rarf.utils.throwable.RARFThrowable;

/**
 * Created by apple on 16/4/28.
 */
@FunctionalInterface
public interface InjectableWorkerWithUniResourceBag {
    Action doWork(UniResourceBag uniResourceBag, Action action) throws Exception, RARFThrowable;
}