package top.ilovemyhome.security.soy.core.event;

import java.lang.annotation.*;

/**
 * Indicates a method is an event consumer.  The method must have a single argument and the argument's type determines
 * what type of events should be delivered to the method for consumption.
 * <p/>
 * For example:
 * <pre>
 * &#64;Subscribe
 * public void onSomeEvent(SomeEvent event) { ... }
 * </pre>
 * <p/>
 * Because the method argument is declared as a {@code SomeEvent} type, the method will be called by the event
 * dispatcher whenever a {@code SomeEvent} instance (or one of its subclass instances that is not already registered)
 * is published.
 *
 * @since 1.3
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Documented
public @interface Subscribe {
}
