package top.ilovemyhome.peanotes.backend.common.task.impl;

import org.apache.commons.lang3.StringUtils;
import top.ilovemyhome.peanotes.backend.common.task.TaskOrder;

import java.util.*;

//should none related to business
public class SimpleTaskOrder implements TaskOrder {

    @Override
    public String getKey() {
        return key;
    }

    private static final String KEY_FORMAT = "%s_%s";
    private final String name;
    private final String key;
    private final OrderType orderType;
    private final Map<String, String> otherKeys = new TreeMap<>();

    public SimpleTaskOrder(String name, OrderType orderType, Map<String, String> otherKeys) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(orderType);
        this.name = name;
        if (StringUtils.containsAny(this.name, "_")) {
            throw new IllegalArgumentException("The name should not contain _ character.");
        }
        doValidate(otherKeys);
        this.orderType = orderType;
        if (Objects.nonNull(otherKeys) && !otherKeys.isEmpty()) {
            this.otherKeys.putAll(otherKeys);
        }
        this.key = generateKey();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OrderType getOrderType() {
        return orderType;
    }

    private String generateKey() {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(name.toUpperCase()).append("_")
            .append(orderType.name().toUpperCase()).append("_")
        ;
        otherKeys.forEach((k, v) -> keyBuilder.append(v).append("_"));
        return keyBuilder.substring(0, keyBuilder.length() - 1);
    }

    void doValidate(Map<String, String> otherKeys) {
        if (Objects.nonNull(otherKeys) && !otherKeys.isEmpty()) {
            if (otherKeys.size() > 5){
                throw new IllegalArgumentException("too big nubmer of key.");
            }
            otherKeys.forEach((k, v) -> {
                if (StringUtils.containsAny(v, "-")) {
                    throw new IllegalArgumentException("The value should not contain _ character.");
                }
            });
        }
    }

    @Override
    public Map<String, String> getOtherKeys() {
        return Collections.unmodifiableMap(otherKeys);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleTaskOrder that = (SimpleTaskOrder) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }

    @Override
    public String toString() {
        return "SimpleTaskOrder{" +
            "name='" + name + '\'' +
            ", key='" + key + '\'' +
            ", orderType=" + orderType +
            ", otherKeys=" + otherKeys +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private OrderType orderType;
        private Map<String, String> otherKeys = new TreeMap<>();

        private Builder() {
        }



        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withOrderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public Builder withOtherKeys(Map<String, String> otherKeys) {
            this.otherKeys = otherKeys;
            return this;
        }

        public SimpleTaskOrder build() {
            return new SimpleTaskOrder(name, orderType, otherKeys);
        }
    }
}
