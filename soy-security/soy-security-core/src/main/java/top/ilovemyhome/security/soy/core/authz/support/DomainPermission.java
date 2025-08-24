package top.ilovemyhome.security.soy.core.authz.support;

import top.ilovemyhome.commons.common.text.StrUtils;

import java.util.Set;

/**
 * Provides a base Permission class from which type-safe/domain-specific subclasses may extend.  Can be used
 * as a base class for JPA/Hibernate persisted permissions that wish to store the parts of the permission string
 * in separate columns (e.g. 'domain', 'actions' and 'targets' columns), which can be used in querying
 * strategies.
 *
 * @since 1.0
 */
public class DomainPermission extends WildcardPermission {

    private static final long serialVersionUID = 1L;

    private String domain;
    private Set<String> actions;
    private Set<String> targets;

    /**
     * Creates a domain permission with *all* actions for *all* targets;
     */
    public DomainPermission() {
        this.domain = getDomain(getClass());
        setParts(getDomain(getClass()));
    }

    public DomainPermission(String actions) {
        domain = getDomain(getClass());
        this.actions = StrUtils.splitToSet(actions, SUBPART_DIVIDER_TOKEN);
        encodeParts(domain, actions, null);
    }

    public DomainPermission(String actions, String targets) {
        this.domain = getDomain(getClass());
        this.actions = StrUtils.splitToSet(actions, SUBPART_DIVIDER_TOKEN);
        this.targets = StrUtils.splitToSet(targets, SUBPART_DIVIDER_TOKEN);
        encodeParts(this.domain, actions, targets);
    }

    protected DomainPermission(Set<String> actions, Set<String> targets) {
        this.domain = getDomain(getClass());
        setParts(domain, actions, targets);
    }

    private void encodeParts(String domain, String actions, String targets) {
        if (!StrUtils.hasText(domain)) {
            throw new IllegalArgumentException("domain argument cannot be null or empty.");
        }
        StringBuilder sb = new StringBuilder(domain);

        if (!StrUtils.hasText(actions)) {
            if (StrUtils.hasText(targets)) {
                sb.append(PART_DIVIDER_TOKEN).append(WILDCARD_TOKEN);
            }
        } else {
            sb.append(PART_DIVIDER_TOKEN).append(actions);
        }
        if (StrUtils.hasText(targets)) {
            sb.append(PART_DIVIDER_TOKEN).append(targets);
        }
        setParts(sb.toString());
    }

    protected void setParts(String domain, Set<String> actions, Set<String> targets) {
        String actionsString = StrUtils.toDelimitedString(actions, SUBPART_DIVIDER_TOKEN);
        String targetsString = StrUtils.toDelimitedString(targets, SUBPART_DIVIDER_TOKEN);
        encodeParts(domain, actionsString, targetsString);
        this.domain = domain;
        this.actions = actions;
        this.targets = targets;
    }

    protected String getDomain(Class<? extends DomainPermission> clazz) {
        String domain = clazz.getSimpleName().toLowerCase();
        //strip any trailing 'permission' text from the name (as all subclasses should have been named):
        int index = domain.lastIndexOf("permission");
        if (index != -1) {
            domain = domain.substring(0, index);
        }
        return domain;
    }

    public String getDomain() {
        return domain;
    }

    protected void setDomain(String domain) {
        if (this.domain != null && this.domain.equals(domain)) {
            return;
        }
        this.domain = domain;
        setParts(domain, actions, targets);
    }

    public Set<String> getActions() {
        return actions;
    }

    protected void setActions(Set<String> actions) {
        if (this.actions != null && this.actions.equals(actions)) {
            return;
        }
        this.actions = actions;
        setParts(domain, actions, targets);
    }

    public Set<String> getTargets() {
        return targets;
    }

    protected void setTargets(Set<String> targets) {
        if (this.targets != null && this.targets.equals(targets)) {
            return;
        }
        this.targets = targets;
        setParts(domain, actions, targets);
    }
}
