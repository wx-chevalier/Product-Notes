package wx.controller.user;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wx.controller.BasicController;
import wx.rarf.context.Action;
import wx.rarf.context.SyncContext;
import wx.rarf.resource.bag.UniResourceBag;
import wx.rarf.utils.ErrorConfig;
import wx.rarf.utils.HJSONObject;
import wx.rarf.utils.throwable.RARFThrowable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import com.google.code.kaptcha.servlet.KaptchaExtend;

/**
 * Created by apple on 16/4/29.
 */
@RestController("loginController")
public class LoginController extends KaptchaExtend {

    @Autowired
    protected HttpServletRequest request;

    @RequestMapping(value = "/captcha.jpg", method = RequestMethod.GET)
    public void captcha(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.captcha(req, resp);
    }

    @RequestMapping("/login/{verifyCode}")
    String login(@PathVariable("verifyCode") String verifyCode) {

        //初始化上下文
        SyncContext syncContext = new SyncContext();

        syncContext

                //判断用户提交的信息是否有效
                .requestHandler((uniResourceBag, action) -> {

                    JSONObject requestData = UniResourceBag.parseRequestData(this.request, "username", "password");

                    return new Action("RequestDataReady")
                            .addActionData("username", requestData.getString("username"))
                            .addActionData("password", requestData.getString("password"));

                })

                .step("请求处理")

                .reducer("判断用户是否存在", (uniResourceBag, action) -> {

                    //判断接收到的类型是否为当前需要处理的类型,如果不是则忽略
                    if (!action.isType("RequestDataReady")) {
                        return new Action();
                    }

                    String username = (String) action.getActionDataOrDefault("username");

                    String password = (String) action.getActionDataOrDefault("password");

                    //如果用户名为chevalier,则认为是存在的
                    if (username.equals("chevalier")) {

                        return new Action("doLogin").addActionData("username", username).addActionData("password", password);

                    } else if (username.equals("error")) {

                        throw new NullPointerException("error");

                    } else {
                        //否则创建新用户
                        return new Action("doRegister").addActionData("username", username).addActionData("password", password);
                    }

                })

                .step("执行登录操作或者创建新用户")

                .reducer("对于已存在用户进行登录操作,返回user_token", (uniResourceBag, action) -> {

                    if (!action.isType("doLogin")) {
                        return new Action();
                    }

                    String username = (String) action.getActionDataOrDefault("username");

                    String password = (String) action.getActionDataOrDefault("password");

                    //将username+password连接作为user_token登录
                    uniResourceBag.setRequestDataWhenSuccess("user_token", UUID.randomUUID());

                    return new Action("Complete");
                })

                .reducer("对于新用户判断用户名是否存在,返回创建好的信息与user_token", (uniResourceBag, action) -> {

                    if (!action.isType("doRegister")) {
                        return new Action();
                    }

                    //返回用户名,用户密码,创建时间和用户Token
                    String username = (String) action.getActionDataOrDefault("username");

                    String password = (String) action.getActionDataOrDefault("password");

                    //将username+password连接作为user_token登录
                    uniResourceBag.setRequestDataWhenSuccess(
                            "username", username,
                            "password", password,
                            "create_time", Instant.now().toEpochMilli(),
                            "user_token", UUID.randomUUID());

                    return new Action("Complete");
                })

                .responseHandler((uniResourceBag, action) -> {

                    //进行最后的处理

                    return new Action();
                });


        return syncContext
                .getUniResourceBag()
                .getResponseString();
    }
}
