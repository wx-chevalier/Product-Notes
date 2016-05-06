package wx.rarf.utils;

/**
 * @author apple
 * @function 配置所有错误信息
 */
public interface ErrorConfig {
    /**
     * 成功
     */
    int CODE_SUCCESS = 0;

    /**
     * region 系统模块相关错误
     */
    /**
     * 系统异常
     */
    int CODE_1000 = -1000;
    /**
     * 请求数据错误
     */
    int CODE_1005 = -1005;
    /**
     * 数据库相关操作失败
     */
    int CODE_1006 = -1006;
    /**
     * 未找到请求信息
     */
    int CODE_1007 = -1007;
    /**
     * 代码内部异常
     **/
    int CODE_INTERNEL_EXCEPTION = -1008;


}
