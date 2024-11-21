package top.ilovemyhome.peanotes.autotest.domain;

import top.ilovemyhome.peanotes.autotest.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KarateExecution {

    private Long id;
    private final Long karateRequestId;
    private Status status;
    private final String reportPath;
    private final LocalDateTime createDt;
    private final LocalDateTime startDt;
    private LocalDateTime endDt;

    private KarateRequest karateRequest;

    private KarateExecution(Long id, Long karateRequestId, Status status, String reportPath
        , LocalDateTime createDt, LocalDateTime startDt, LocalDateTime endDt) {
        this.id = id;
        this.karateRequestId = karateRequestId;
        this.status = status;
        this.reportPath = reportPath;
        this.createDt = createDt;
        this.startDt = startDt;
        this.endDt = endDt;
    }

    public enum Field {
        id("ID", true),
        karateRequestId("KARATE_REQUEST_ID"),
        status("STATUS" ),
        reportPath("REPORT_PATH"),
        createDt("CREATE_DT"),
        startDt("START_DT"),
        endDt("END_DT");

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
        = Collections.unmodifiableMap(Stream.of(KarateExecution.Field.values())
        .collect(Collectors.toMap(KarateExecution.Field::name, KarateExecution.Field::getDbColumn)));

    public static final String ID_FIELD = KarateExecution.Field.id.name();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public Long getKarateRequestId(){
        return karateRequestId;
    }

    public void setKarateRequest(KarateRequest karateRequest) {
        if (!karateRequest.getId().equals(this.karateRequestId)) {
            throw new IllegalStateException("The karate request id is different from the current karate request id");
        }
        this.karateRequest = karateRequest;
    }

    public KarateRequest getKarateRequest() {
        return karateRequest;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReportPath() {
        return reportPath;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getStartDt() {
        return startDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public void setEndDt(LocalDateTime endDt) {
        this.endDt = endDt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KarateExecution that = (KarateExecution) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "KarateExecution{" +
            "id=" + id +
            ", karateRequestId=" + karateRequestId +
            ", status=" + status +
            ", reportPath='" + reportPath + '\'' +
            ", startDt=" + startDt +
            ", endDt=" + endDt +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private LocalDateTime endDt;
        private LocalDateTime startDt;
        private LocalDateTime createDt;
        private String reportPath;
        private Status status;
        private Long karateRequestId;
        private Long id;

        private Builder() {
        }

        public static Builder aKarateExecution() {
            return new Builder();
        }

        public Builder withEndDt(LocalDateTime endDt) {
            this.endDt = endDt;
            return this;
        }

        public Builder withStartDt(LocalDateTime startDt) {
            this.startDt = startDt;
            return this;
        }

        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder withReportPath(String reportPath) {
            this.reportPath = reportPath;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder withKarateRequestId(Long karateRequestId) {
            this.karateRequestId = karateRequestId;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public KarateExecution build() {
            return new KarateExecution(id, karateRequestId, status, reportPath, createDt, startDt, endDt);
        }
    }
}
