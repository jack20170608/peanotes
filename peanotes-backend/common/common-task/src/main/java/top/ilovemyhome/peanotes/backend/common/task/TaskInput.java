package top.ilovemyhome.peanotes.backend.common.task;

import java.io.Serializable;
import java.util.Map;

public interface TaskInput<I> extends Serializable {

    TaskOrder getTaskOrder();

    I getInput();

    default Map<String, String> getAttributes(){
        return null;
    }

    default String toJson(){
        return null;
    }

}
