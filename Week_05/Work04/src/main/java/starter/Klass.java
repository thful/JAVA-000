package starter;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
@ToString
public class Klass {

    @Autowired
    List<Student> students;

    public void dong(){
        System.out.println(this.getStudents());
    }
}