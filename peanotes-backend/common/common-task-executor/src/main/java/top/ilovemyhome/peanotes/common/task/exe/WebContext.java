package top.ilovemyhome.peanotes.common.task.exe;

import java.net.URI;

public interface WebContext {

    String getFqdn();

    boolean isSslEnabled();
    int getPort();

    String getKeystorePath();

    String getKeystorePassword();

    String getKeyPassword();

    String getContextPath();

    URI getUri();

}
