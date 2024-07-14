package top.ilovemyhome.peanotes.autotest.domain;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.Persistable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KarateRequest implements Persistable<Long> {

    private Long id;
    private final String name;
    private final String sequenceNo;
    private final LocalDateTime createDt;
    private final String serviceName;
    private final String env;
    private final List<String> featureFileNames;
    private final List<String> tags;

    private KarateRequest(Long id, String name, String sequenceNo, LocalDateTime createDt, String serviceName, String env, List<String> featureFileNames, List<String> tags) {
        this.id = id;
        this.name = name;
        this.sequenceNo = sequenceNo;
        this.createDt = createDt;
        this.serviceName = serviceName;
        this.env = env;
        this.featureFileNames = featureFileNames;
        this.tags = tags;
    }

    public enum Field {
        id("ID", true),
        name("NAME"),
        sequenceNo("SEQUENCE_NO"),
        createDt("CREATE_DT"),
        serviceName("SERVICE_NAME"),
        env("ENV"),
        featureFileNames("FEATURE_FILE_NAMES"),
        tags("TAGS");

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
        = Collections.unmodifiableMap(Stream.of(KarateRequest.Field.values())
        .collect(Collectors.toMap(KarateRequest.Field::name, KarateRequest.Field::getDbColumn)));

    public static final String ID_FIELD = KarateRequest.Field.id.name();


    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null || id == 0;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEnv() {
        return env;
    }

    public List<String> getFeatureFileNames() {
        return featureFileNames;
    }

    public List<String> getTags() {
        return tags;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KarateRequest that = (KarateRequest) o;
        return Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(sequenceNo, that.sequenceNo)
            && Objects.equals(serviceName, that.serviceName)
            && Objects.equals(env, that.env)
            && Objects.equals(featureFileNames, that.featureFileNames)
            && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sequenceNo, serviceName, env, featureFileNames, tags);
    }

    @Override
    public String toString() {
        return "KarateRequest{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", sequenceNo='" + sequenceNo + '\'' +
            ", createDt=" + createDt +
            ", serviceName='" + serviceName + '\'' +
            ", env='" + env + '\'' +
            ", featureFileNames=" + featureFileNames +
            ", tags=" + tags +
            '}';
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String sequenceNo;
        private LocalDateTime createDt;
        private String serviceName;
        private String env;
        private List<String> featureFileNames;
        private List<String> tags;

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

        public Builder withSequenceNo(String sequenceNo) {
            this.sequenceNo = sequenceNo;
            return this;
        }

        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder withServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder withEnv(String env) {
            this.env = env;
            return this;
        }

        public Builder withFeatureFileNames(List<String> featureFileNames) {
            this.featureFileNames = featureFileNames;
            return this;
        }

        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public KarateRequest build() {
            return new KarateRequest(id, name, sequenceNo, createDt, serviceName, env, featureFileNames, tags);
        }
    }
}
