package starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringStarterApplication {

    public static void main(String args[]) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringStarterApplication.class, args);

        System.out.println(context.getBean(School.class));

    }
}