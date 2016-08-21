package wx.rarf.resource.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by apple on 15/12/18.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Attribute {

    /**
     * @function 属性类型名
     */
    attributeType attributeType() default attributeType.String;

    /**
     * @function 属性的存在性
     */
    attributeExistence attributeExistence() default attributeExistence.Optional;

    /**
     * @function 属性可见性，是否会返回给前端
     */
    attributeVisibility attributeVisibility() default attributeVisibility.Visible;

    //属性类型
    enum attributeType {
        String,
        JSONArray,
        JSONObject
    }

    //属性的存在性，用于从JSONObject构造对象时候
    enum attributeExistence {
        Optional,//可选存在
        Required,// 必须存在
        Ignored // 忽略JSONObject中的赋值
    }

    //属性对于结果的可见性
    enum attributeVisibility {
        Visible,
        Invisible
    }

}
