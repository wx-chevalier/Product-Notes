package wx.webservice.hessian.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

/**
 * Created by apple on 16/3/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
public class HessianConfiguration {

    @Autowired
    private HelloServiceImpl helloService;

    @Bean(name = "/HelloService")
    public HessianServiceExporter accountService() {
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(helloService);
        exporter.setServiceInterface(HelloService.class);
        return exporter;
    }
}
