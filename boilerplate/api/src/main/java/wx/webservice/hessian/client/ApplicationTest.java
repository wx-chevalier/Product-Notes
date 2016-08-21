package wx.webservice.hessian.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import wx.application.Application;
import wx.webservice.hessian.server.Foo;
import wx.webservice.hessian.server.HelloService;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Created by apple on 16/3/16.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfig.class})
public class ApplicationTest {


    @Autowired
    private HelloService helloClient;

    @Test
    public void shouldSayHello() {

        //when
        String message = helloClient.sayHello();

        then(message)
                .isNotEmpty()
                .isEqualTo("Hello World");
    }

    @Test
    public void shouldReceiveFoo() {

        //when
        Foo foo = helloClient.foo();

        then(foo.getName())
                .isEqualTo("foo");
    }
}
