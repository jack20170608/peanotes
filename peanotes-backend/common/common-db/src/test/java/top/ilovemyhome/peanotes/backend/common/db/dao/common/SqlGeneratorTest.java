package top.ilovemyhome.peanotes.backend.common.db.dao.common;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Direction;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Order;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Pageable;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.Sort;
import top.ilovemyhome.peanotes.backend.common.db.dao.page.impl.PageRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SqlGeneratorTest {

    private final SqlGenerator sqlGenerator = new SqlGenerator();

    static TableDescription oneIdTable;
    static TableDescription xyzTable;
    static TableDescription orderTable;
    static TableDescription.Builder orderTableBuilder;

    @BeforeAll
    public static void initTestData() {
        oneIdTable = TableDescription.builder()
            .withName("t_order")
            .withFieldColumnMap(Map.of("id", "ID"))
            .withIdField("id")
            .build();
        orderTable = TableDescription.builder()
            .withName("t_order")
            .withIdField("id")
            .withFieldColumnMap(Map.of("id", "ID", "name", "NAME", "insertDt", "INSERT_DT", "lastUpdateDt", "LAST_UPDATE_DT"))
            .withIdAutoGenerate(true)
            .build();

        orderTableBuilder = TableDescription.builder()
            .withName("t_order")
            .withIdField("id")
            .withFieldColumnMap(Map.of("id", "ID", "name", "NAME", "insertDt", "INSERT_DT", "lastUpdateDt", "LAST_UPDATE_DT"))
        ;

        xyzTable = TableDescription.builder()
            .withName("xyz")
            .withFieldColumnMap(Map.of("x", "X", "y", "Y", "z", "Z"))
            .withIdField("x")
            .withIdAutoGenerate(true)
            .build();

    }


    @Test
    public void buildSqlForSelectByIdsWhenSingleIdColumnAndNoId() {
        String sql = sqlGenerator.selectByIds(oneIdTable, 0);
        assertThat(sql).isEqualTo("SELECT * FROM t_order");

        sql = sqlGenerator.selectByIds(oneIdTable, 1);
        assertThat(sql).isEqualTo("SELECT * FROM t_order WHERE ID = :id");

        sql = sqlGenerator.selectByIds(oneIdTable, 2);
        assertThat(sql).isEqualTo("SELECT * FROM t_order WHERE ID IN (<listOfid>)");

        sql = sqlGenerator.selectByIds(oneIdTable, 3);
        assertThat(sql).isEqualTo("SELECT * FROM t_order WHERE ID IN (<listOfid>)");
    }


    @Test
    public void buildSqlForDeleteBySingleIdColumn() {
        String sql = sqlGenerator.deleteById(oneIdTable);
        assertThat(sql).isEqualTo("DELETE FROM t_order WHERE ID = :id");
        sql = sqlGenerator.deleteByIds(oneIdTable);
        assertThat(sql).isEqualTo("DELETE FROM t_order WHERE ID IN (<listOfid>)");
    }

    @Test
    public void testCreateWithIdAutogenerate() {
        final String sql = sqlGenerator.create(orderTableBuilder.withIdAutoGenerate(true).build());
        assertThat(sql).isEqualTo("""
            INSERT INTO t_order (
            INSERT_DT, LAST_UPDATE_DT, NAME)
            VALUES (
            :t.insertDt, :t.lastUpdateDt, :t.name);
            """
        );
    }

    @Test
    public void testCreateWithIdAutogenerateFalse() {
        final String sql = sqlGenerator.create(orderTableBuilder.withIdAutoGenerate(false).build());
        assertThat(sql).isEqualTo("""
            INSERT INTO t_order (
            ID, INSERT_DT, LAST_UPDATE_DT, NAME)
            VALUES (
            :t.id, :t.insertDt, :t.lastUpdateDt, :t.name);
            """
        );
    }


    @Test
    public void buildSqlForUpdateWithSingleIdColumn() {
        final String sql = sqlGenerator.updateById(xyzTable);
        assertThat(sql).isEqualTo("UPDATE xyz SET Y = :t.y, Z = :t.z WHERE X = :x");
    }


    @Test
    public void testSearchCriteria() {
        SearchCriteria criteria1 = new FooSearchCriteria();
        String sql = sqlGenerator.select(orderTable, criteria1);
        assertThat(sql).isEqualTo("SELECT * FROM t_order\n" +
            " where 1 =1 and id in <:listOfIds> and foo = :foo  and bar = :bar");

        Pageable pageable = new PageRequest(0, 100);
        sql = sqlGenerator.select(orderTable, criteria1, pageable);
        assertThat(sql).isEqualTo("SELECT * FROM t_order\n" +
            " where 1 =1 and id in <:listOfIds> and foo = :foo  and bar = :bar LIMIT 100 OFFSET 0");

        pageable = new PageRequest(1, 100, Direction.DESC, "ID");
        sql = sqlGenerator.select(orderTable, criteria1, pageable);
        assertThat(sql).isEqualTo("SELECT * FROM t_order\n" +
            " where 1 =1 and id in <:listOfIds> and foo = :foo  and bar = :bar ORDER BY ID DESC LIMIT 100 OFFSET 100");

        Sort sort = new Sort(List.of(new Order(Direction.DESC, "ID"), new Order(Direction.ASC, "NAME")));
        pageable = new PageRequest(2, 100, sort);
        sql = sqlGenerator.select(orderTable, criteria1, pageable);
        assertThat(sql).isEqualTo("SELECT * FROM t_order\n" +
            " where 1 =1 and id in <:listOfIds> and foo = :foo  and bar = :bar ORDER BY ID DESC, NAME ASC LIMIT 100 OFFSET 200");

    }
}
