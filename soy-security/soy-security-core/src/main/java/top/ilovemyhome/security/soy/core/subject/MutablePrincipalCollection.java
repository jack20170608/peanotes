package top.ilovemyhome.security.soy.core.subject;

import java.util.Collection;


/**
 * A {@link PrincipalCollection} that allows modification.
 *
 * @since 0.9
 */
public interface MutablePrincipalCollection extends PrincipalCollection {

    /**
     * Adds the given principal to this collection.
     *
     * @param principal the principal to be added.
     * @param realmName the realm this principal came from.
     */
    void add(Object principal, String realmName);

    /**
     * Adds all of the principals in the given collection to this collection.
     *
     * @param principals the principals to be added.
     * @param realmName  the realm these principals came from.
     */
    void addAll(Collection principals, String realmName);

    /**
     * Adds all of the principals from the given principal collection to this collection.
     *
     * @param principals the principals to add.
     */
    void addAll(PrincipalCollection principals);

    /**
     * Removes all Principals in this collection.
     */
    void clear();
}
