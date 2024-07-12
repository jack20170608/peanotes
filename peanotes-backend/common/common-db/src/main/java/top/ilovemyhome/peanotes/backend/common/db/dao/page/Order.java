package top.ilovemyhome.peanotes.backend.common.db.dao.page;


import java.io.Serializable;
import java.util.Objects;

import static top.ilovemyhome.peanotes.backend.common.db.dao.page.Sort.DEFAULT_DIRECTION;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Direction direction;
    private final String property;

    public Order(Direction direction, String property) {
        if (Objects.isNull(property) || property.isEmpty()) {
            throw new IllegalArgumentException("Property must not null or empty!");
        }

        this.direction = direction == null ? DEFAULT_DIRECTION : direction;
        this.property = property;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getProperty() {
        return property;
    }

    public boolean isAscending() {
        return this.direction.equals(Direction.ASC);
    }

    public Order with(Direction order) {
        return new Order(order, this.property);
    }

    public Sort withProperties(String... properties) {
        return new Sort(this.direction, properties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return direction == order.direction && Objects.equals(property, order.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, property);
    }

    @Override
    public String toString() {
        return "Order{" +
            "direction=" + direction +
            ", property='" + property + '\'' +
            '}';
    }
}
