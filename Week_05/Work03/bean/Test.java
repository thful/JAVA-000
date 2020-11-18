package bean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath:spring.xml")
public class Test {
    public static void main(String args[]) {
        ConfigurableApplicationContext context = SpringApplication.run(Test.class, args);
        System.out.println("bean:" + context.getBean("bean", Student.class));
        System.out.println("xml配置:" + context.getBean("xml", Student.class));
        System.out.println("annotation:" + context.getBean("annotation", Student.class));
        System.out.println("xml工厂:" + context.getBean("factory", Student.class));
        context.close();
    }
}
