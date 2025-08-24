package top.ilovemyhome.tooling.hosthelper.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.system.OSUtil;
import top.ilovemyhome.peanotes.commons.jdbi.page.Page;
import top.ilovemyhome.peanotes.commons.jdbi.page.impl.PageRequest;
import top.ilovemyhome.tooling.hosthelper.application.AppContext;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchCriteria;
import top.ilovemyhome.tooling.hosthelper.domain.FileSearchResult;
import top.ilovemyhome.tooling.hosthelper.domain.HostItem;
import top.ilovemyhome.tooling.hosthelper.service.FileSearchStrategy;
import top.ilovemyhome.tooling.hosthelper.service.QueryService;

import java.util.List;
import java.util.Map;

public class QueryServiceImpl implements QueryService {

    public QueryServiceImpl(AppContext appContext) {
        this.appContext = appContext;
        this.linuxSearchStrategy = new LinuxSystemSearchStrategy();
        this.javaFileSearchStrategy = new JavaFileSearchStrategy();
        this.dummyFileSearchStrategy = new DummyFileSearchStrategy();
    }


    @Override
    public Map<String, List<HostItem>> getAllHosts() {
        return appContext.getHostItemMap();
    }

    @Override
    public Page<FileSearchResult> search(FileSearchCriteria searchCriteria, PageRequest pageRequest) {
        OSUtil.OSType osType = OSUtil.getOSType();
        logger.info("search criteria: {}, OS type is [{}].", searchCriteria, osType);
        Page<FileSearchResult> results = null;
        switch (osType){
            case LINUX, MAC -> results = linuxSearchStrategy.search(searchCriteria, pageRequest);
            case WINDOWS -> results = javaFileSearchStrategy.search(searchCriteria, pageRequest);
            default -> results = dummyFileSearchStrategy.search(searchCriteria, pageRequest);
        }
        return results;
    }

    private final DummyFileSearchStrategy dummyFileSearchStrategy;
    private final FileSearchStrategy javaFileSearchStrategy;
    private final FileSearchStrategy linuxSearchStrategy;
    private final AppContext appContext;
    private static final Logger logger = LoggerFactory.getLogger(QueryServiceImpl.class);
}
