package top.ilovemyhome.security.soy.core.event.support;

/**
 * An event listener knows how to accept and process events of a particular type (or types).
 * <p/>
 * Note that this interface is in the event implementation support package (and not the event package directly)
 * because it is a supporting concept for event bus implementations and not something that most application
 * developers using Shiro should implement directly.  App developers should instead use the
 * {@link org.apache.shiro.event.Subscribe Subscribe} annotation on methods they wish to receive events.
 * <p/>
 * This interface therefore mainly represents a 'middle man' between the event bus and the actual subscribing
 * component.  As such, event bus implementers (or framework/infrastructural implementers) or those that wish to
 * customize listener/dispatch functionality might find this concept useful.
 * <p/>
 * It is a concept almost always used in conjunction with a {@link EventListenerResolver} implementation.
 *
 * @see SingleArgumentMethodEventListener
 * @see AnnotationEventListenerResolver
 * @since 1.3
 */
public interface EventListener {

    /**
     * Returns {@code true} if the listener instance can process the specified event object, {@code false} otherwise.
     *
     * @param event the event object to test
     * @return {@code true} if the listener instance can process the specified event object, {@code false} otherwise.
     */
    boolean accepts(Object event);

    /**
     * Handles the specified event.  Again, as this interface is an implementation concept, implementations of this
     * method will likely dispatch the event to a 'real' processor (e.g. method).
     *
     * @param event the event to handle.
     */
    void onEvent(Object event);
}
