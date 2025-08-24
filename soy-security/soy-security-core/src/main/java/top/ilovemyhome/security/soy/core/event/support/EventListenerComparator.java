package top.ilovemyhome.security.soy.core.event.support;

import java.util.Comparator;

/**
 * Compares two event listeners to determine the order in which they should be invoked when an event is dispatched.
 * The lower the order, the sooner it will be invoked (the higher its precedence).  The higher the order, the later
 * it will be invoked (the lower its precedence).
 * <p/>
 * TypedEventListeners have a higher precedence (i.e. a lower order) than standard EventListener instances.  Standard
 * EventListener instances have the same order priority.
 * <p/>
 * When both objects being compared are TypedEventListeners, they are ordered according to the rules of the
 * {@link EventClassComparator}, using the TypedEventListeners'
 * {@link TypedEventListener#getEventType() eventType}.
 *
 * @since 1.3
 */
public class EventListenerComparator implements Comparator<EventListener> {

    //event class comparator is stateless, so we can retain an instance:
    private static final EventClassComparator EVENT_CLASS_COMPARATOR = new EventClassComparator();

    public int compare(EventListener a, EventListener b) {
        if (a == null) {
            if (b == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (b == null) {
            return 1;
        } else if (a == b || a.equals(b)) {
            return 0;
        } else {
            if (a instanceof TypedEventListener) {
                TypedEventListener ta = (TypedEventListener) a;
                if (b instanceof TypedEventListener) {
                    TypedEventListener tb = (TypedEventListener) b;
                    return EVENT_CLASS_COMPARATOR.compare(ta.getEventType(), tb.getEventType());
                } else {
                    //TypedEventListeners are 'less than' (higher priority) than non typed
                    return -1;
                }
            } else {
                if (b instanceof TypedEventListener) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}
