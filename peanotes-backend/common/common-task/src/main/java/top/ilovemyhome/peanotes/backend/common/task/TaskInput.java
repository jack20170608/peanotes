package top.ilovemyhome.peanotes.backend.common.task;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import top.ilovemyhome.peanotes.backend.common.task.impl.StringTaskInput;
import top.ilovemyhome.peanotes.backend.common.task.persistent.TaskOrder;

import java.io.Serializable;
import java.util.Map;

@JsonDeserialize(as = StringTaskInput.class)
public interface TaskInput<I> extends Serializable {

    Long getTaskId();

    I getInput();

    default Map<String, String> getAttributes(){
        return null;
    }

}
