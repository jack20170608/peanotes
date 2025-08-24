package top.ilovemyhome.dag.server.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.io.ResourceUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static top.ilovemyhome.dag.server.util.SharedConstants.*;


public class SimpleJwtAuthenticatorImpl implements JwtAuthenticator {

    public SimpleJwtAuthenticatorImpl(
        String publicKeyPath
        , String privateKeyPath) {
        this.jwtSignPublicKey = resolvePublicKey(publicKeyPath);
        this.jwtSignPrivateKey = resolvePrivateKey(privateKeyPath);
    }

    @Override
    public Principal authenticate(String token) {
        Principal principal = null;
        try {
            Claims claims = validateToken(token);
            String id = claims.get("id", String.class);
            String name = claims.get("name", String.class);
            String displayName = claims.get("displayName", String.class);
            String roles = claims.get("roles", String.class);
            Set<String> roleSet = roles.isEmpty() ? Set.of() : Set.of(roles.split(","));
            principal = new User(id, name, displayName, roleSet, null, null);
        }catch (Throwable t){
            logger.error("Failed to authenticate user.", t);
        }
        return principal;
    }

    @Override
    public String generateJwt(User user) {
        long expirationTime = System.currentTimeMillis() + 3600000 * 2;
        return Jwts.builder()
            .header()
            .type("JWT")
            .add("alg", "RS256")
            .keyId(UUID.randomUUID().toString())
            .and()
            .issuer(APP_NAME)
            .expiration(new Date(expirationTime))
            .issuedAt(Calendar.getInstance(TimeZone.getDefault()).getTime())
            .subject("HostHelper")
            .claims(Map.of("id", user.id(), "name", user.name()
                , "displayName", user.displayName()
                , "roles", String.join(",", user.roles())))
            .audience()
//            .add(audiences)
            .and()
            .signWith(this.jwtSignPrivateKey)
            .compact()
            ;
    }

    private Claims validateToken(String token) {
        return Jwts.parser()
            .verifyWith(this.jwtSignPublicKey)
            .requireIssuer(APP_NAME)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            ;
    }

    private PrivateKey resolvePrivateKey(String privateKeyPath) {
        try {
            String keyContent = readKeyFromFile(privateKeyPath)
                .stream()
                .filter(line -> !line.startsWith("-----"))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
                .replaceAll("\\s*", "");
            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Throwable t) {
            logger.error("Failed to resolve private key.", t);
            throw new IllegalArgumentException("Failed to resolve private key.", t);
        }
    }

    private PublicKey resolvePublicKey(String publicKeyPath) {
        try {
            String keyContent = readKeyFromFile(publicKeyPath)
                .stream()
                .filter(line -> !line.startsWith("-----"))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
                .replaceAll("\\s*", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Throwable t) {
            logger.error("Failed to resolve private key.", t);
            throw new IllegalArgumentException("Failed to resolve private key.", t);
        }
    }

    private List<String> readKeyFromFile(String keyFilePath) throws Exception {
        List<String> keyContent = null;
        if (keyFilePath.startsWith(CLASSPATH_PREFIX)) {
            keyContent = ResourceUtil.getClasspathResourceAsStringList(StringUtils.substringAfter(keyFilePath, CLASSPATH_PREFIX));
        } else if (keyFilePath.startsWith(FILE_PREFIX)) {
            keyContent = Files.readAllLines(Paths.get(StringUtils.substringAfter(keyFilePath, FILE_PREFIX)));
        } else {
            throw new IllegalArgumentException("Invalid private key path.");
        }
        return keyContent;
    }

    private final transient PrivateKey jwtSignPrivateKey;
    private final transient PublicKey jwtSignPublicKey;


    private static final Logger logger = LoggerFactory.getLogger(SimpleJwtAuthenticatorImpl.class);
}
