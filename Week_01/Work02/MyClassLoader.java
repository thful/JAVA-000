import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> tclass = new MyClassLoader().findClass("Hello");
        Method method = tclass.getMethod("hello");
        method.invoke((Object) tclass.newInstance());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File f = new File(this.getClass().getResource("/Hello.xlass").getPath());
        int length = Math.toIntExact(f.length());
        byte[] bytes = new byte[length];
        try {
            new FileInputStream(f).read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return super.findClass(name);
        }
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) (2550 - bytes[i]);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}
