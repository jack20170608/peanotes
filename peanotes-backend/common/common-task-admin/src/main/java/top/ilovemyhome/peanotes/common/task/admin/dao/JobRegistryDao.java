package top.ilovemyhome.peanotes.common.task.admin.dao;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.common.task.admin.domain.JobRegistry;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRegistryDao extends BaseDao<JobRegistry> {

    List<Long> findDead(LocalDateTime minLastUpdateDt);

    List<JobRegistry> findAll(LocalDateTime minLastUpdateDt);

    Long registrySave(LocalDateTime lastUpdateDt, String registryGroup, String registryKey, String registryValue);

    int registryUpdate(LocalDateTime lastUpdateDt, String registryGroup, String registryKey, String registryValue);

    int registryDelete(String registryGroup, String registryKey, String registryValue);
}
