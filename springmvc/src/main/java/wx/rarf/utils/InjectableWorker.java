package wx.rarf.utils;

/**
 * Created by apple on 16/4/28.
 */
@FunctionalInterface
public interface InjectableWorker {
    void doWork() throws Exception;
}