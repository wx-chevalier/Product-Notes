package wx.application;

import com.squareup.okhttp.OkHttpClient;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wx.webservice.hessian.server.HessianConfiguration;

/**
 * Created by apple on 16/3/8.
 */
@EnableAutoConfiguration
@ImportResource("classpath:spring/applicationContext.xml")
public class Application {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Application.class, args);

    }

}