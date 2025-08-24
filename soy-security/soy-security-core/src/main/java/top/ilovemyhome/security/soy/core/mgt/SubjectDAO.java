package top.ilovemyhome.security.soy.core.mgt;


import top.ilovemyhome.security.soy.core.subject.Subject;

/**
 * A {@code SubjectDAO} is responsible for persisting a Subject instance's internal state such that the Subject instance
 * can be recreated at a later time if necessary.
 * <p/>
 * Shiro's default {@code SecurityManager} implementations typically use a {@code SubjectDAO} in conjunction
 * with a {@link SubjectFactory}: after the {@code SubjectFactory} creates a {@code Subject} instance, the
 * {@code SubjectDAO} is used to persist that subject's state such that it can be accessed later if necessary.
 * <h3>Usage</h3>
 * It should be noted that this component is used by {@code SecurityManager} implementations to manage Subject
 * state persistence.  It does <em>not</em> make Subject instances accessible to the
 * application (e.g. via {@link org.apache.shiro.SecurityUtils#getSubject() SecurityUtils.getSubject()}).
 *
 * @see DefaultSubjectDAO
 * @since 1.2
 */
public interface SubjectDAO {

    /**
     * Persists the specified Subject's state for later access.  If there is a no existing state persisted, this
     * persists it if possible (i.e. a create operation).  If there is existing state for the specified {@code Subject},
     * this method updates the existing state to reflect the current state (i.e. an update operation).
     *
     * @param subject the Subject instance for which its state will be created or updated.
     * @return the Subject instance to use after persistence is complete.  This can be the same as the method argument
     * if the underlying implementation does not need to make any Subject changes.
     */
    Subject save(Subject subject);

    /**
     * Removes any persisted state for the specified {@code Subject} instance.  This is a delete operation such that
     * the Subject's state will not be accessible at a later time.
     *
     * @param subject the Subject instance for which any persistent state should be deleted.
     */
    void delete(Subject subject);
}
