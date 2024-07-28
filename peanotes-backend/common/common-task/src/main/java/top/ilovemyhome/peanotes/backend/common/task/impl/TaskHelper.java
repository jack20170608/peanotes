package top.ilovemyhome.peanotes.backend.common.task.impl;

import top.ilovemyhome.peanotes.backend.common.task.TaskOutput;

public final class TaskHelper {

    public static <O> TaskOutput<O> createErrorOutput(Throwable t) {
        return new TaskOutput<>() {
            @Override
            public boolean isSuccessful() {
                return false;
            }

            @Override
            public Throwable getFailureCause() {
                return t;
            }

            @Override
            public String getFailureReason() {
                return t.getMessage();
            }
        };
    }
}
