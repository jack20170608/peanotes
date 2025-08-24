package top.ilovemyhome.peanotes.keepie.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.text.AntPathMatcher;
import top.ilovemyhome.peanotes.backend.common.text.StrUtils;
import top.ilovemyhome.peanotes.keepie.server.AppContext;
import top.ilovemyhome.peanotes.keepie.server.config.KeepieConfig;
import top.ilovemyhome.peanotes.keepie.server.config.SecretItemConfig;
import top.ilovemyhome.peanotes.keepie.server.domain.EchoSecretSupplier;
import top.ilovemyhome.peanotes.keepie.server.domain.SecretSupplier;
import top.ilovemyhome.peanotes.keepie.server.domain.UppercaseSecretSupplier;

import java.util.HashMap;
import java.util.Map;

public class KeepieServiceImpl implements KeepieService{

    @Override
    public String getSecret(String receiptUrl, String name) {
        //1.0 check the parameter
        if (StrUtils.isEmpty(receiptUrl)) {
            throw new IllegalArgumentException("receiptUrl is empty.");
        }
        SecretItemConfig itemConfig = keepieConfig.getSecretItemConfig(name);
        if (itemConfig == null) {
            throw new IllegalArgumentException("Invalid secret name: " + name);
        }
        //2.0 check the receiptUrl
        boolean matched = false;
        for(String pattern : itemConfig.getTrustedURLs()) {
            if (matcher.match(pattern, receiptUrl)){
                matched = true;
                break;
            }
        }
        if (!matched) {
            throw new IllegalArgumentException("Not a trusted URL: " + receiptUrl);
        }
        //3.0 get the secret
        SecretSupplier supplier = supplierMap.get(itemConfig.getSupplierName());
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown supplier: " + itemConfig.getSupplierName());
        }
        return supplier.getSecret(name);
    }

    public KeepieServiceImpl(AppContext appContext) {
        this.keepieConfig = new KeepieConfig(appContext);
        //All registered Secret supplier
        registerSupplier(appContext);
    }

    private void registerSupplier(AppContext appContext){
        supplierMap.put("top.ilovemyhome.peanotes.keepie.server.domain.EchoSecretSupplier", new EchoSecretSupplier(appContext));
        supplierMap.put("top.ilovemyhome.peanotes.keepie.server.domain.UppercaseSecretSupplier", new UppercaseSecretSupplier(appContext));
    }

    private Map<String, SecretSupplier> supplierMap = new HashMap<>();
    private final AntPathMatcher matcher = new AntPathMatcher();
    private final KeepieConfig keepieConfig;

    private static final Logger logger = LoggerFactory.getLogger(KeepieServiceImpl.class);
}
