package top.ilovemyhome.peanotes.backend.common.db.dao.sample.impl;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.BarDao;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.domain.Bar;

public class BarDaoImpl extends BaseDaoJdbiImpl<Bar> implements BarDao {

    public BarDaoImpl(Jdbi jdbi) {
        super(TableDescription.builder()
            .withIdField(Bar.ID_FIELD)
            .withIdAutoGenerate(true)
            .withFieldColumnMap(Bar.FIELD_COLUMN_MAP)
            .withName("bar")
            .build(), jdbi);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(Bar.class, (RowMapper<Bar>) (rs, ctx) -> new Bar(rs.getLong(Bar.Field.id.getDbColumn())
            , rs.getString(Bar.Field.name.getDbColumn())
            , rs.getString(Bar.Field.others.getDbColumn())));
    }
}
