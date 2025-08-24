package top.ilovemyhome.peanotes.backend.dao.order;

import org.jdbi.v3.core.Jdbi;
import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.web.dto.common.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class OrderDuckdbDaoImpl extends AbstractOrderDaoImpl {

    public OrderDuckdbDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
                .withName("t_order")
                .withFieldColumnMap(Order.FIELD_COLUMN_MAP)
                .withIdAutoGenerate(false)
                .withIdField(Order.ID_FIELD)
                .withEntityClass(Order.class)
                .build()
            , createDuckDb());
    }

    @Override
    public List<Long> getNextIds(int size) {
        return jdbi.withHandle(h -> h.createQuery("select nextval('seq_t_order_id') from range(1, :size) ")
            .bind("size", size + 1)
            .mapTo(Long.class)
            .list());
    }


    private static Jdbi createDuckDb() {
        try {
            Connection duckDbFileConn = DriverManager.getConnection("jdbc:duckdb:D:\\data\\duckdb\\peanotes");
            return Jdbi.create(duckDbFileConn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
