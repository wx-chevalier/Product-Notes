package wx.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.rarf.context.Action;
import wx.rarf.context.SyncContext;
import wx.rarf.utils.ErrorConfig;
import wx.rarf.utils.throwable.RARFThrowable;
import wx.service.user.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by apple on 16/3/16.
 */
@RestController
public class BasicController {

    @Autowired
    protected BasicDataSource basicDataSource;

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected UserService userService;

    @RequestMapping("/")
    private String home() {
        return "Hello World!";
    }

}
