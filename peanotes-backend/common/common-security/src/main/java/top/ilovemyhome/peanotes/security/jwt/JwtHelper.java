package top.ilovemyhome.peanotes.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtHelper {

    public static void printJwtInfo(String jwt){
        String[] jwtParts = jwt.split("\\.");
        LOGGER.info("JWT Headers: {}."
            , new String(Base64.getDecoder().decode(jwtParts[0]), StandardCharsets.UTF_8));
        LOGGER.info("JWT Payload: {}."
            , new String(Base64.getDecoder().decode(jwtParts[1]), StandardCharsets.UTF_8));
        LOGGER.info("JWT Signature: {}.", jwtParts[2]);
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);
}
