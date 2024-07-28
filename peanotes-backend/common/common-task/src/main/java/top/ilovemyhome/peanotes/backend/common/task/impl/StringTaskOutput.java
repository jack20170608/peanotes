package top.ilovemyhome.peanotes.backend.common.task.impl;

import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;

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
}
