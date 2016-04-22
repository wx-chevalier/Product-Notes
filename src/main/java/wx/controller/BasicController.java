package wx.controller;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by apple on 16/3/16.
 */
@RestController
public class BasicController {

    @Autowired
    BasicDataSource basicDataSource;

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
}
