package top.ilovemyhome.peanotes.backend.web.dto.common;

import com.google.common.collect.ImmutableMap;
import top.ilovemyhome.peanotes.backend.common.number.DecimalUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Order(Long id
    , String sequenceNo
    , Integer customerId
    , Integer productId
    , LocalDate valueDate
    , BigDecimal price
    , int quantity
    , BigDecimal value
    , LocalDateTime createDt
    , LocalDateTime lastUpdateDt) {

    public enum Field {
        id("id", true),
        sequenceNo("sequence_no"),
        customerId("customer_id"),
        productId("product_id"),
        valueDate("value_date"),
        price("price"),
        quantity("quantity"),
        value("value"),
        createDt("create_dt"),
        lastUpdateDt("last_update_dt")
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

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(Field.values())
        .collect(Collectors.toMap(Field::name, Field::getDbColumn)));

    public static final String ID_FIELD = Stream.of(Field.values())
        .filter(f -> f.isId)
        .map(Enum::name)
        .findFirst().orElse(null);

    public static Stream<Order> randomObj(int size) {
        if (size < 1) {
            return null;
        }
        if (size > 10240) {
            throw new IllegalStateException("size too large!");
        }
        RandomGenerator randomGenerator = RandomGeneratorFactory.of("Random").create();

        List<Integer> customerIds = randomGenerator.ints(size, 1, 1000).boxed().toList();
        List<Long> dateDeltas = randomGenerator.longs(size, -365 * 3, 30).boxed().toList();
        List<Integer> randomProductIds = randomGenerator.ints(size, 1, 100000).boxed().toList();
        List<BigDecimal> prices = randomGenerator.longs(size, 100, 100000).boxed()
            .map(l -> DecimalUtils.div(BigDecimal.valueOf(l), new BigDecimal(100), 2)).toList();
        List<Integer> quantities = randomGenerator.ints(size, 1, 3000).boxed().toList();

        List<BigDecimal> values = IntStream.range(0, size).boxed()
            .map(idx -> DecimalUtils.mul(prices.get(idx), new BigDecimal(quantities.get(idx)), 2)).toList();
        List<Long> createDtDeltas = randomGenerator.longs(size, -60 * 60 * 24 * 365 * 3, 60 * 60 * 24 * 100).boxed().toList();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> lastUpdateDts = createDtDeltas.stream().map(c -> now.plusSeconds(c).plusSeconds(randomGenerator.nextInt(1, 200)))
            .toList();

        return IntStream.range(0, size).boxed().map(idx -> {
            return Order.builder()
                .sequenceNo(UUID.randomUUID().toString())
                .customerId(customerIds.get(idx))
                .productId(randomProductIds.get(idx))
                .valueDate(today.plusDays(dateDeltas.get(idx)))
                .price(prices.get(idx))
                .quantity(quantities.get(idx))
                .value(values.get(idx))
                .createDt(now.plusSeconds(createDtDeltas.get(idx)))
                .lastUpdateDt(lastUpdateDts.get(idx))
                .build();
        });
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String sequenceNo;
        private Integer customerId;
        private Integer productId;
        private LocalDate valueDate;
        private BigDecimal price;
        private int quantity;
        private BigDecimal value;
        private LocalDateTime createDt;
        private LocalDateTime lastUpdateDt;

        private Builder() {
        }


        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sequenceNo(String sequenceNo) {
            this.sequenceNo = sequenceNo;
            return this;
        }

        public Builder customerId(Integer costomerId) {
            this.customerId = costomerId;
            return this;
        }

        public Builder productId(Integer productId) {
            this.productId = productId;
            return this;
        }

        public Builder valueDate(LocalDate valueDate) {
            this.valueDate = valueDate;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public Builder createDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder lastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public Order build() {
            return new Order(id, sequenceNo, customerId, productId, valueDate, price, quantity, value, createDt, lastUpdateDt);
        }
    }
}
