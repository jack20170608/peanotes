package top.ilovemyhome.security.soy.core.mgt;


import top.ilovemyhome.commons.common.lifecycle.Destroyable;
import top.ilovemyhome.commons.common.lifecycle.LifecycleUtils;
import top.ilovemyhome.security.soy.core.cache.CacheManager;
import top.ilovemyhome.security.soy.core.cache.CacheManagerAware;
import top.ilovemyhome.security.soy.core.event.EventBus;
import top.ilovemyhome.security.soy.core.event.EventBusAware;
import top.ilovemyhome.security.soy.core.event.support.DefaultEventBus;

/**
 * A very basic starting point for the SecurityManager interface that merely provides logging and caching
 * support.  All actual {@code SecurityManager} method implementations are left to subclasses.
 * <p/>
 * <b>Change in 1.0</b> - a default {@code CacheManager} instance is <em>not</em> created by default during
 * instantiation.  As caching strategies can vary greatly depending on an application's needs, a {@code CacheManager}
 * instance must be explicitly configured if caching across the framework is to be enabled.
 *
 * @since 0.9
 */
public abstract class CachingSecurityManager implements SecurityManager, Destroyable, CacheManagerAware, EventBusAware {

    /**
     * The CacheManager to use to perform caching operations to enhance performance.  Can be null.
     */
    private CacheManager cacheManager;

    /**
     * The EventBus to use to use to publish and receive events of interest during Shiro's lifecycle.
     *
     * @since 1.3
     */
    private EventBus eventBus;

    /**
     * Default no-arg constructor that will automatically attempt to initialize a default cacheManager
     */
    public CachingSecurityManager() {
        //use a default event bus:
        setEventBus(new DefaultEventBus());
    }

    /**
     * Returns the CacheManager used by this SecurityManager.
     *
     * @return the cacheManager used by this SecurityManager
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Sets the CacheManager used by this {@code SecurityManager} and potentially any of its
     * children components.
     * <p/>
     * After the cacheManager attribute has been set, the template method
     * {@link #afterCacheManagerSet afterCacheManagerSet()} is executed to allow subclasses to adjust when a
     * cacheManager is available.
     *
     * @param cacheManager the CacheManager used by this {@code SecurityManager} and potentially any of its
     *                     children components.
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        afterCacheManagerSet();
    }

    /**
     * Template callback to notify subclasses that a
     * {@link org.apache.shiro.cache.CacheManager CacheManager} has been set and is available for use via the
     * {@link #getCacheManager getCacheManager()} method.
     */
    protected void afterCacheManagerSet() {
        applyEventBusToCacheManager();
    }

    /**
     * Returns the {@code EventBus} used by this SecurityManager and potentially any of its children components.
     *
     * @return the {@code EventBus} used by this SecurityManager and potentially any of its children components.
     * @since 1.3
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Sets the EventBus used by this {@code SecurityManager} and potentially any of its
     * children components.
     * <p/>
     * After the eventBus attribute has been set, the template method
     * {@link #afterEventBusSet() afterEventBusSet()} is executed to allow subclasses to adjust when a
     * eventBus is available.
     *
     * @param eventBus the EventBus used by this {@code SecurityManager} and potentially any of its
     *                 children components.
     * @since 1.3
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        afterEventBusSet();
    }

    /**
     * @since 1.3
     */
    protected void applyEventBusToCacheManager() {
        if (this.eventBus != null && this.cacheManager != null && this.cacheManager instanceof EventBusAware) {
            ((EventBusAware) this.cacheManager).setEventBus(this.eventBus);
        }
    }

    /**
     * Template callback to notify subclasses that an {@link EventBus EventBus} has been set and is available for use
     * via the {@link #getEventBus() getEventBus()} method.
     *
     * @since 1.3
     */
    protected void afterEventBusSet() {
        applyEventBusToCacheManager();
    }

    /**
     * Destroys the {@link #getCacheManager() cacheManager} via {@link LifecycleUtils#destroy LifecycleUtils.destroy}.
     */
    public void destroy() {
        LifecycleUtils.destroy(getCacheManager());
        this.cacheManager = null;
        LifecycleUtils.destroy(getEventBus());
        this.eventBus = new DefaultEventBus();
    }

}
