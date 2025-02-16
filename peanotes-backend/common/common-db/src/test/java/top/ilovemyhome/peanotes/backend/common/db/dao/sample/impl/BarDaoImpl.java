package top.ilovemyhome.peanotes.backend.common.db.dao.sample.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.RowMapper;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.BarDao;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.argument.StringMapArgumentFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.argument.UUIDArgumentFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.argument.YearMonthArgumentFactory;
import top.ilovemyhome.peanotes.backend.common.db.dao.sample.domain.Bar;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;

import java.lang.reflect.Type;
import java.sql.Types;
import java.time.YearMonth;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        jdbi.registerRowMapper(Bar.class, (RowMapper<Bar>) (rs, ctx) -> new Bar(
            rs.getLong(Bar.Field.id.getDbColumn())
            , rs.getString(Bar.Field.name.getDbColumn())
            , rs.getString(Bar.Field.others.getDbColumn())
            , UUID.fromString(rs.getString(Bar.Field.uuid.getDbColumn()))
            , LocalDateUtils.toYearMonth(rs.getString(Bar.Field.billingMonth.getDbColumn()))
            , JacksonUtil.fromJson(rs.getString(Bar.Field.attributes.getDbColumn()), new TypeReference<>() {})
            , JacksonUtil.fromJson(rs.getString(Bar.Field.intAttributes.getDbColumn()), new TypeReference<>() {})
            ));
        //Register the argument factory
        jdbi.registerArgument(new UUIDArgumentFactory());
        jdbi.registerArgument(new YearMonthArgumentFactory());
        jdbi.registerArgument(new StringMapArgumentFactory());

        jdbi.registerArgument(new AbstractArgumentFactory<Map<String, Integer>>(Types.VARCHAR) {
            @Override
            protected Argument build(Map<String, Integer> value, ConfigRegistry config) {
                return (position, statement, ctx) -> statement.setString(position, JacksonUtil.toJson(value));
            }
        });
    }
}
