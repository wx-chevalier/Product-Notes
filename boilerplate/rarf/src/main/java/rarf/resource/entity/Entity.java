package rarf.resource.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import rarf.resource.annotation.Attribute;
import rarf.utils.throwable.LFThrowable;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by apple on 15/11/16.
 */

/**
 * @function 资源所拥有的实体类
 */
public abstract class Entity {

    //存放该资源的关联附属属性
    public Map<String, JSONArray> resourceIntegration = new HashMap<>();

    public Map<String, JSONObject> resourceIntegrationSingle = new HashMap<>();

    //当前操作者的令牌，一般来说是user_token
    //这里是权限控制的一个入口，一般在修改资源或者删除资源时会用作权限控制
    public String currentOperatorToken = "";

    /**
     * @param jsonObject
     * @return
     * @function 从传入的JsonObject对象中封装出当前资源
     */
    public abstract boolean wrapper(JSONObject jsonObject) throws LFThrowable;

    /**
     * @param jsonObject
     * @param userId
     * @throws LFThrowable
     * @function 根据当前操作者创建一个操作令牌
     */
    protected void wrapperWithCurrentOperatorToken(JSONObject jsonObject, String userId) throws LFThrowable {

        this.wrapper(jsonObject);

        this.currentOperatorToken = userId;

    }

    /**
     * @param jsonObject
     * @function 將jsonObject內容利用反射方法填充到實例中
     */
    protected boolean wrapperReflect(JSONObject jsonObject) {

        for (Field f : this.getClass().getDeclaredFields()) {

            f.setAccessible(true);

            //获取该域的注解，首先判断是否为忽略类型，如果忽略类型则直接跳过，然后判断是否为必须类型，如果是必须类型但是没有，则直接报错
            if (f.isAnnotationPresent(Attribute.class)) {
                Attribute attributeAnnotation = f.getAnnotation(Attribute.class);
                //首先判断是否為忽略
                if (attributeAnnotation.attributeExistence().equals(Attribute.attributeExistence.Ignored)) {
                    continue;
                }

                if (attributeAnnotation.attributeExistence().equals(Attribute.attributeExistence.Required)) {
                    if (!jsonObject.containsKey(f.getName())) {
                        return false;
                    }
                }
            }


            if (jsonObject.containsKey(f.getName().split(".Entity")[0])) {

                String name = f.getType().getName();

                //如果是字符串类型的
                if (f.getType().getName().equals(String.class.getName())) {
                    try {
                        f.set(this, String.valueOf(jsonObject.get(f.getName())));
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                    continue;
                }

                //如果是数值类型的
                if (f.getType().getName().equals(Integer.class.getName())) {
                    try {
                        f.set(this, Integer.valueOf(String.valueOf(jsonObject.get(f.getName()))));
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                    continue;
                }

                //如果是时间类型的
                if (f.getType().getName().equals(LocalDateTime.class.getName())) {
                    try {
                        //判断获得的是时间戳还是时间表达式
                        String str = String.valueOf(jsonObject.get(f.getName()));

                        try {
                            //如果解析為Long類型成功
                            Long timeStamp = Long.valueOf(str);
                            f.set(this, LocalDateTime.ofInstant(Instant.ofEpochSecond(timeStamp), TimeZone.getDefault().toZoneId()));

                        } catch (Exception e) {

                            //否则就是String类型
                            f.set(this, LocalDateTime.parse(str));

                        }

                    } catch (IllegalAccessException e) {
                        continue;
                    }
                    continue;
                }

                //如果对应的域是LocalDateTime
                if (f.getType().getName().equals(Instant.class.getName())) {

                    try {
                        //判断获得的是时间戳还是时间表达式
                        String str = String.valueOf(jsonObject.get(f.getName()));

                        try {
                            //如果解析為Long類型成功
                            Long timeStamp = Long.valueOf(str);
                            f.set(this, Instant.ofEpochSecond(timeStamp));

                        } catch (Exception e) {

                            //否则就是String类型
                            f.set(this, LocalDateTime.parse(str).atZone(TimeZone.getDefault().toZoneId()).toInstant());
                        }

                    } catch (IllegalAccessException e) {
                        continue;
                    }
                    continue;

                }

                //如果对应的域是JSONArray
                if (f.getType().getName().equals(JSONArray.class.getName())) {

                    try {

                        //判斷類型
                        Object object = jsonObject.get(f.getName());

                        if (object instanceof String) {
                            f.set(this, JSONArray.parse(String.valueOf(jsonObject.get(f.getName()))));

                        } else if (object instanceof JSONArray) {
                            f.set(this, object);
                        }

                    } catch (IllegalAccessException e) {
                        continue;
                    }
                    continue;

                }

                //如果对应的域是JSONObject
                if (f.getType().getName().equals(JSONObject.class.getName())) {

                    try {

                        //判斷類型
                        Object object = jsonObject.get(f.getName());

                        if (object instanceof String) {

                            f.set(this, JSONObject.parse(String.valueOf(jsonObject.get(f.getName()))));

                        } else if (object instanceof JSONObject) {
                            f.set(this, object);
                        }

                        f.set(this, JSONObject.parse(String.valueOf(jsonObject.get(f.getName()))));
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                    continue;

                }
            }
        }
        return true;

    }

    /**
     * @return
     * @function 将本实体类转化为JsonString
     */

    public abstract String toJsonString();

    /**
     * @return
     * @function 利用反射的方法，自動將子類的參數填充到JsonObject中
     */
    protected JSONObject toJsonStringAuto() {

        JSONObject jsonObject = new JSONObject();

        Class<?> c = this.getClass();

        Field[] fs = c.getDeclaredFields();

        //遍历当前域
        for (Field f : c.getDeclaredFields()) {

            try {
                f.setAccessible(true);

                //如果是两个需要依赖注入的则略过
                if (f.getName().equals("resourceIntegration") || f.getName().equals("resourceIntegrationSingle")) {
                    continue;
                }

                //先将所有类型引入
                //如果是其他不需要进行转化的类型
                f.setAccessible(true);
                //將獲取到的值填入到jsonObject中
                jsonObject.put(f.getName(), f.get(this));

                //如果是时间戳类型，则取Long值
                if (f.getType().getName().equals(Instant.class.getName())) {
                    //转化为时间戳
                    Instant instant = (Instant) f.get(this);

                    if (instant == null) {
                        instant = Instant.now();
                    }
                    jsonObject.put(f.getName(), instant.getEpochSecond());
                }

                //如果是本地时间类型
                if (f.getType().getName().equals(LocalDateTime.class.getName())) {
                    //如果是LocalDateTime类型，则转化为时间戳
                    //转化为时间戳
                    LocalDateTime localDateTime = (LocalDateTime) f.get(this);

                    if (localDateTime == null) {
                        localDateTime = LocalDateTime.now();
                    }

                    jsonObject.put(f.getName(), localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant().getEpochSecond());

                }

                if (f.isAnnotationPresent(Attribute.class)) {
                    Attribute attributeAnnotation = f.getAnnotation(Attribute.class);
                    //首先判断是否可见
                    if (attributeAnnotation.attributeVisibility().equals(Attribute.attributeVisibility.Invisible)) {
                        //如果是不可见，则直接移除
                        jsonObject.remove(f.getName());
                        continue;
                    }

                    //判断是否需要转化为JSONArray或者JSONObject
                    if (attributeAnnotation.attributeType().equals(Attribute.attributeType.JSONArray)) {

                        try {
                            jsonObject.put(f.getName(), JSONArray.parse(String.valueOf(f.get(this))));

                        } catch (Exception e) {

                            JSONArray jsonArray = new JSONArray();

                            jsonArray.add(String.valueOf(f.get(this)));

                            //如果解析失败，直接设置空数组
                            jsonObject.put(f.getName(), jsonArray);
                        }


                    }

                    if (attributeAnnotation.attributeType().equals(Attribute.attributeType.JSONObject)) {

                        jsonObject.put(f.getName(), JSONObject.parse(String.valueOf(f.get(this))));

                    }
                }


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //遍历当前的待整合的单个资源
        resourceIntegrationSingle.forEach((s, jsonObject1) -> {
            jsonObject.put(s, jsonObject1);
        });

        //遍历当前的待整合资源
        resourceIntegration.forEach((s, jsonArray) -> {
            jsonObject.put(s, jsonArray);
        });

        return jsonObject;

    }

    /**
     * @param list
     * @return
     * @function 將List中的資源轉化為JSONArray
     */
    public static JSONArray toJsonArrayFromList(List<? extends Entity> list) {

        JSONArray jsonArray = new JSONArray();

        list.forEach(entity -> {
            jsonArray.add(JSON.parseObject(entity.toJsonString()));
        });

        return jsonArray;

    }

    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return this.toJsonStringAuto().toJSONString();
    }


}
