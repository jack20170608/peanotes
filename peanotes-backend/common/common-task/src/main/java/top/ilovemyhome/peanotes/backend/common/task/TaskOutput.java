package top.ilovemyhome.peanotes.backend.common.task;

import java.io.Serializable;

public interface TaskOutput<O> extends Serializable {

    default O getOutput() {
        return null;
    }

    boolean isSuccessful();

    default Throwable getFailureCause(){
        return null;
    }

    default String getFailureReason(){
        return null;
    }
}
