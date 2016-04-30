package wx.user;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import wx.AbstractTest;

import java.net.URL;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by apple on 16/4/30.
 */
@IntegrationTest({"server.port=0"})
public class LoginControllerIntegrationTest extends AbstractTest {

    @Value("${local.server.port}")
    private int port;

    private URL base;

    private RestTemplate template;


    @Before
    public void setUp() throws Exception {

        this.base = new URL("http://localhost:" + port + "/");

        template = new TestRestTemplate();

    }

    @Test
    public void testLoginWithVerifyCode() throws Exception {

        //构造请求数据
        JSONObject requestData = new JSONObject();

        requestData.put("username", "chevalier");

        requestData.put("password", 123456);

        ResponseEntity<String> response = template.getForEntity(base.toString() + "login/1", String.class);

        System.out.println(response.getBody());

    }
}
