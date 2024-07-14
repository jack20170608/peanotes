package top.ilovemyhome.peanotes.autotest.dao.impl;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import top.ilovemyhome.peanotes.autotest.dao.KarateRequestDao;
import top.ilovemyhome.peanotes.autotest.domain.KarateRequest;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.utils.ArrayUtils;

import static top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils.toLocalDateTime;
import static top.ilovemyhome.peanotes.backend.common.utils.StringConvertUtils.toStrArray;

public class KarateRequestDaoImpl extends BaseDaoJdbiImpl<KarateRequest> implements KarateRequestDao {

    protected KarateRequestDaoImpl(Jdbi jdbi) {
        super(TableDescription.builder()
            .withIdField(KarateRequest.ID_FIELD)
            .withIdAutoGenerate(true)
            .withFieldColumnMap(KarateRequest.FIELD_COLUMN_MAP)
            .withName("KARATE_REQUEST")
            .build(), jdbi);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(KarateRequest.class, (RowMapper<KarateRequest>) (rs, ctx) -> {
            String [] files = toStrArray(rs.getString(KarateRequest.Field.featureFileNames.getDbColumn()));
            String [] tags = toStrArray(rs.getString(KarateRequest.Field.tags.getDbColumn()));

            return KarateRequest.builder()
                .withId(rs.getLong(KarateRequest.Field.id.getDbColumn()))
                .withName(rs.getString(KarateRequest.Field.name.getDbColumn()))
                .withSequenceNo(rs.getString(KarateRequest.Field.sequenceNo.getDbColumn()))
                .withCreateDt(toLocalDateTime(rs.getTimestamp(KarateRequest.Field.createDt.getDbColumn())))
                .withServiceName(rs.getString(KarateRequest.Field.serviceName.getDbColumn()))
                .withEnv(rs.getString(rs.getString(KarateRequest.Field.env.getDbColumn())))
                .withFeatureFileNames(ArrayUtils.toList(files))
                .withTags(ArrayUtils.toList(tags))
                .build();
        });
    }
}
