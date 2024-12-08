package top.ilovemyhome.peanotes.common.task.exe.domain;

import top.ilovemyhome.peanotes.common.task.exe.domain.enums.RegistType;

public record RegistryParam(RegistType registryType, String appName, String address) {

}
