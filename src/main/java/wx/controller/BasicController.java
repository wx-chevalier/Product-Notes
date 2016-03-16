package wx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by apple on 16/3/16.
 */
@RestController
public class BasicController {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
}
