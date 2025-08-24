package top.ilovemyhome.tooling.hosthelper.service;

import top.ilovemyhome.peanotes.commons.jdbi.page.Page;
import top.ilovemyhome.peanotes.commons.jdbi.page.impl.PageRequest;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchCriteria;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchResult;

public interface FileSearchStrategy {

    Page<FileSearchResult> search(FileSearchCriteria searchCriteria
        , PageRequest pageRequest);
}
