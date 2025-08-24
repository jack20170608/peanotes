package top.ilovemyhome.security.soy.core.authz;

@FunctionalInterface
public interface Permission {

    boolean implies(Permission p);
}
