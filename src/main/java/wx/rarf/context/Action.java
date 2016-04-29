package wx.rarf.context;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 16/4/29.
 */
public class Action {

    public Boolean isValid;

    private String actionType;

    private Map<String, Object> actionData;

    public Action() {
        isValid = false;
    }

    public Action(String actionType, Map actionData) {

        this.isValid = true;

        this.actionType = actionType;

        this.actionData = actionData;
    }

    public Action(String actionType) {
        this(actionType, new HashMap<>());
    }

    public Action addActionData(String key, String value) {

        actionData.put(key, value);

        return this;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Map<String, Object> getActionData() {
        return actionData;
    }

    public void setActionData(Map<String, Object> actionData) {
        this.actionData = actionData;
    }

    public Object getActionDataOrDefault(String key) {

        return actionData.getOrDefault(key, null);
    }


    /**
     * @param type
     * @return
     * @function 判断当前Action类型是否与待比较的类型一致
     */
    public boolean isType(String type) {

        if (isValid) {
            return actionType.equals(type);

        } else {
            return false;
        }
    }

    public JSONObject toJSONObject() {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("actionType", actionType);

        jsonObject.put("actionData", actionData);

        return jsonObject;

    }

}
