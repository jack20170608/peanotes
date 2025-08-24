package top.ilovemyhome.peanotes.backend.dao.order;

import top.ilovemyhome.peanotes.backend.application.AppContext;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.web.dto.common.Order;

import java.util.List;

public class OrderPostgresDaoImpl extends AbstractOrderDaoImpl {

    public OrderPostgresDaoImpl(AppContext appContext) {
        super(TableDescription.builder()
                .withName("t_order")
                .withFieldColumnMap(Order.FIELD_COLUMN_MAP)
                .withIdAutoGenerate(false)
                .withIdField(Order.ID_FIELD)
                .withEntityClass(Order.class)
                .build()
            , appContext.getDataSourceFactory().getJdbi());
    }

    @Override
    public List<Long> getNextIds(int size) {
        return jdbi.withHandle(h -> h.createQuery("select nextval('seq_t_order_id') from range(1, :size) ")
            .bind("size", size + 1)
            .mapTo(Long.class)
            .list());
    }
}
