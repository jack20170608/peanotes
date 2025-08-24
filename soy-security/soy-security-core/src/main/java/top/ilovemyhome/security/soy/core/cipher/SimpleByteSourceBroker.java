package top.ilovemyhome.security.soy.core.cipher;


import top.ilovemyhome.commons.common.lang.ByteSource;
import top.ilovemyhome.commons.common.lang.ByteSourceWrapper;
import top.ilovemyhome.commons.common.lifecycle.Destroyable;
import top.ilovemyhome.commons.common.util.ByteUtils;

import java.io.IOException;

/**
 * A simple implementation that maintains cipher service, ciphertext and key for decrypting it later.
 * {@link #useBytes(ByteSourceUser)} guarantees the sensitive data in byte array will be erased at end of use.
 */
public class SimpleByteSourceBroker implements ByteSourceBroker, Destroyable {
    private JcaCipherService cipherService;
    private byte[] ciphertext;
    private byte[] key;
    private boolean destroyed;

    public SimpleByteSourceBroker(JcaCipherService cipherService, byte[] ciphertext, byte[] key) {
        this.cipherService = cipherService;
        this.ciphertext = ciphertext.clone();
        this.key = key.clone();
    }

    public synchronized void useBytes(ByteSourceUser user) {
        if (destroyed || user == null) {
            return;
        }
        ByteSource byteSource = cipherService.decryptInternal(ciphertext, key);

        try (ByteSourceWrapper temp = ByteSourceWrapper.wrap(byteSource.getBytes())) {
            user.use(temp.getBytes());
        } catch (IOException e) {
            // ignore
        }

    }

    public byte[] getClonedBytes() {
        ByteSource byteSource = cipherService.decryptInternal(ciphertext, key);
        // this's a newly created byte array
        return byteSource.getBytes();
    }

    public void destroy() {
        if (!destroyed) {
            synchronized (this) {
                destroyed = true;
                cipherService = null;
                ByteUtils.wipe(ciphertext);
                ciphertext = null;
                ByteUtils.wipe(key);
                key = null;
            }
        }
    }
}
