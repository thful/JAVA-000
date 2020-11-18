package Week_05.work02;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 * 使 Java 里的动态代理，实现一个简单的 AOP 2
 * */
public class Test2 implements InvocationHandler {

    private Object obj;

    public Test2(Object o) {
        this.obj = o;
    }

    public static void main(String[] args) {
        Person eat = new Student();
        Person result = (Person) Proxy.newProxyInstance(eat.getClass().getClassLoader(), eat.getClass().getInterfaces(), new Test2(eat));
        result.eat();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = doBefore();
        Object result = method.invoke(this.obj, args);
        doAfter(startTime);
        return result;
    }

    private void doAfter(long startTime) {
        System.out.println("End--> " + (System.currentTimeMillis() - startTime) + " ms");
    }

    private long doBefore() {
        long l = System.currentTimeMillis();
        System.out.println("Start");
        return l;
    }
}
