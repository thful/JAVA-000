package starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "school", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SchoolAutoConfiguration {

    @Bean
    public School create() {
        School school = new School();
        return school;
    }

    @Bean(name = "student100")
    public Student createStudent() {
        Student st = new Student();
        st.setId(1);
        st.setName("小明");
        return st;
    }

    @Bean
    public Klass createKlass() {
        Klass klass = new Klass();
        return klass;
    }
}