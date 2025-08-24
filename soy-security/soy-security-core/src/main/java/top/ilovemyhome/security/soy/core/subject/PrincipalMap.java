package top.ilovemyhome.security.soy.core.subject;

import java.util.Map;

/**
 * EXPERIMENTAL - DO NOT USE YET
 * <p/>
 * A {@code PrincipalMap} is map of all of a subject's principals - its identifying attributes like username, userId,
 * etc.
 * <p/>
 * The {@link Map} methods allow you to interact with a unified representation of
 * all of the Subject's principals, even if they came from different realms.  You can think of the {@code Map} methods
 * as the general purpose API for a Subject's principals.  That is, you can access a principal generally:
 * <pre>
 * Object principal = subject.getPrincipals().get(principalName);
 * </pre>
 * For example, to get the Subject's username (if the
 * username principal indeed exists and was populated by a Realm), you can do the following:
 * <pre>
 * String username = (String)subject.getPrincipals().get("username");
 * </pre>
 * <h3>Multi-Realm Environments</h3>
 * If your application uses multiple realms, the {@code Map} methods reflect
 * the the aggregate principals from <em>all</em> realms that authenticated the owning {@code Subject}.
 * <p/>
 * But in these multi-realm environments, it is often convenient or necessary to acquire only the principals contributed
 * by a specific realm (often in a realm implementation itself).  This {@code PrincipalMap} interface satisfies
 * those needs by providing additional realm-specific accessor/mutator methods.
 *
 * @since 1.2
 */
public interface PrincipalMap extends PrincipalCollection, Map<String, Object> {

    Map<String, Object> getRealmPrincipals(String realmName);

    Map<String, Object> setRealmPrincipals(String realmName, Map<String, Object> principals);

    Object setRealmPrincipal(String realmName, String principalName, Object principal);

    Object getRealmPrincipal(String realmName, String realmPrincipal);

    Object removeRealmPrincipal(String realmName, String principalName);

}
