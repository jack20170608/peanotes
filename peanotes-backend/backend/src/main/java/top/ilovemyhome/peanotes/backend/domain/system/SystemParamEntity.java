package top.ilovemyhome.peanotes.backend.domain.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import top.ilovemyhome.peanotes.backend.common.Constants;
import top.ilovemyhome.peanotes.backend.common.db.dao.common.Persistable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonDeserialize(builder = SystemParamEntity.Builder.class)
public class SystemParamEntity implements Persistable<Long> {

    private transient boolean persisted;

    private Long id;
    private final String paramName;
    private final String paramValue;
    private final String paramDesc;
    private final LocalDateTime createDt;
    private final LocalDateTime updateDt;


    private SystemParamEntity(Long id, String paramName, String paramValue, String paramDesc, LocalDateTime createDt, LocalDateTime updateDt) {
        this.id = id;
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.paramDesc = paramDesc;
        this.createDt = createDt;
        this.updateDt = updateDt;
    }

    public enum Field {
        id("ID", true)
        , paramName("PARAM_NAME" )
        , paramValue("PARAM_VALUE" )
        , paramDesc("PARAM_DESC")
        , createDt("CREATE_DT")
        , updateDt("UPDATE_DT")
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

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(SystemParamEntity.Field.values())
        .collect(Collectors.toMap(SystemParamEntity.Field::name, SystemParamEntity.Field::getDbColumn)));

    public static final List<String> ID_FIELDS = ImmutableList.copyOf(Stream.of(SystemParamEntity.Field.values())
        .filter(f -> f.isId)
        .map(Enum::name)
        .collect(Collectors.toList()));

    public String getParamName() {
        return paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.JSON_DATETIME_FORMAT)
    public LocalDateTime getCreateDt() {
        return createDt;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = Constants.JSON_DATETIME_FORMAT)
    public LocalDateTime getUpdateDt() {
        return updateDt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return persisted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemParamEntity that = (SystemParamEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(paramName, that.paramName) && Objects.equals(paramValue, that.paramValue) && Objects.equals(paramDesc, that.paramDesc) && Objects.equals(createDt, that.createDt) && Objects.equals(updateDt, that.updateDt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paramName, paramValue, paramDesc, createDt, updateDt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SystemParamEntity old) {
        return new Builder()
            .withId(old.id)
            .withParamName(old.paramName)
            .withParamValue(old.paramValue)
            .withParamDesc(old.paramDesc)
            .withCreateDt(old.createDt)
            .withUpdateDt(old.updateDt);
    }

    @JsonPOJOBuilder()
    public static final class Builder {
        private Long id;
        private String paramName;
        private String paramValue;
        private String paramDesc;
        private LocalDateTime createDt;
        private LocalDateTime updateDt;

        private Builder() {
        }


        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withParamName(String paramName) {
            this.paramName = paramName;
            return this;
        }

        public Builder withParamValue(String paramValue) {
            this.paramValue = paramValue;
            return this;
        }

        public Builder withParamDesc(String paramDesc) {
            this.paramDesc = paramDesc;
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
        public Builder withUpdateDt(LocalDateTime updateDt) {
            this.updateDt = updateDt;
            return this;
        }

        public SystemParamEntity build() {
            return new SystemParamEntity(id, paramName, paramValue, paramDesc, createDt, updateDt);
        }
    }
}
