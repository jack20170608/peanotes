package top.ilovemyhome.peanotes.security.domain;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TokenStyleTest {

    @Test
    public void testTokenStyleUUID() {
        LOGGER.info(UUID.randomUUID().toString());
    }

    @Test
    public void testTokenStyleSimpleUUID() {
        LOGGER.info(UUID.randomUUID().toString().replaceAll("-", ""));
    }

    @Test
    public void testTokenStyleRandomString() {
        LOGGER.info(RandomStringUtils.random(32, true, true));
        LOGGER.info(RandomStringUtils.random(64, true, true));
        LOGGER.info(RandomStringUtils.random(128, true, true));
    }

    @Test
    public void testTokenStyleRandomUUID() {
        String token = RandomStringUtils.random(2, true, true)
            + "_" + RandomStringUtils.random(14, true, true)
            + "_" + RandomStringUtils.random(16, true, true)
            + "__";
        LOGGER.info(token);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenStyleTest.class);
}
