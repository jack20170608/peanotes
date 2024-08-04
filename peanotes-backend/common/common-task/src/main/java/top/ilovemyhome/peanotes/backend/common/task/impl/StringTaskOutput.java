package top.ilovemyhome.peanotes.backend.common.task.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;

@JsonDeserialize(builder = StringTaskOutput.Builder.class)
public class StringTaskOutput implements TaskOutput<String> {

    public static StringTaskOutput success(String output){
        return new StringTaskOutput(output, true , null, null);
    }

    public static StringTaskOutput fail(String failReason){
        return fail(failReason, null);
    }
    public static StringTaskOutput fail(String output, String failReason){
        return fail(output, failReason, null);
    }

    public static StringTaskOutput fail(String output, String failReason, Throwable failureCause){
        return new StringTaskOutput(output, false, failureCause, failReason);
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public boolean isSuccessful() {
        return success;
    }

    @JsonIgnore
    @Override
    public Throwable getFailureCause() {
        return failureCause;
    }

    @Override
    public String getFailureReason() {
        return failReason;
    }

    public StringTaskOutput(String output, boolean success, Throwable failureCause, String failReason) {
        this.output = output;
        this.success = success;
        this.failureCause = failureCause;
        this.failReason = failReason;
    }

    @Override
    public String toString() {
        return "StringTaskOutput{" +
            "output='" + output + '\'' +
            ", success=" + success +
            ", failureCause=" + failureCause +
            ", failReason='" + failReason + '\'' +
            '}';
    }



    private final String output;
    private final boolean success;
    private final Throwable failureCause;
    private final String failReason;

    @JsonPOJOBuilder()
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private String output;
        private boolean success;
        private Throwable failureCause;
        private String failReason;

        private Builder() {
        }

        public static Builder aStringTaskOutput() {
            return new Builder();
        }

        public Builder withOutput(String output) {
            this.output = output;
            return this;
        }

        public Builder withSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public Builder withFailureCause(Throwable failureCause) {
            this.failureCause = failureCause;
            return this;
        }

        public Builder withFailReason(String failReason) {
            this.failReason = failReason;
            return this;
        }

        public StringTaskOutput build() {
            return new StringTaskOutput(output, success, failureCause, failReason);
        }
    }
}
