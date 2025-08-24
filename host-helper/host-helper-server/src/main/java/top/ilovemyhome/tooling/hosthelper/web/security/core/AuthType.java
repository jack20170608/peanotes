package top.ilovemyhome.tooling.hosthelper.web.security.core;

public enum AuthType {
    BASIC("Basic "),
    BEARER("Bearer "),
    UNKNOWN("");

    private final String prefix;

    AuthType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static AuthType fromHeader(String authHeader) {
        if (authHeader == null) {
            return UNKNOWN;
        }
        for (AuthType type : values()) {
            if (authHeader.startsWith(type.getPrefix())) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
