package wx.user;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wx.AbstractTest;
import wx.controller.BasicController;
import wx.controller.user.LoginController;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by apple on 16/4/29.
 */
public class LoginControllerTest extends AbstractTest {


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;


    @Before
    public void setUp() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testLoginWithVerifyCode() throws Exception {

        //构造请求数据
        JSONObject requestData = new JSONObject();

        requestData.put("username", "chevalier");

        requestData.put("password", 123456);

        mvc.perform(MockMvcRequestBuilders
                .get("/login/123456")
                .param("requestData", requestData.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Greetings from Spring Boot!")));

    }
}
