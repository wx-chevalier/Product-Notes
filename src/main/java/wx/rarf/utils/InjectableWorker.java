package wx.rarf.utils;

/**
 * Created by apple on 16/4/28.
 */
@FunctionalInterface
public interface InjectableWorker {
    public void doWork() throws Exception;
}