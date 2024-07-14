package top.ilovemyhome.peanotes.backend.common.db.dao.common;

import org.jdbi.v3.core.HandleCallback;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//this interface is refer to @org.springframework.data.repository.CrudRepository
public interface BaseDao<T> {

    String getCachedSql(SqlGenerator.SQL_STATEMENT sqlStatementType);

    Page<T> findAll(Pageable pageable);

    Long create(T entity);

    HandleCallback<Long, RuntimeException> invokeCreate(T entity);

    int update(Long id, T entity);

    int update(String sql, Map<String, Object> params, Map<String, List> listParam);

    Iterable<T> save(Iterable<T> entities);

    Optional<T> findOne(Long id);

    boolean exists(Long id);

    List<T> find(SearchCriteria searchCriteria);

    Page<T> find(SearchCriteria searchCriteria, Pageable pageable);

    List<T> find(String sql, Map<String, Object> params, Map<String, List> listParam);

    Long count(SearchCriteria searchCriteria);
    Long count(String sql, Map<String, Object> params, Map<String, List> listParam);

    List<T> findAll();

    List<T> findAllByIds(List<Long> ids);

    long countAll();

    int delete(Long id);

    int delete(List<Long> listOfId);

    void deleteAll();


}
