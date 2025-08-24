package top.ilovemyhome.tooling.hosthelper.service;

import top.ilovemyhome.tooling.hosthelper.domain.FileSearchCriteria;

public interface CommandBuilder {

    String build(FileSearchCriteria searchCriteria);
}
