package top.ilovemyhome.security.soy.core.event.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A event listener that invokes a target object's method that accepts a single event argument.
 *
 * @since 1.3
 */
public class SingleArgumentMethodEventListener implements TypedEventListener {

    private final Object target;
    private final Method method;

    public SingleArgumentMethodEventListener(Object target, Method method) {
        this.target = target;
        this.method = method;
        //assert that the method is defined as expected:
        getMethodArgumentType(method);

        assertPublicMethod(method);
    }

    public Object getTarget() {
        return this.target;
    }

    public Method getMethod() {
        return this.method;
    }

    private void assertPublicMethod(Method method) {
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Event handler method [" + method + "] must be public.");
        }
    }

    public boolean accepts(Object event) {
        return event != null && getEventType().isInstance(event);
    }

    public Class getEventType() {
        return getMethodArgumentType(getMethod());
    }

    public void onEvent(Object event) {
        Method method = getMethod();
        try {
            method.invoke(getTarget(), event);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to invoke event handler method [" + method + "].", e);
        }
    }

    protected Class getMethodArgumentType(Method method) {
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            //the default implementation expects a single typed argument and nothing more:
            String msg = "Event handler methods must accept a single argument.";
            throw new IllegalArgumentException(msg);
        }
        return paramTypes[0];
    }
}
