package wx.webservice.hessian.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import wx.webservice.hessian.server.HelloService;

/**
 * Created by apple on 16/3/16.
 */
@Configuration
public class TestConfig {

    @Bean
    public HessianProxyFactoryBean helloClient() {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl("http://localhost:8181/HelloService");
        factory.setServiceInterface(HelloService.class);
        return factory;
    }
}