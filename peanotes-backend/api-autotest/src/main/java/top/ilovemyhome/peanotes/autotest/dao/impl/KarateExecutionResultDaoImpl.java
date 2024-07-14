package top.ilovemyhome.peanotes.autotest.dao.impl;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.autotest.dao.KarateExecutionResultDao;
import top.ilovemyhome.peanotes.autotest.domain.KarateExecutionResult;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;

public class KarateExecutionResultDaoImpl extends BaseDaoJdbiImpl<KarateExecutionResult> implements KarateExecutionResultDao {

    protected KarateExecutionResultDaoImpl(Jdbi jdbi) {
        super(TableDescription.builder()
            .withIdAutoGenerate(true)
            .withIdField(KarateExecutionResult.ID_FIELD)
            .withFieldColumnMap(KarateExecutionResult.FIELD_COLUMN_MAP)
            .withName("KARATE_EXECUTION_RESULT")
            .build(), jdbi);
    }

    @Override
    public void registerRowMappers(Jdbi jdbi) {

    }
}
