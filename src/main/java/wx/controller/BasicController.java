package wx.controller;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.rarf.context.Action;
import wx.rarf.context.BusinessContext;
import wx.service.user.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by apple on 16/3/16.
 */
@RestController
public class BasicController {

    @Autowired
    BasicDataSource basicDataSource;


    @Autowired
    UserService userService;

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    @Autowired
    BusinessContext businessContext;

    @RequestMapping("/login")
    String login(HttpServletRequest request) {

        //判断用户提交的信息是否有效

        //判断该用户是否存在

        //若存在判断该用户是否登陆过

        //若不存在则判断用户名是否重名

        //如果不重名则创建新用户并且返回信息

        System.out.print(1);

        businessContext

                .requestHandler((uniResourceBag, action) -> {
                    return new Action();

                })

                .step("请求判断")

                .reducer("判断用户是否存在", (uniResourceBag, action) -> {

                    Thread.sleep(50000l);
                    return new Action();
                })

                .step("执行登录操作或者创建新用户")

                .reducer("对于已存在用户进行登录操作,返回user_tokem", (uniResourceBag, action) -> {
                    return action;
                })

                .reducer("对于新用户判断用户名是否存在,返回创建好的信息与user_token", (uniResourceBag, action) -> {
                    return action;
                })

                .responseHandler((uniResourceBag, action) -> {
                    return null;
                });


        return businessContext.getUniResourceBag().getResponseData().toString();
    }
}
