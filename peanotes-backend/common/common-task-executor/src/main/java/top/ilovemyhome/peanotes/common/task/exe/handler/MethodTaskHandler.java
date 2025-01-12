package top.ilovemyhome.peanotes.common.task.exe.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodTaskHandler implements TaskHandler{

    private final Object target;
    private final Method method;
    private final Method initMethod;
    private final Method destroyMethod;

    public MethodTaskHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Override
    public void doHandle() {
        try {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length > 0) {
                method.invoke(target, new Object[paramTypes.length]);       // method-param can not be primitive-types
            } else {
                method.invoke(target);
            }
        }catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        if (initMethod != null) {
            try {
                initMethod.invoke(target);
            }  catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    @Override
    public void destroy() {
        if (destroyMethod != null) {
            try {
                destroyMethod.invoke(target);
            }catch (Throwable t){
                throw new RuntimeException(t);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString()+"["+ target.getClass() + "#" + method.getName() +"]";
    }
}
