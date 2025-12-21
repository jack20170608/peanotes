package top.ilovemyhome.benchmark.server.config;

import top.ilovemyhome.commons.muserver.security.core.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserConfig {
    private String id;
    private String name;
    private String displayName;
    private List<String> roles;
    private String passwordHashVal;
    private Map<String, Object> attributes;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setPasswordHashVal(String passwordHashVal) {
        this.passwordHashVal = passwordHashVal;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public String getPasswordHashVal() {
        return passwordHashVal;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public UserConfig() {
    }
    public User toUser() {
        return new User(id, name, displayName, roles, passwordHashVal, attributes);
    }
}
