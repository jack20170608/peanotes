package top.ilovemyhome.peanotes.backend.domain.operation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.ImmutableMap;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.Persistable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@JsonDeserialize(builder = OperationLogEntity.Builder.class)
public class OperationLogEntity implements Persistable<Long> {

    private transient boolean persisted;

    private Long id;
    private final Long userId;


    private final LocalDateTime createDt;
    private final String uri;
    private final String details;

    private OperationLogEntity(Long id, Long userId, LocalDateTime createDt, String uri, String details) {
        this.id = id;
        this.userId = userId;
        this.createDt = createDt;
        this.uri = uri;
        this.details = details;
    }

    //todo by jack, use annotation to keep these infos
    public enum Field {
        id("ID", true), userId("USER_ID"), createDt("CREATE_DT"), uri("URI"), details("DETAILS");
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



    @Override
    public Long getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return !persisted;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUri() {
        return uri;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.JSON_DATETIME_FORMAT)
    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public String getDetails() {
        return details;
    }

    public OperationLogEntity withPersisted(boolean persisted) {
        this.persisted = persisted;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationLogEntity that = (OperationLogEntity) o;
        return persisted == that.persisted && Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(createDt, that.createDt) && Objects.equals(uri, that.uri) && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persisted, id, userId, createDt, uri, details);
    }

    @Override
    public String toString() {
        return "OperationLogEntity{" +
            ", id=" + id +
            ", userId=" + userId +
            ", createDt=" + createDt +
            ", uri='" + uri + '\'' +
            ", details='" + details + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder()
    public static final class Builder {
        private Long id;
        private Long userId;
        private LocalDateTime createDt;
        private String uri;
        private String details;

        private Builder() {
        }



        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = Constants.JSON_DATETIME_FORMAT)
        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withDetails(String details) {
            this.details = details;
            return this;
        }

        public OperationLogEntity build() {
            return new OperationLogEntity(id, userId, createDt, uri, details);
        }
    }
}
