package wx.rarf.utils;

/**
 * Created by apple on 16/5/1.
 */

/**
 * @function 数据状态码
 */
public interface StatusCode {

    /**
     * 　1xx:信息响应类，表示接收到请求并且继续处理
     * <p>
     * 　　2xx:处理成功响应类，表示动作被成功接收、理解和接受
     * <p>
     * 　　3xx:重定向响应类，为了完成指定的动作，必须接受进一步处理
     * <p>
     * 　　4xx:客户端错误，客户请求包含语法错误或者是不能正确执行
     * <p>
     * 　　5xx:服务端错误，服务器不能正确执行一个正确的请求
     */


    //请求已成功，请求所希望的响应头或数据体将随此响应返回。
    Integer CODE_SUCCESS = 200;

    //表示服务端已经接收到请求,但是因为处理时间过长,需要客户端稍后发送同样请求以获取回复
    Integer CODE_ASYNC = 202;

    //本请求的目标已经被迁移到了另一个URI上
    Integer CODE_DEPRECATED = 303;

    //请求参数错误
    Integer CODE_REQUEST_ERROR = 400;

    //当前请求需要用户认证
    Integer CODE_REQUIRE_AUTH = 401;

    //服务器内部错误
    Integer CODE_SERVER_ERROR = 500;

    //服务器暂时还不支持对于当前请求的处理
    Integer CODE_SERVER_UNSUPPORTED = 501;

}