package top.ilovemyhome.tooling.hosthelper.web.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.tooling.hosthelper.util.jwt.JwtHelper;

import java.security.Principal;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleJwtAuthenticatorImplTest {

    private SimpleJwtAuthenticatorImpl authenticator;

    @BeforeEach
    void setUp() {
        authenticator = new SimpleJwtAuthenticatorImpl("classpath:key/public.key", "classpath:key/private.key");
    }

    @Test
    void authenticate_validToken_returnsPrincipal() throws Exception {
        User user = new User("100", "jack","jack ma"
           , Set.of("read", "write"), null, null);
        String token = authenticator.generateJwt(user);
        System.out.println(token);
        JwtHelper.printJwtInfo(token);
        User authUser = (User)authenticator.authenticate(token);
        assertThat(authUser.id()).isEqualTo("100");
        assertThat(authUser.name()).isEqualTo("jack");
        assertThat(authUser.displayName()).isEqualTo("jack ma");
        assertThat(authUser.roles().contains("read")).isTrue();
        assertThat(authUser.roles().contains("write")).isTrue();
    }

    @Test
    void authenticate_invalidToken_throwsNotAuthorized() {
        String invalidToken = "invalid.token.string";
        Principal p = authenticator.authenticate(invalidToken);
        assertThat(p).isNull();
    }

}
