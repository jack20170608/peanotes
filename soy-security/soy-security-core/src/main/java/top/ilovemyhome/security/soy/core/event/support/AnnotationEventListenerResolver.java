package top.ilovemyhome.security.soy.core.event.support;


import top.ilovemyhome.commons.common.util.ClassUtils;
import top.ilovemyhome.commons.common.util.CollectionUtil;
import top.ilovemyhome.security.soy.core.event.Subscribe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Inspects an object for annotated methods of interest and creates an {@link EventListener} instance for each method
 * discovered.  An event bus will call the resulting listeners as relevant events arrive.
 * <p/>
 * The default {@link #setAnnotationClass(Class) annotationClass} is {@link Subscribe}, indicating each
 * {@link Subscribe}-annotated method will be represented as an EventListener.
 *
 * @see SingleArgumentMethodEventListener
 * @since 1.3
 */
public class AnnotationEventListenerResolver implements EventListenerResolver {

    private Class<? extends Annotation> annotationClass;

    public AnnotationEventListenerResolver() {
        this.annotationClass = Subscribe.class;
    }

    /**
     * Returns a new collection of {@link EventListener} instances, each instance corresponding to an annotated
     * method discovered on the specified {@code instance} argument.
     *
     * @param instance the instance to inspect for annotated event handler methods.
     * @return a new collection of {@link EventListener} instances, each instance corresponding to an annotated
     * method discovered on the specified {@code instance} argument.
     */
    public List<EventListener> getEventListeners(Object instance) {
        if (instance == null) {
            return Collections.emptyList();
        }

        List<Method> methods = ClassUtils.getAnnotatedMethods(instance.getClass(), getAnnotationClass());
        if (CollectionUtil.isEmpty(methods)) {
            return Collections.emptyList();
        }

        List<EventListener> listeners = new ArrayList<>(methods.size());

        for (Method m : methods) {
            listeners.add(new SingleArgumentMethodEventListener(instance, m));
        }

        return listeners;
    }

    /**
     * Returns the type of annotation that indicates a method that should be represented as an {@link EventListener},
     * defaults to {@link Subscribe}.
     *
     * @return the type of annotation that indicates a method that should be represented as an {@link EventListener},
     * defaults to {@link Subscribe}.
     */
    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    /**
     * Sets the type of annotation that indicates a method that should be represented as an {@link EventListener}.
     * The default value is {@link Subscribe}.
     *
     * @param annotationClass the type of annotation that indicates a method that should be represented as an
     *                        {@link EventListener}.  The default value is {@link Subscribe}.
     */
    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }
}
