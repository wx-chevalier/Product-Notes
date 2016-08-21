package wx.rarf.resource.bag;

import com.alibaba.fastjson.JSONObject;
import wx.rarf.utils.ErrorConfig;
import wx.rarf.utils.HJSONObject;
import wx.rarf.utils.ResponseData;
import wx.rarf.utils.throwable.RARFThrowable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by apple on 16/4/28.
 */
public class UniResourceBag {

    //请求数据中的路径变量
    Map<String, String> pathVariables;

    //请求数据中的requestData中的内容
    JSONObject requestData;

    //必须包含的返回数据
    ResponseData responseData;

    //调试信息
    String stackTrace;

    public JSONObject getRuntimeLog() {
        return runtimeLog;
    }

    public void setRuntimeLog(JSONObject runtimeLog) {
        this.runtimeLog = runtimeLog;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    //运行时信息
    JSONObject runtimeLog;

    public String getResponseString() {

        this.responseData.getData().put("stackTrace", stackTrace);

        this.responseData.getData().put("runtimeLog", runtimeLog);

        return responseData.getJSONString();
    }

    public UniResourceBag() {
        this.responseData = new ResponseData();
    }

    public UniResourceBag(JSONObject requestData) {

        this();

        this.requestData = requestData;

    }


    public UniResourceBag(JSONObject requestData, Map pathVariables) {

        this(requestData);

        this.pathVariables = pathVariables;

    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(RARFThrowable RARFThrowable) {

        this.responseData = new ResponseData(RARFThrowable);

    }

    public void setRequestDataWhenSuccess(JSONObject jsonObject) {


        this.responseData.setData(jsonObject);

    }

    public void setRequestDataWhenSuccess(Map<String, String> mapData) {

        JSONObject jsonObject = new JSONObject();

        mapData.forEach((s, s2) -> {
            jsonObject.put(s, s2);
        });

        this.setRequestDataWhenSuccess(jsonObject);

    }

    public void setRequestDataWhenSuccess(Object... objectData) {

        int i = 0;

        JSONObject jsonObject = new JSONObject();

        while (i < objectData.length && i + 1 < objectData.length) {

            jsonObject.put(String.valueOf(objectData[i]), objectData[i + 1]);

            i = i + 2;
        }

        this.setRequestDataWhenSuccess(jsonObject);

    }


    public static JSONObject parseRequestData(HttpServletRequest httpServletRequest, String... mustKeys) throws RARFThrowable {

        try {

            JSONObject requestData;

            //首先判断requestMap中是否存在
            if (httpServletRequest.getParameterMap().containsKey("requestData")) {

                requestData = HJSONObject.parseObject(String.valueOf(httpServletRequest.getParameterMap().get("requestData")[0]));

            } else {
                requestData = HJSONObject.parseObject(((String[]) httpServletRequest.getAttribute("requestData"))[0]);

            }

            //从request中获取请求数据

            //判断请求参数是否缺失,如果缺失则抛出异常
            if (requestData == null) {
                throw new RARFThrowable.Builder().code(ErrorConfig.CODE_1005).desc("请求参数缺失").build();
            }

            for (String key : mustKeys) {
                if (!requestData.containsKey(key)) {
                    throw new RARFThrowable.Builder().code(ErrorConfig.CODE_1005).desc("请求参数缺失").build();

                }
            }

            return requestData;

        } catch (Exception e) {
            throw new RARFThrowable.Builder().code(ErrorConfig.CODE_1005).desc("请求数据格式错误").build();
        }


    }
}
