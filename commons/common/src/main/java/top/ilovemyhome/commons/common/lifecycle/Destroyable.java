package top.ilovemyhome.commons.common.lifecycle;

/**
 * Shiro container-agnostic interface that indicates that this object requires a callback during destruction.
 *
 * @since 0.2
 */
public interface Destroyable {

    /**
     * Called when this object is being destroyed, allowing any necessary cleanup of internal resources.
     *
     */
    void destroy() ;

}
