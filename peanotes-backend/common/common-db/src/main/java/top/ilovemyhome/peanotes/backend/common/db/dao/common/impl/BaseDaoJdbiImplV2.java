package top.ilovemyhome.peanotes.backend.common.db.dao.common.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.BaseDao;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.helper.*;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BaseDaoJdbiImplV2<T> implements BaseDao<T> {

    public BaseDaoJdbiImplV2(Jdbi jdbi, TableDescription tableDescription) {
        this.jdbi = jdbi;
        this.tableDesc = tableDescription;
        this.clazz = null;
    }

    @Override
    public String getCachedSql(SqlGenerator.SQL_STATEMENT sqlStatementType) {
        return "";
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Long create(T entity) {
        Insert<T> insert =  () -> tableDesc;
        return insert.insert(jdbi, entity);
    }

    @Override
    public int update(Long id, T entity) {
        return 0;
    }

    @Override
    public Iterable<T> save(Iterable<T> entities) {
        return null;
    }

    @Override
    public Optional<T> findOne(Long id) {
        SelectAllByIds<T> select = () -> tableDesc;
        Collection<T> result = select.selectAllByIds(clazz, jdbi, List.of(id));
        return result.isEmpty() ? Optional.empty() : Optional.of(result.iterator().next());
    }

    @Override
    public boolean exists(Long id) {
        return findOne(id).isPresent();
    }

    @Override
    public List<T> find(SearchCriteria searchCriteria) {
        return List.of();
    }

    @Override
    public Page<T> find(SearchCriteria searchCriteria, Pageable pageable) {
        return null;
    }

    @Override
    public List<T> find(String sql, Map<String, Object> params, Map<String, List> listParam) {
        return List.of();
    }

    @Override
    public Long count(SearchCriteria searchCriteria) {
        return 0L;
    }

    @Override
    public Long count(String sql, Map<String, Object> params, Map<String, List> listParam) {
        return 0L;
    }

    @Override
    public List<T> findAll() {
        SelectAll<T> selectAll = () -> tableDesc;
        return List.copyOf(selectAll.selectAll(clazz, jdbi));
    }

    @Override
    public List<T> findAllByIds(List<Long> ids) {
        SelectAllByIds<T> selectAllByIds = () -> tableDesc;
        return List.copyOf(selectAllByIds.selectAllByIds(clazz, jdbi, ids));
    }

    @Override
    public long countAll() {
        CountAll countAll = () -> tableDesc;
        return countAll.countAll(jdbi);
    }

    @Override
    public int delete(Long id) {
        DeleteAll deleteAll = () -> tableDesc;
        return deleteAll.deleteAll(jdbi);
    }

    @Override
    public int delete(List<Long> listOfId) {
        DeleteAllByIds deleteAllByIds = () -> tableDesc;
        return deleteAllByIds.deleteAllByIds(jdbi, listOfId);
    }

    @Override
    public void deleteAll() {
        DeleteAll deleteAll = () -> tableDesc;
        deleteAll.deleteAll(jdbi);
    }

    private final Class<T> clazz;

    protected final TableDescription tableDesc;
    private Jdbi jdbi;
}
