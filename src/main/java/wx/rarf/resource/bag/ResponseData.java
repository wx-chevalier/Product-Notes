package wx.rarf.resource.bag;

import com.alibaba.fastjson.JSONObject;
import wx.rarf.utils.throwable.LFThrowable;

/**
 * Created by apple on 16/4/28.
 */
public class ResponseData {

    private Integer code = 0;

    private Integer subCode = 0;

    private String desc = "Success";

    private JSONObject Data;

    public ResponseData() {
        this.Data = new JSONObject();
    }

    public ResponseData(LFThrowable lfThrowable) {

        this();

        this.code = lfThrowable.getCode();

        this.subCode = lfThrowable.getSubCode();

        this.desc = lfThrowable.getDesc();

    }

    public String getJSONString() {

        Data.put("code", code);

        Data.put("subCode", subCode);

        Data.put("desc", desc);

        return Data.toString();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getSubCode() {
        return subCode;
    }

    public void setSubCode(Integer subCode) {
        this.subCode = subCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public JSONObject getResponseData() {
        return Data;
    }

    public JSONObject getData() {
        return Data;
    }

    public void setData(JSONObject responseData) {
        this.Data = responseData;

    }
}
