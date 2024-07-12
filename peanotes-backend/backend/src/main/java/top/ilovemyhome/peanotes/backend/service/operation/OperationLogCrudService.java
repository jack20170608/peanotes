package top.ilovemyhome.peanotes.backend.service.operation;

import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;
import top.ilovemyhome.peanotes.backend.dao.operation.OperationLogDao;
import top.ilovemyhome.peanotes.backend.dao.operation.OperationLogSearchCriteria;
import top.ilovemyhome.peanotes.backend.domain.operation.OperationLogEntity;

import java.util.List;

public class OperationLogCrudService {

    public OperationLogCrudService(AppContext appContext) {
        this.operationLogDao = appContext.getBean("operationLogDao", OperationLogDao.class);
    }

    public List<OperationLogEntity> find(OperationLogSearchCriteria searchCriteria){
        return this.operationLogDao.find(searchCriteria);
    }

    public Page<OperationLogEntity> find(OperationLogSearchCriteria searchCriteria, Pageable pageable){
        return this.operationLogDao.find(searchCriteria, pageable);
    }


    private final OperationLogDao operationLogDao;
}
