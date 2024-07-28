package top.ilovemyhome.peanotes.backend.common.task;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//persist object
public final class TaskRecord {

    private Long id;
    private String orderKey;
    private String name;
    private String description;
    private String executionKey;
    private Set<Long> successorIds;
    private String input;
    private String output;
    private boolean async;
    private boolean dummy;
    private LocalDateTime createDt;
    private LocalDateTime lastUpdateDt;

    private TaskStatus status;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private boolean success;
    private String failReason;


    public enum Field {
        id("ID", true),
        orderKey("ORDER_KEY"),
        name("NAME"),
        description("DESCRIPTION"),
        executionKey("EXECUTION_KEY"),
        successorIdStr("SUCCESSOR_IDS"),
        input("INPUT"),
        output("OUTPUT"),
        async("ASYNC"),
        dummy("DUMMY"),
        createDt("CREATE_DT"),
        lastUpdateDt("LAST_UPDATE_DT"),
        status("STATUS"),
        startDt("START_DT"),
        endDt("END_DT"),
        success("SUCCESS"),
        failReason("FAIL_REASON");

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
        = Collections.unmodifiableMap(Stream.of(Field.values())
        .collect(Collectors.toMap(Field::name, Field::getDbColumn)));

    public static final String ID_FIELD = Field.id.name();

    public Long getId() {
        return id;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getExecutionKey() {
        return executionKey;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public boolean isAsync() {
        return async;
    }

    public boolean isDummy() {
        return dummy;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getStartDt() {
        return startDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailReason() {
        return failReason;
    }

    public Set<Long> getSuccessorIds() {
        return successorIds;
    }

    public String getSuccessorIdStr() {
        if (successorIds == null || successorIds.isEmpty()) {
            return null;
        }
        return successorIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String failReason;
        private boolean success;
        private LocalDateTime endDt;
        private LocalDateTime startDt;
        private TaskStatus status;
        private LocalDateTime lastUpdateDt;
        private LocalDateTime createDt;
        private boolean dummy;
        private boolean async;
        private String output;
        private String input;
        private Set<Long> successorIds;
        private String executionKey;
        private String description;
        private String name;
        private String orderKey;
        private Long id;

        private Builder() {
        }


        public Builder withFailReason(String failReason) {
            this.failReason = failReason;
            return this;
        }

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withEndDt(LocalDateTime endDt) {
            this.endDt = endDt;
            return this;
        }

        public Builder withStartDt(LocalDateTime startDt) {
            this.startDt = startDt;
            return this;
        }

        public Builder withStatus(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public Builder withCreateDt(LocalDateTime createDt) {
            this.createDt = createDt;
            return this;
        }

        public Builder withDummy(boolean dummy) {
            this.dummy = dummy;
            return this;
        }

        public Builder withAsync(boolean async) {
            this.async = async;
            return this;
        }

        public Builder withOutput(String output) {
            this.output = output;
            return this;
        }

        public Builder withInput(String input) {
            this.input = input;
            return this;
        }

        public Builder withSuccessorIds(Set<Long> successorIds) {
            this.successorIds = successorIds;
            return this;
        }

        public Builder withExecutionKey(String executionKey) {
            this.executionKey = executionKey;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withOrderKey(String orderKey) {
            this.orderKey = orderKey;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public TaskRecord build() {
            TaskRecord taskRecord = new TaskRecord();
            taskRecord.executionKey = this.executionKey;
            taskRecord.name = this.name;
            taskRecord.failReason = this.failReason;
            taskRecord.status = this.status;
            taskRecord.lastUpdateDt = this.lastUpdateDt;
            taskRecord.output = this.output;
            taskRecord.input = this.input;
            taskRecord.successorIds = this.successorIds;
            taskRecord.success = this.success;
            taskRecord.description = this.description;
            taskRecord.startDt = this.startDt;
            taskRecord.endDt = this.endDt;
            taskRecord.id = this.id;
            taskRecord.dummy = this.dummy;
            taskRecord.createDt = this.createDt;
            taskRecord.orderKey = this.orderKey;
            taskRecord.async = this.async;
            return taskRecord;
        }
    }
}
