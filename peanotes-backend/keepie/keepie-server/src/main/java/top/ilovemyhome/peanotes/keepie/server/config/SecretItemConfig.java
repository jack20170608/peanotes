package top.ilovemyhome.peanotes.keepie.server.config;


import java.util.Set;

public final class SecretItemConfig {

    private String name;
    private String supplierName;
    private Set<String> trustedURLs;

    public SecretItemConfig() {
    }

    public SecretItemConfig(String name, String supplierName, Set<String> trustedURLs) {
        this.name = name;
        this.supplierName = supplierName;
        this.trustedURLs = trustedURLs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setTrustedURLs(Set<String> trustedURLs) {
        this.trustedURLs = trustedURLs;
    }

    public String getName() {
        return name;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public Set<String> getTrustedURLs() {
        return trustedURLs;
    }
}
