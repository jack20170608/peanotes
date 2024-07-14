package top.ilovemyhome.peanotes.autotest.dao.impl;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import top.ilovemyhome.peanotes.autotest.dao.KarateExecutionDao;
import top.ilovemyhome.peanotes.autotest.domain.KarateExecution;
import top.ilovemyhome.peanotes.autotest.domain.enums.Status;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toEnum;

public class KarateExecutionDaoImpl extends BaseDaoJdbiImpl<KarateExecution> implements KarateExecutionDao {

    protected KarateExecutionDaoImpl(Jdbi jdbi) {
        super(TableDescription.builder()
            .withIdField(KarateExecution.ID_FIELD)
            .withIdAutoGenerate(true)
            .withFieldColumnMap(KarateExecution.FIELD_COLUMN_MAP)
            .withName("KARATE_EXECUTION")
            .build(), jdbi);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(KarateExecution.class, (RowMapper<KarateExecution>) (rs, ctx) -> {
            return KarateExecution.builder()
                .withId(rs.getLong(KarateExecution.Field.id.getDbColumn()))
                .withKarateRequestId(rs.getLong(KarateExecution.Field.karateRequestId.getDbColumn()))
                .withStatus(toEnum(Status.class, rs.getString(KarateExecution.Field.startDt.getDbColumn())))
                .withReportPath(rs.getString(KarateExecution.Field.reportPath.getDbColumn()))
                .withStartDt(toLocalDateTime(rs.getTimestamp(KarateExecution.Field.startDt.getDbColumn())))
                .withEndDt(toLocalDateTime(rs.getTimestamp(KarateExecution.Field.endDt.getDbColumn())))
                .build();
        });
    }
}
