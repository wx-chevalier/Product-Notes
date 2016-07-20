package rarf.utils.throwable;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by apple on 15/11/10.
 */
public class LFThrowable extends Throwable {

    /**
     * 类名
     */
    final private String className;

    /**
     * 方法名
     */
    final private String methodName;

    /**
     * Code
     */
    final private int code;

    /**
     * SubCode
     */
    final private Integer subCode;

    /**
     * Desc
     */
    final private String desc;

    public static class Builder {

        /**
         * 类名
         */
        private String className;

        /**
         * 方法名
         */
        private String methodName;

        /**
         * Code
         */
        private int code = -1;

        /**
         * SubCode
         */
        private Integer subCode;

        /**
         * Desc
         */
        private String desc;

        public Builder() {

        }

        public Builder className(String str) {
            this.className = str;
            return this;
        }

        public Builder methodName(String str) {
            this.methodName = str;
            return this;
        }

        public Builder desc(String str) {
            this.desc = str;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder subCode(Integer subCode) {
            this.subCode = subCode;
            return this;
        }

        public LFThrowable build() {
            return new LFThrowable(this);
        }

    }

    private LFThrowable(Builder builder) {

        this.className = builder.className;

        this.methodName = builder.methodName;

        this.code = builder.code;

        this.subCode = builder.subCode;

        this.desc = builder.desc;

    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getCode() {
        return code;
    }

    public Integer getSubCode() {
        return subCode;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * @return
     * @function 转化为JsonString
     */
    public String toJsonString() {

        return this.getJSONObject().toJSONString();

    }

    public JSONObject getJSONObject() {

        JSONObject jsonObject = new JSONObject();

        if (this.code != -1) {
            jsonObject.put("code", code);
        }

        if (this.subCode != null) {
            jsonObject.put("subCode", subCode);
        }

        if (this.desc != null) {
            jsonObject.put("desc", desc);
        }

        return jsonObject;
    }

    /**
     * @param o o仅可以传入String、JSONArray或者JSONObject
     * @return
     * @function 将输入的Object数组依次加入本地的JSONObject中
     */
    public String toJsonString(Object[] o) {

        return this.getJSONObject(o).toJSONString();

    }

    public JSONObject getJSONObject(Object[] o) {

        JSONObject jsonObject = new JSONObject();

        if (this.code != -1) {
            jsonObject.put("code", code);
        }

        if (this.subCode != null) {
            jsonObject.put("subCode", subCode);
        }

        if (this.desc != null) {
            jsonObject.put("desc", desc);
        }

        int len = o.length;

        for (int i = 0; i < len && i + 1 < len; ) {
            //這裡進行容錯，只有存在兩個數字時才加入
            jsonObject.put(String.valueOf(o[i]), o[i + 1]);

            i = i + 2;
        }

        return jsonObject;
    }
}
