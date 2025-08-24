package top.ilovemyhome.security.soy.core.cipher.core;


import top.ilovemyhome.security.soy.core.exception.SoyException;

/**
 * Base Shiro exception for problems encountered during cryptographic operations.
 *
 * @since 1.0
 */
public class CryptoException extends SoyException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
