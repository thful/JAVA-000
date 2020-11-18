package bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean(name = "bean")
    public Student create() {
        Student st = new Student();
        st.setName("bean");
        st.setGender("男");
        return st;
    }
}