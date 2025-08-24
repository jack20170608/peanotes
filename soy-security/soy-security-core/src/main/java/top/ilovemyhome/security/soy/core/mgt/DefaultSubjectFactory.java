package top.ilovemyhome.security.soy.core.mgt;


import top.ilovemyhome.security.soy.core.session.Session;
import top.ilovemyhome.security.soy.core.subject.DelegatingSubject;
import top.ilovemyhome.security.soy.core.subject.PrincipalCollection;
import top.ilovemyhome.security.soy.core.subject.Subject;
import top.ilovemyhome.security.soy.core.subject.SubjectContext;

/**
 * Default {@link SubjectFactory SubjectFactory} implementation that creates
 * {@link org.apache.shiro.subject.support.DelegatingSubject DelegatingSubject}
 * instances.
 *
 * @since 1.0
 */
public class DefaultSubjectFactory implements SubjectFactory {

    public DefaultSubjectFactory() {
    }

    public Subject createSubject(SubjectContext context) {
        SecurityManager securityManager = context.resolveSecurityManager();
        Session session = context.resolveSession();
        boolean sessionCreationEnabled = context.isSessionCreationEnabled();
        PrincipalCollection principals = context.resolvePrincipals();
        boolean authenticated = context.resolveAuthenticated();
        String host = context.resolveHost();

        return new DelegatingSubject(principals, authenticated, host, session, sessionCreationEnabled, securityManager);
    }

    /**
     * @deprecated since 1.2 - override {@link #createSubject(org.apache.shiro.subject.SubjectContext)} directly if you
     * need to instantiate a custom {@link Subject} class.
     */
    @Deprecated
    protected Subject newSubjectInstance(PrincipalCollection principals, boolean authenticated, String host,
                                         Session session, SecurityManager securityManager) {
        return new DelegatingSubject(principals, authenticated, host, session, true, securityManager);
    }

}
