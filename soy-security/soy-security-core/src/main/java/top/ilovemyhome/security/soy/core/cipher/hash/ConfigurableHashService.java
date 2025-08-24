package top.ilovemyhome.security.soy.core.cipher.hash;

/**
 * A {@code HashService} that allows configuration of its strategy via JavaBeans-compatible setter methods.
 *
 * @since 1.2
 */
public interface ConfigurableHashService extends HashService {

    /**
     * Sets the name of the key derivation function algorithm that will be used to compute
     * secure hashes for passwords.
     *
     * @param name the name of the key derivation function algorithm that will be used to
     *             compute secure hashes for passwords.
     */
    void setDefaultAlgorithmName(String name);

}
