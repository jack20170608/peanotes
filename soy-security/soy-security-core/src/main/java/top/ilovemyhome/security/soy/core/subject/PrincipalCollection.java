package top.ilovemyhome.security.soy.core.subject;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PrincipalCollection extends Iterable, Serializable {

    Object getPrimaryPrincipal();


    <T> Collection<T> byType(Class<T> type);


    List asList();


    Set asSet();

    Collection fromRealm(String realmName);


    Set<String> getRealmNames();

    boolean isEmpty();
}
