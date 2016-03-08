package wx.rarf.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class HJSONObject extends JSONObject {

    public HJSONObject(JSONObject parseObject) {
        // TODO Auto-generated constructor stub
        if (parseObject == null) {
            return;
        }

        for (String key : parseObject.keySet()) {
            this.put(key, parseObject.get(key));
        }
    }

    public HJSONObject() {
        // TODO Auto-generated constructor stub
    }

    public String getString(String key, String defaultString) {
        // TODO Auto-generated method stub
        if (super.containsKey(key)) {
            return super.getString(key);
        } else {
            return defaultString;
        }
    }

    /**
     * @param str
     * @return -1 不是JSON格式 0 ﹣ JSONObject 1 ﹣ JSONArray
     */
    public static int getJsonType(String str) {

        if (str == null) {
            return -1;
        }

        //首先判斷是否為JSONObject
        try {
            JSON.parseObject(str);
            return 0;
        } catch (Exception e) {

            try {
                JSON.parseArray(str);
                return 1;
            } catch (Exception e1) {
                return -1;
            }

        }

    }

    /**
     * @param map
     * @return
     * @function 从Map中构造JSONObject
     */
    public static JSONObject fromMap(Map<String, Object> map) {

        JSONObject jsonObject = new JSONObject();

        map.forEach((k, v) -> {
            jsonObject.put(k, v);
        });

        return jsonObject;

    }

    public static void main(String[] args) {
        System.out.println(getJsonType(""));
    }

}
