package top.ilovemyhome.peanotes.backend.common.db.proxy;

import java.lang.reflect.Proxy;

public class JdkProxyTest {

    public static void main(String[] args) {
        System.setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        HelloService target = new HelloServiceImpl();

        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();

        UserServiceInvocationHandler invocationHandler = new UserServiceInvocationHandler(target);

        HelloService proxy = (HelloService) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
        proxy.sayHello("Tom");
        System.out.println("=================");
        proxy.sayHello("haha");
    }
}
