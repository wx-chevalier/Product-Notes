package wx.rarf.resource.bag;

import wx.rarf.context.StepSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by apple on 16/4/28.
 */
public class UniResourceBag {

    //请求数据
    Map<String, String> pathVariables = new HashMap();

    //返回数据

    //必须包含的返回数据
    ResponseData responseData;

    public UniResourceBag() {
        this.responseData = new ResponseData();
    }

    public ResponseData getResponseData() {
        return responseData;
    }
}
