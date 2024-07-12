package top.ilovemyhome.peanotes.backend.dao.operation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.SearchCriteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonDeserialize(builder = OperationLogSearchCriteria.Builder.class)
public class OperationLogSearchCriteria implements SearchCriteria {

    private final List<Long> listOfId;
    private final Long userId;
    private final LocalDateTime minCreateDt;
    private final LocalDateTime maxCreateDt;
    private final String uriPrefix;

    private final Map<String, Object> normalParameters = Maps.newHashMap();
    private final Map<String, List> listParameters = Maps.newHashMap();

    private OperationLogSearchCriteria(List<Long> listOfId, Long userId, LocalDateTime minCreateDt, LocalDateTime maxCreateDt, String uriPrefix) {
        this.listOfId = listOfId;
        this.userId = userId;
        this.minCreateDt = minCreateDt;
        this.maxCreateDt = maxCreateDt;
        this.uriPrefix = uriPrefix;
    }

    @Override
    public String whereClause() {
        StringBuilder whereClause = new StringBuilder(" where 1 = 1 ");
        boolean emptyCriteria = true;
        if (Objects.nonNull(listOfId) && !listOfId.isEmpty()) {
            emptyCriteria = false;
            whereClause.append(" and ID in (<listOfId>)");
            listParameters.put("listOfId", listOfId);
        }
        if (Objects.nonNull(userId)) {
            emptyCriteria = false;
            whereClause.append(" and USER_ID = :userId");
            normalParameters.put("userId", userId);
        }
        if (Objects.nonNull(minCreateDt)) {
            emptyCriteria = false;
            whereClause.append(" and CREATE_DT >= :minCreateDt");
            normalParameters.put("minCreateDt", minCreateDt);
        }
        if (Objects.nonNull(maxCreateDt)) {
            emptyCriteria = false;
            whereClause.append(" and CREATE_DT <= :maxCreateDt");
            normalParameters.put("maxCreateDt", maxCreateDt);
        }
        if (Objects.nonNull(uriPrefix)) {
            emptyCriteria = false;
            whereClause.append(" and URI like :uriPrefix");
            normalParameters.put("uriPrefix", uriPrefix + "%");
        }
        //just in-case return the whole table
        if (emptyCriteria) {
            whereClause.append(" and 1 = 2 ");
        }
        return whereClause.toString();
    }

    public List<Long> getListOfId() {
        return listOfId;
    }

    public Long getUserId() {
        return userId;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
    public LocalDateTime getMinCreateDt() {
        return minCreateDt;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
    public LocalDateTime getMaxCreateDt() {
        return maxCreateDt;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    @Override
    public Map<String, Object> normalParams() {
        return ImmutableMap.copyOf(this.normalParameters);
    }

    @Override
    public Map<String, List> listParam() {
        return ImmutableMap.copyOf(listParameters);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationLogSearchCriteria that = (OperationLogSearchCriteria) o;
        return Objects.equals(listOfId, that.listOfId) && Objects.equals(userId, that.userId) && Objects.equals(minCreateDt, that.minCreateDt) && Objects.equals(maxCreateDt, that.maxCreateDt) && Objects.equals(uriPrefix, that.uriPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listOfId, userId, minCreateDt, maxCreateDt, uriPrefix);
    }

    @Override
    public String toString() {
        return "OperationLogSearchCriteria{" +
            "listOfId=" + listOfId +
            ", userId=" + userId +
            ", maxCreateDt=" + maxCreateDt +
            ", minCreateDt=" + minCreateDt +
            ", uriPrefix='" + uriPrefix + '\'' +
            '}';
    }

    @JsonPOJOBuilder()
    public static final class Builder {
        private List<Long> listOfId;
        private Long userId;

        private LocalDateTime minCreateDt;
        private LocalDateTime maxCreateDt;
        private String uriPrefix;

        private Builder() {
        }

        public Builder withListOfId(List<Long> listOfId) {
            this.listOfId = listOfId;
            return this;
        }

        public Builder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withMinCreateDt(LocalDateTime minCreateDt) {
            this.minCreateDt = minCreateDt;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withMaxCreateDt(LocalDateTime maxCreateDt) {
            this.maxCreateDt = maxCreateDt;
            return this;
        }

        public Builder withUriPrefix(String uriPrefix) {
            this.uriPrefix = uriPrefix;
            return this;
        }

        public OperationLogSearchCriteria build() {
            return new OperationLogSearchCriteria(this.listOfId, this.userId, this.minCreateDt, this.maxCreateDt, this.uriPrefix);
        }
    }
}
