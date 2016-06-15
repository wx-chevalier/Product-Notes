package wx.application.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.experimental.Builder;

import java.util.Map;

/**
 * Created by apple on 16/6/13.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONResponse {

    //返回状态
    String code = StatusCode.SUCCESS;

    //返回的数据信息
    @JsonProperty("data")
    Object data;

    //错误以及描述
    String error;

    //附加的信息
    String message;

    //时间戳信息
    String timeStamp;

    //追踪信息
    String trace;

}
