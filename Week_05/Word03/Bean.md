#### 基本定义
```
public class Student {
    private int id;
    private String name;
}

public class School {
    private String schoolName;
    private String address;
    private Student student;
}
```

#### 第一种：基于AnnotationConfigApplicationContext + @Compone注解获取 bean
```
ApplicationContext context = new AnnotationConfigApplicationContext("com.thful.springbean");
Student stu = (Student) context.getBean("student");
stu.setName("1");
stu.setPhone("2");
System.out.println(stu.toString());
```
```
@Component("student")
public class Student {
    private int id;
    private String name;
}
```

#### 第二种：基于XML获取 bean
```
ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
Student student = (Student) context.getBean("student");
System.out.println(student.toString());
```
```
<bean id="student" class="com.thful.springbean.Student">
    <property name="age" value="31" />
    <property name="name" value="hehe" />
</bean>
<context:component-scan base-package="com.thful.springbean" />
```

#### 第三种：基于AnnotationConfigApplicationContext + @Configuration + @Bean 注解获取 bean
```
ApplicationContext context = new AnnotationConfigApplicationContext("com.thful.springbean");
Student stu = (Student) context.getBean("student");
stu.setName("1");
stu.setPhone("2");
System.out.println(stu.toString());
```
```
@Configuration
@ComponentScan
public class Student {
    @Bean
    private int id;
    @Bean    
    private String name;
}
```
