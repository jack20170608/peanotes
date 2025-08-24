package top.ilovemyhome.peanotes.keepie.server.domain;

import top.ilovemyhome.peanotes.keepie.server.AppContext;

public class EchoSecretSupplier implements SecretSupplier {

    public EchoSecretSupplier(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public String getSecret(String name) {
        return name;
    }

    private final AppContext appContext;
}
