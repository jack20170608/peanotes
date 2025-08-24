package top.ilovemyhome.peanotes.keepie.server.domain;

@FunctionalInterface
public interface SecretSupplier {
    String getSecret(String name);
}


