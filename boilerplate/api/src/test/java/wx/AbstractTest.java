package wx;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wx.application.Application;

/**
 * Created by apple on 16/4/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@SpringApplicationConfiguration(classes = {Application.class, MockServletContext.class})
@ImportResource("classpath:common/resources/spring/applicationContext.xml")
public class AbstractTest {
}
