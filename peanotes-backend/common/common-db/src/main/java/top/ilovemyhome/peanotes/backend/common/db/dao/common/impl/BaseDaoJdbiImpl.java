package top.ilovemyhome.peanotes.backend.common.db.dao.common.impl;

import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.*;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Page;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageImpl;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseDaoJdbiImpl<T> implements BaseDao<T> {

    public abstract void registerRowMappers(Jdbi jdbi);


    @Override
    public Long create(T entity) {
        HandleCallback<Long, RuntimeException> callback = invokeCreate(entity);
        return jdbi.withHandle(callback);
    }

    @Override
    public HandleCallback<Long, RuntimeException> invokeCreate(T entity) {
        String sql = sqlGenerator.create(table);
        LOGGER.info("Create SQL=[{}].", sql);
        return h -> {
            Long result = null;
            Update update = h.createUpdate(sql)
                .bindBean("t", entity);
            if (table.isIdAutoGenerate()) {
                result = update.executeAndReturnGeneratedKeys("id")
                    .mapTo(Long.class)
                    .one();
            } else {
                result = (long) update.execute();
            }
            return result;
        };
    }


    @Override
    public int update(Long id, T entity) {
        String sql = sqlGenerator.updateById(table);
        LOGGER.info("Update SQL=[{}].", sql);
        HandleCallback<Integer, RuntimeException> callback = handle -> handle.createUpdate(sql)
            .bindBean("t", entity)
            .bind("id", id)
            .execute();
        return jdbi.withHandle(callback);
    }

    @Override
    public int update(String sql, Map<String, Object> params) {
        return update(sql, params, null);
    }

    @Override
    public int update(String sql, Map<String, Object> params, Map<String, List> listParam) {
        return update(sql, params, listParam, null);
    }

    @Override
    public int update(final String sql, final Map<String, Object> params, final Map<String, List> listParam, final Map<String, Object> beanParam) {
        LOGGER.info("Update sql=[{}].", sql);
        return jdbi.withHandle(handle -> {
            Update update = handle.createUpdate(sql);
            bindParamsForUpdate(update, params, listParam, beanParam);
            return update.execute();
        });
    }

    @Override
    public int delete(Long id) {
        String deleteByIdSql = Optional.of(sqlCache.get(SqlGenerator.SQL_STATEMENT.deleteById.name()))
            .orElse(sqlGenerator.deleteById(this.table));
        HandleCallback<Integer, RuntimeException> callback = handle -> handle.createUpdate(deleteByIdSql)
            .bind("id", id)
            .execute();
        return jdbi.withHandle(callback);
    }

    @Override
    public int delete(List<Long> listOfId) {
        String deleteByIdsSql = getCachedSql(SqlGenerator.SQL_STATEMENT.deleteByIds);
        HandleCallback<Integer, RuntimeException> callback = handle -> handle.createUpdate(deleteByIdsSql)
            .bind("listOfid", listOfId)
            .execute();
        return jdbi.withHandle(callback);
    }

    @Override
    public int delete(String sql, Map<String, Object> params, Map<String, List> listParam) {
        return update(sql, params, listParam);
    }

    @Override
    public void deleteAll() {
        String deleteAllSql = getCachedSql(SqlGenerator.SQL_STATEMENT.deleteAll);
        jdbi.withHandle(handle -> handle.createUpdate(deleteAllSql)
            .execute());
    }

    @Override
    public Iterable<T> save(Iterable<T> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> findOne(Long id) {
        String selectByIdSql = getCachedSql(SqlGenerator.SQL_STATEMENT.selectById);
        LOGGER.info("FindOne SQL=[{}].", selectByIdSql);
        return (Optional<T>) jdbi.withHandle(handle -> handle.createQuery(selectByIdSql)
            .bind("id", id)
            .mapTo(getEntityType())
            .findOne());
    }

    @Override
    public boolean exists(Long id) {
        return findOne(id).isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> find(String sql, Map<String, Object> params, Map<String, List> listParam) {
        LOGGER.info("Find sql=[{}].", sql);
        return (List<T>) jdbi.withHandle(handle -> {
            Query query = handle.createQuery(sql);
            bindParamsForQuery(query, params, listParam);
            return query.mapTo(getEntityType()).list();
        });
    }

    @Override
    public Page<T> find(SearchCriteria searchCriteria, Pageable page) {
        int total = count(searchCriteria);
        String sql = sqlGenerator.select(table, searchCriteria, page);
        List<T> pagedResult = find(sql, searchCriteria.normalParams(), searchCriteria.listParam());
        return new PageImpl<>(pagedResult, page, total);
    }

    @Override
    public int count(String sql, Map<String, Object> params, Map<String, List> listParam) {
        LOGGER.info("Count sql=[{}].", sql);
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery(sql);
            bindParamsForQuery(query, params, listParam);
            return query.mapTo(Integer.class).one();
        });
    }


    @Override
    public List<T> find(SearchCriteria searchCriteria) {
        String sql = sqlGenerator.select(table, searchCriteria);
        return find(sql, searchCriteria.normalParams(), searchCriteria.listParam());
    }

    @Override
    public List<Long> findIds(SearchCriteria searchCriteria) {
        String sql = String.format("select %s from %s", table.getIdField(), table.getFromClause())
            + searchCriteria.whereClause();
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery(sql);
            bindParamsForQuery(query, searchCriteria.normalParams(), searchCriteria.listParam());
            return query.mapTo(Long.class).list();
        });
    }

    @Override
    public int count(SearchCriteria searchCriteria) {
        String sql = sqlGenerator.count(table, searchCriteria);
        return count(sql, searchCriteria.normalParams(), searchCriteria.listParam());
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        String selectAllSql = getCachedSql(SqlGenerator.SQL_STATEMENT.selectAll);
        return (List<T>) jdbi.withHandle(handle -> handle.createQuery(selectAllSql)
            .mapTo(getEntityType())
            .list()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAllByIds(List<Long> ids) {
        String selectByIdsSql = getCachedSql(SqlGenerator.SQL_STATEMENT.selectByIds);
        return (List<T>) jdbi.withHandle(handle -> handle.createQuery(selectByIdsSql)
            .bindList("listOfid", ids)
            .mapTo(getEntityType())
            .list()
        );
    }


    @Override
    public long countAll() {
        String countAllSql = getCachedSql(SqlGenerator.SQL_STATEMENT.countAll);
        return jdbi.withHandle(handle -> {
            Query query = handle.createQuery(countAllSql);
            return query.mapTo(Long.class).one();
        });
    }


    @Override
    public String getCachedSql(@NotNull SqlGenerator.SQL_STATEMENT sqlStatementType) {
        return Optional.ofNullable(sqlCache.get(sqlStatementType)).orElseGet(() -> {
            String sql = null;
            switch (sqlStatementType) {
                case deleteAll -> sql = sqlGenerator.deleteAll(this.table);
                case deleteById -> sql = sqlGenerator.deleteById(this.table);
                case deleteByIds -> sql = sqlGenerator.deleteByIds(this.table);
                case selectAll -> sql = sqlGenerator.selectAll(this.table);
                case selectById -> sql = sqlGenerator.selectById(this.table);
                case selectByIds ->
                    sql = sqlGenerator.selectByIds(this.table, 2);
                case countAll -> sql = sqlGenerator.count(this.table);
                default ->
                    throw new IllegalArgumentException("Not support sql statement type");
            }
            return sql;
        });
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    private Type getEntityType() {
        return ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private void bindParamsForUpdate(Update update, Map<String, Object> params, Map<String, List> listParam, Map<String, Object> beanParam) {
        if (Objects.nonNull(listParam) && !listParam.isEmpty()) {
            listParam.forEach(update::bindList);
        }
        if (Objects.nonNull(params) && !params.isEmpty()) {
            params.forEach(update::bind);
        }
        if (Objects.nonNull(beanParam) && !beanParam.isEmpty()) {
            beanParam.forEach(update::bindBean);
        }
    }

    private void bindParamsForQuery(Query query, Map<String, Object> params, Map<String, List> listParam) {
        if (Objects.nonNull(listParam) && !listParam.isEmpty()) {
            listParam.forEach(query::bindList);
        }
        if (Objects.nonNull(params) && !params.isEmpty()) {
            params.forEach(query::bind);
        }
    }

    protected BaseDaoJdbiImpl(TableDescription table, Jdbi jdbi) {
        this.table = table;
        this.jdbi = jdbi;
        registerRowMappers(this.jdbi);
        this.sqlGenerator = new SqlGenerator();
    }

    protected final Map<String, String> sqlCache = new ConcurrentHashMap<>(10);

    private final SqlGenerator sqlGenerator;
    protected final TableDescription table;
    protected final Jdbi jdbi;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDaoJdbiImpl.class);


}
