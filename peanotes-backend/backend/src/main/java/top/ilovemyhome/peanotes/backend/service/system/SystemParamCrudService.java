package top.ilovemyhome.peanotes.backend.service.system;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;
import top.ilovemyhome.peanotes.backend.dao.system.SystemParamDao;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.domain.system.SystemParamEntity;

import java.util.*;

public class SystemParamCrudService {

    public SystemParamCrudService(AppContext appContext) {
        this.systemParamDao = appContext.getBean("systemParamDao", SystemParamDao.class);
    }

    public List<SystemParamEntity> getAll(){
        return systemParamDao.findAll();
    }

    public List<SystemParamEntity> getByIds(List<Long> ids){
        return systemParamDao.findAllByIds(ids);
    }

    public Optional<SystemParamEntity> getById(Long id){
        return systemParamDao.findOne(id);
    }

    public Optional<SystemParamEntity> findByParamName(String paramName){
        return this.systemParamDao.find(new SearchCriteria() {
            @Override
            public String whereClause() {
                return "where PARAM_NAME = :paramName ";
            }
            @Override
            public Map<String, Object> normalParams() {
                return Map.of("paramName", paramName);
            }
        }).stream().findFirst();
    }

    public SystemParamEntity put(SystemParamEntity paramEntity) {
        if (Objects.isNull(paramEntity.getId())){
            Long id = systemParamDao.create(paramEntity);
            paramEntity.setId(id);
        }else {
            Long id = paramEntity.getId();
            if (Objects.isNull(id)){
                throw new RuntimeException("Not provide the entity id.");
            }
            systemParamDao.update(id, paramEntity);
        }
        return paramEntity;
    }

    public Page<SystemParamEntity> find(String keyword, Pageable pageable){
        SearchCriteria searchCriteria = new SearchCriteria() {
            @Override
            public String whereClause() {
                return " where param_name like :keyword or param_value like :keyword or param_desc like :keyword";
            }
            @Override
            public Map<String, Object> normalParams() {
                String fuzzyKeyword = keyword;
                if (!StringUtils.startsWith(keyword, "%") && !StringUtils.endsWith(keyword, "%")) {
                    fuzzyKeyword = "%" + fuzzyKeyword + "%";
                }
                return Map.of("keyword", fuzzyKeyword);
            }
        };
        return this.systemParamDao.find(searchCriteria, pageable);
    }

    private final SystemParamDao systemParamDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemParamCrudService.class);
}
