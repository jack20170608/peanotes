package top.ilovemyhome.security.soy.core.mgt.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.security.soy.core.session.Session;

import java.io.Serializable;
import java.util.Random;

/**
 * Generates session IDs by using a {@link Random} instance to generate random IDs. The default {@code Random}
 * implementation is a {@link java.security.SecureRandom SecureRandom} with the {@code SHA1PRNG} algorithm.
 *
 * @since 1.0
 */
public class RandomSessionIdGenerator implements SessionIdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomSessionIdGenerator.class);

    private static final String RANDOM_NUM_GENERATOR_ALGORITHM_NAME = "SHA1PRNG";
    private Random random;

    public RandomSessionIdGenerator() {
        try {
            this.random = java.security.SecureRandom.getInstance(RANDOM_NUM_GENERATOR_ALGORITHM_NAME);
        } catch (java.security.NoSuchAlgorithmException e) {
            LOGGER.debug("The SecureRandom SHA1PRNG algorithm is not available on the current platform.  Using the "
                    + "platform's default SecureRandom algorithm.", e);
            this.random = new java.security.SecureRandom();
        }
    }

    public Random getRandom() {
        return this.random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Returns the String value of the configured {@link Random}'s {@link Random#nextLong() nextLong()} invocation.
     *
     * @param session the {@link Session} instance to which the ID will be applied.
     * @return the String value of the configured {@link Random}'s {@link Random#nextLong()} invocation.
     */
    public Serializable generateId(Session session) {
        //ignore the argument - just call the Random:
        return Long.toString(getRandom().nextLong());
    }
}
