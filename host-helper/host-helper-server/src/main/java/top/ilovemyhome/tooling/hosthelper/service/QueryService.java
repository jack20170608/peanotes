package top.ilovemyhome.tooling.hosthelper.service;

import top.ilovemyhome.peanotes.commons.jdbi.page.Page;
import top.ilovemyhome.peanotes.commons.jdbi.page.impl.PageRequest;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchCriteria;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchResult;
import top.ilovemyhome.tooling.hosthelper.domain.HostItem;

import java.util.List;
import java.util.Map;

public interface QueryService {

    Map<String, List<HostItem>> getAllHosts();

    Page<FileSearchResult> search(FileSearchCriteria searchCriteria, PageRequest pageRequest);
}
