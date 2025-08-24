package top.ilovemyhome.security.soy.core.cipher.hash;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

/**
 * Hashes used by the Shiro2CryptFormat class.
 *
 * <p>Instead of maintaining them as an {@code Enum}, ServiceLoaders would provide a pluggable alternative.</p>
 *
 * @since 2.0
 */
public final class HashProvider {

    private HashProvider() {
        // utility class
    }

    /**
     * Find a KDF implementation by searching the algorithms.
     *
     * @param algorithmName the algorithmName to match. This is case-sensitive.
     * @return an instance of {@link HashProvider} if found, otherwise {@link Optional#empty()}.
     * @throws NullPointerException if the given parameter algorithmName is {@code null}.
     */
    public static Optional<HashSpi> getByAlgorithmName(String algorithmName) {
        requireNonNull(algorithmName, "algorithmName in HashProvider.getByAlgorithmName");
        ServiceLoader<HashSpi> hashSpis = load();

        return StreamSupport.stream(hashSpis.spliterator(), false)
                .filter(hashSpi -> hashSpi.getImplementedAlgorithms().contains(algorithmName))
                .findAny();
    }

    @SuppressWarnings("unchecked")
    private static ServiceLoader<HashSpi> load() {
        return ServiceLoader.load(HashSpi.class);
    }

}
