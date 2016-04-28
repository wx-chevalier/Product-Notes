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
import org.springframework.context.ApplicationContext;
import wx.webservice.hessian.server.HessianConfiguration;

import java.util.Arrays;

/**
 * Created by apple on 16/3/8.
 */
@EnableAutoConfiguration
@ImportResource("classpath:spring/applicationContext.xml")
public class Application {

    public static void main(String[] args) throws Exception {

        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();

        Arrays.sort(beanNames);

        //列举出所有装载进来的Bean的名称
        for (String beanName : beanNames) {
            if(!beanName.contains("org.springframework")){
                System.out.println(beanName);
            }
        }

        //列举出关键配置
        //数据库配置
        System.out.println("数据库核心配置为:");

        BasicDataSource basicDataSource = (BasicDataSource) ctx.getBean("dataSource");

        System.out.println(basicDataSource.getUrl());

    }

}