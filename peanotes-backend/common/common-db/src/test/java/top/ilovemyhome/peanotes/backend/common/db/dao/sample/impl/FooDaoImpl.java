package top.ilovemyhome.peanotes.backend.common.db.dao.sample.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.domain.Foo;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.FooDao;


import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDate;
import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;

public class FooDaoImpl extends BaseDaoJdbiImpl<Foo> implements FooDao {

    public FooDaoImpl(Jdbi jdbi) {
        super(TableDescription.builder()
            .withIdAutoGenerate(true)
            .withIdField(Foo.ID_FIELD)
            .withFieldColumnMap(Foo.FIELD_COLUMN_MAP)
            .withName("foo")
            .build(), jdbi);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        this.jdbi.registerRowMapper(Foo.class, (rs, ctx) -> Foo.builder()
            .withId(rs.getLong(Foo.Field.id.getDbColumn()))
            .withFirstName(rs.getString(Foo.Field.firstName.getDbColumn()))
            .withLastName(rs.getString(Foo.Field.lastName.getDbColumn()))
            .withCountry(rs.getString(Foo.Field.country.getDbColumn()))
            .withAge(rs.getInt(Foo.Field.age.getDbColumn()))
            .withBirthDate(toLocalDate(rs.getDate(Foo.Field.birthDate.getDbColumn())))
            .withLastModified(toLocalDateTime(rs.getTimestamp(Foo.Field.lastModified.getDbColumn())))
            .withOthers(rs.getString(Foo.Field.others.getDbColumn()))
            .build());
    }
}
