package top.ilovemyhome.peanotes.backend.dao.order;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.TableDescription;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.impl.BaseDaoJdbiImpl;
import top.ilovemyhome.peanotes.backend.common.utils.LocalDateUtils;
import top.ilovemyhome.peanotes.backend.web.dto.common.Order;


public abstract class AbstractOrderDaoImpl extends BaseDaoJdbiImpl<Order> implements OrderDao{

    protected AbstractOrderDaoImpl(TableDescription table, Jdbi jdbi) {
        super(table, jdbi);
    }

    @Override
    protected void registerRowMappers(Jdbi jdbi) {
        jdbi.registerRowMapper(Order.class, (RowMapper<Order>) (rs, ctx) -> Order.builder()
            .id(rs.getLong(Order.Field.id.getDbColumn()))
            .sequenceNo(rs.getString(Order.Field.sequenceNo.getDbColumn()))
            .customerId(rs.getInt(Order.Field.customerId.getDbColumn()))
            .productId(rs.getInt(Order.Field.productId.getDbColumn()))
            .valueDate(LocalDateUtils.toLocalDate(rs.getDate(Order.Field.valueDate.getDbColumn())))
            .price(rs.getBigDecimal(Order.Field.price.getDbColumn()))
            .quantity(rs.getInt(Order.Field.quantity.getDbColumn()))
            .value(rs.getBigDecimal(Order.Field.value.getDbColumn()))
            .createDt(LocalDateUtils.toLocalDateTime(rs.getTimestamp(Order.Field.createDt.getDbColumn())))
            .lastUpdateDt(LocalDateUtils.toLocalDateTime(rs.getTimestamp(Order.Field.lastUpdateDt.getDbColumn())))
            .build());
    }

}
