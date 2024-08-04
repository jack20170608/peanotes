package top.ilovemyhome.peanotes.backend.common.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.StringUtils;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.impl.OrderType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//should none related to business
@JsonDeserialize(builder = SimpleTaskOrder.Builder.class)
public class SimpleTaskOrder{

    public String getKey() {
        return key;
    }

    private Long id;
    private final String name;
    private final String key;
    private final OrderType orderType;
    private Map<String, String> otherKeys;
    private Map<String, String> params;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
    private LocalDateTime createDt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
    private LocalDateTime lastUpdateDt;

    public enum Field {
        id("ID", true),
        name("NAME"),
        key("KEY"),
        orderType("ORDER_TYPE"),
        otherKeysInJson("OTHER_KEYS"),
        paramsInJson("PARAMS"),
        createDt("CREATE_DT"),
        lastUpdateDt("LAST_UPDATE_DT")
        ;

        private final String dbColumn;
        private final boolean isId;

        Field(String dbColumn) {
            this.dbColumn = dbColumn;
            this.isId = false;
        }

        Field(String dbColumn, boolean isId) {
            this.dbColumn = dbColumn;
            this.isId = isId;
        }

        public String getDbColumn() {
            return dbColumn;
        }

        public boolean isId() {
            return isId;
        }
    }

    public static final Map<String, String> FIELD_COLUMN_MAP
        = Collections.unmodifiableMap(Stream.of(SimpleTaskOrder.Field.values())
        .collect(Collectors.toMap(SimpleTaskOrder.Field::name, SimpleTaskOrder.Field::getDbColumn)));

    public static final String ID_FIELD = SimpleTaskOrder.Field.id.name();

    private SimpleTaskOrder(Long id, String name, OrderType orderType
        , String key, Map<String, String> otherKeys, Map<String, String> params
        , LocalDateTime createDt, LocalDateTime lastUpdateDt) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(orderType);
        this.id = id;
        this.name = name;
        if (StringUtils.containsAny(this.name, "_")) {
            throw new IllegalArgumentException("The name should not contain _ character.");
        }
        doValidate(otherKeys);
        this.orderType = orderType;
        if (Objects.nonNull(otherKeys) && !otherKeys.isEmpty()) {
            this.otherKeys = new TreeMap<>(String::compareToIgnoreCase);
            this.otherKeys.putAll(otherKeys);
        }
        if (Objects.nonNull(params) && !params.isEmpty()) {
            this.params = new TreeMap<>(String::compareToIgnoreCase);
            this.params.putAll(params);
        }
        LocalDateTime now = LocalDateTime.now();
        this.createDt = Objects.isNull(createDt) ? now : createDt;
        this.lastUpdateDt = Objects.isNull(lastUpdateDt) ? now : lastUpdateDt;
        if (StringUtils.isEmpty(key)) {
            this.key = generateKey();
        } else {
            this.key = key;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

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
            if (otherKeys.size() > 5) {
                throw new IllegalArgumentException("too big number of key.");
            }
            otherKeys.forEach((k, v) -> {
                if (StringUtils.containsAny(v, "-")) {
                    throw new IllegalArgumentException("The value should not contain _ character.");
                }
            });
        }
    }

    public Map<String, String> getOtherKeys() {
        if (Objects.nonNull(otherKeys) && !otherKeys.isEmpty()) {
            return Collections.unmodifiableMap(otherKeys);
        }
        return null;
    }

    @JsonIgnore
    public String getOtherKeysInJson() {
        if (Objects.nonNull(otherKeys) && !otherKeys.isEmpty()) {
            return JacksonUtil.toJson(otherKeys);
        }
        return null;
    }

    @JsonIgnore
    public String getParamsInJson() {
        if (Objects.nonNull(params) && !params.isEmpty()) {
            return JacksonUtil.toJson(params);
        }
        return null;
    }

    public Map<String, String> getParams() {
        if (Objects.nonNull(params) && !params.isEmpty()) {
            return Collections.unmodifiableMap(params);
        }
        return null;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
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
            "id=" + id +
            ", name='" + name + '\'' +
            ", key='" + key + '\'' +
            ", orderType=" + orderType +
            ", otherKeys=" + otherKeys +
            ", params=" + params +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder()
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private Long id;
        private String name;
        private String key;
        private OrderType orderType;
        private Map<String, String> otherKeys;
        private Map<String, String> params;
        private LocalDateTime createDt;
        private LocalDateTime lastUpdateDt;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withKey(String key) {
            this.key = key;
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

        public Builder withParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public SimpleTaskOrder build() {
            return new SimpleTaskOrder(id, name, orderType, key, otherKeys, params, createDt, lastUpdateDt);
        }
    }
}
