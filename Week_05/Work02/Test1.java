import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 * 使 Java 里的动态代理，实现一个简单的 AOP
 * */
public class Test1 {
    public static void main(String[] args) {
        // 1.最基本的AOP
        Student student = new Student();
        Person proxy = (Person) Proxy.newProxyInstance(Student.class.getClassLoader(), student.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("Start...");
                Object res = null;
                try {
                    res = method.invoke(student, args);
                    System.out.println("run...");
                } catch (Exception e) {
                    System.out.println("error...");
                }
                return res;
            }
        });
        proxy.eat();
    }
}
