package top.ilovemyhome.peanotes.backend.web.security;

import java.util.Objects;

public record LoginInfo(String username, String password) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginInfo loginInfo = (LoginInfo) o;
        return Objects.equals(username, loginInfo.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
            "username='" + username + '\'' +
            ", password='" + "********" + '\'' +
            '}';
    }
}
