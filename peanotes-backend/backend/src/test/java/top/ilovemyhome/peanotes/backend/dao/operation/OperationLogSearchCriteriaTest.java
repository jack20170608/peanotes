package top.ilovemyhome.peanotes.backend.dao.operation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SqlGenerator;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Order;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Sort;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;
import top.ilovemyhome.peanotes.backend.domain.operation.OperationLogEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OperationLogSearchCriteriaTest {

    final SqlGenerator sqlGenerator = new SqlGenerator();
    static TableDescription operationLogTable = null;
    final static OperationLogSearchCriteria fullSearchCriteria = OperationLogSearchCriteria.builder()
        .withListOfId(List.of(1L, 2L, 3L))
        .withUserId(100L)
        .withMinCreateDt(LocalDateTime.of(2011, 1, 1, 1, 1, 1))
        .withMaxCreateDt(LocalDateTime.of(2033, 3, 3, 3, 3, 3))
        .withUriPrefix("foo/bar")
        .build();
    static OperationLogSearchCriteria emptySearchCriteria = OperationLogSearchCriteria.builder()
        .build();

    @Test
    public void testSearchCriteriaEmpty() {
        assertThat(emptySearchCriteria.whereClause()).isEqualTo(" where 1 = 1  and 1 = 2 ");
        assertThat(emptySearchCriteria.listParam().isEmpty()).isTrue();
        assertThat(emptySearchCriteria.normalParams().isEmpty()).isTrue();
    }

    @Test
    public void testSearchCriteriaFull() {
        assertThat(fullSearchCriteria.whereClause()).isEqualTo(" where 1 = 1  and ID in (<listOfId>) " +
            "and USER_ID = :userId and CREATE_DT >= :minCreateDt " +
            "and CREATE_DT <= :maxCreateDt and URI like :uriPrefix");
        assertThat(fullSearchCriteria.listParam().get("listOfId")).isEqualTo(List.of(1L, 2L, 3L));
        assertThat(fullSearchCriteria.normalParams().get("userId")).isEqualTo(100L);
        assertThat(fullSearchCriteria.normalParams().get("minCreateDt"))
            .isEqualTo(LocalDateTime.of(2011, 1, 1, 1, 1, 1));
        assertThat(fullSearchCriteria.normalParams().get("maxCreateDt"))
            .isEqualTo(LocalDateTime.of(2033, 3, 3, 3, 3, 3));
        assertThat(fullSearchCriteria.normalParams().get("uriPrefix")).isEqualTo("foo/bar%");
    }

    @Test
    public void testSearchCriterial() {
        String select = sqlGenerator.select(operationLogTable, fullSearchCriteria);
        assertThat(select).isEqualTo("SELECT * FROM t_operation_log\n" +
            " where 1 = 1  and ID in (<listOfId>) and USER_ID = :userId " +
            "and CREATE_DT >= :minCreateDt and CREATE_DT <= :maxCreateDt and URI like :uriPrefix");
    }

    @Test
    public void testSearchCriterialPage() {
        String select = sqlGenerator.select(operationLogTable, fullSearchCriteria
            , new PageRequest(2, 10, new Sort(List.of(
                new Order(Direction.ASC, OperationLogEntity.Field.id.getDbColumn())
                , new Order(Direction.DESC, OperationLogEntity.Field.createDt.getDbColumn())
            ))));
        assertThat(select).isEqualTo("SELECT * FROM t_operation_log\n" +
            " where 1 = 1  and ID in (<listOfId>) and USER_ID = :userId " +
            "and CREATE_DT >= :minCreateDt and CREATE_DT <= :maxCreateDt " +
            "and URI like :uriPrefix ORDER BY ID ASC, CREATE_DT DESC LIMIT 10 OFFSET 20");
    }

    @Test
    public void testJsonSerialization(){
        String jsonStr = JacksonUtil.toJson(fullSearchCriteria);
        OperationLogSearchCriteria deSerializationObj = JacksonUtil.fromJson(jsonStr, OperationLogSearchCriteria.class);
        assertThat(deSerializationObj).isEqualTo(fullSearchCriteria);
    }

    @BeforeAll
    public static void generateData() {
        operationLogTable = TableDescription.builder()
            .withName("t_operation_log")
            .withIdField(OperationLogEntity.ID_FIELD)
            .withFieldColumnMap(OperationLogEntity.FIELD_COLUMN_MAP)
            .withIdAutoGenerate(true)
            .build();
        ;
    }
}
