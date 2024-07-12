package top.ilovemyhome.peanotes.backend.service.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.text.StrUtils;
import top.ilovemyhome.peanotes.backend.domain.system.SystemParamEntity;
import top.ilovemyhome.peanotes.backend.domain.system.SystemParamEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static top.ilovemyhome.peanotes.backend.common.utils.ParamValidateHelper.notNull;


public class SystemParamFacetService {

    public SystemParamFacetService(AppContext appContext) {
        this.systemParamCrudService = appContext.getBean("systemParamCrudService", SystemParamCrudService.class);
    }

    public void refresh() {
        synchronized (monitor) {
            systemParamCrudService.getAll().forEach(e -> {
                CACHE.put(e.getParamName(), e);
            });
        }
    }

    public SystemParamEntity getValue(SystemParamEnum name) {
        SystemParamEntity param = CACHE.get(name.name());
        notNull(param, "Missing system param with name " + name.name());
        return param;
    }

    public Map<String, String> selectMap(boolean masking, SystemParamEnum [] names) {
        if (Objects.isNull(names) || names.length == 0) {
            return Map.of();
        }
        Map<String, String> result = new HashMap<>(names.length);
        for (SystemParamEnum name : names) {
            SystemParamEntity param = CACHE.get(name.name());
            notNull(param, "Missing system param with name " + name.name());
            String maskedParamValue = param.getParamValue();
            if (masking && name.getMasking()) {
                 maskedParamValue = (StrUtils.hide(param.getParamValue(), 0, Math.min(param.getParamValue().length(), name.getMaskingLength())));
            }
            result.put(name.name(), maskedParamValue);
            if (name.equals(SystemParamEnum.BLOSSOM_OBJECT_STORAGE_DOMAIN)) {
                result.put(name.name(), getDomain());
            }
        }
        return result;
    }

    public String getDomain() {
        return "";
//        return osManager.getDomain();
    }

    private final transient Object monitor = new Object();

    private final SystemParamCrudService systemParamCrudService;
    private static final Map<String, SystemParamEntity> CACHE = new HashMap<>(20);
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemParamFacetService.class);
}
