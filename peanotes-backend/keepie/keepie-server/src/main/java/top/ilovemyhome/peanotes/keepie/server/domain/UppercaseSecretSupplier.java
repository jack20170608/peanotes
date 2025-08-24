package top.ilovemyhome.peanotes.keepie.server.domain;

import top.ilovemyhome.peanotes.keepie.server.AppContext;

import java.util.Objects;

public class UppercaseSecretSupplier implements SecretSupplier {

    public UppercaseSecretSupplier(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public String getSecret(String name) {
        return Objects.isNull(name) ? null : name.toUpperCase();
    }

    private final AppContext appContext;

}
