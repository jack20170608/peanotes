package top.ilovemyhome.security.soy.core.subject.support;


import top.ilovemyhome.commons.common.util.CollectionUtil;
import top.ilovemyhome.commons.common.util.MapUtil;
import top.ilovemyhome.security.soy.core.subject.Subject;
import top.ilovemyhome.security.soy.core.mgt.SecurityManager;
import top.ilovemyhome.security.soy.core.util.ThreadContext;

import java.util.Map;

/**
 * Manages thread-state for {@link Subject Subject} access (supporting
 * {@code SecurityUtils.}{@link org.apache.shiro.SecurityUtils#getSubject() getSubject()} calls)
 * during a thread's execution.
 * <p/>
 * The {@link #bind bind} method will bind a {@link Subject} and a
 * {@link org.apache.shiro.mgt.SecurityManager SecurityManager} to the {@link ThreadContext} so they can be retrieved
 * from the {@code ThreadContext} later by any
 * {@code SecurityUtils.}{@link org.apache.shiro.SecurityUtils#getSubject() getSubject()} calls that might occur during
 * the thread's execution.
 *
 * @since 1.0
 */
public class SubjectThreadState implements ThreadState {

    private Map<Object, Object> originalResources;

    private final Subject subject;
    private transient SecurityManager securityManager;

    /**
     * Creates a new {@code SubjectThreadState} that will bind and unbind the specified {@code Subject} to the
     * thread
     *
     * @param subject the {@code Subject} instance to bind and unbind from the {@link ThreadContext}.
     */
    public SubjectThreadState(Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject argument cannot be null.");
        }
        this.subject = subject;

        SecurityManager securityManager = null;
        if (subject instanceof DelegatingSubject) {
            securityManager = ((DelegatingSubject) subject).getSecurityManager();
        }
        if (securityManager == null) {
            securityManager = ThreadContext.getSecurityManager();
        }
        this.securityManager = securityManager;
    }

    /**
     * Returns the {@code Subject} instance managed by this {@code ThreadState} implementation.
     *
     * @return the {@code Subject} instance managed by this {@code ThreadState} implementation.
     */
    protected Subject getSubject() {
        return this.subject;
    }

    /**
     * Binds a {@link Subject} and {@link org.apache.shiro.mgt.SecurityManager SecurityManager} to the
     * {@link ThreadContext} so they can be retrieved later by any
     * {@code SecurityUtils.}{@link org.apache.shiro.SecurityUtils#getSubject() getSubject()} calls that might occur
     * during the thread's execution.
     * <p/>
     * Prior to binding, the {@code ThreadContext}'s existing {@link ThreadContext#getResources() resources} are
     * retained so they can be restored later via the {@link #restore restore} call.
     */
    public void bind() {
        SecurityManager securityManager = this.securityManager;
        if (securityManager == null) {
            //try just in case the constructor didn't find one at the time:
            securityManager = ThreadContext.getSecurityManager();
        }
        this.originalResources = ThreadContext.getResources();
        ThreadContext.remove();

        ThreadContext.bind(this.subject);
        if (securityManager != null) {
            ThreadContext.bind(securityManager);
        }
    }

    /**
     * {@link ThreadContext#remove Remove}s all thread-state that was bound by this instance.  If any previous
     * thread-bound resources existed prior to the {@link #bind bind} call, they are restored back to the
     * {@code ThreadContext} to ensure the thread state is exactly as it was before binding.
     */
    public void restore() {
        ThreadContext.remove();
        if (!MapUtil.isEmpty(this.originalResources)) {
            ThreadContext.setResources(this.originalResources);
        }
    }

    /**
     * Completely {@link ThreadContext#remove removes} the {@code ThreadContext} state.  Typically this method should
     * only be called in special cases - it is more 'correct' to {@link #restore restore} a thread to its previous
     * state than to clear it entirely.
     */
    public void clear() {
        ThreadContext.remove();
    }
}
