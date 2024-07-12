package top.ilovemyhome.peanotes.backend.common.db.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UserServiceInvocationHandler implements InvocationHandler {

    private Object target;

    public UserServiceInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            System.out.println("do sth. before invocation");
            Object ret = method.invoke(target, args);
            System.out.println("do sth. after invocation");
            return ret;
        } catch (Exception e) {
            System.out.println("do sth. when exception occurs");
            throw e;
        } finally {
            System.out.println("do sth. finally");
        }
    }
}
