package top.ilovemyhome.peanotes.backend.common.task.impl;

import top.ilovemyhome.peanotes.backend.common.task.TaskInput;

import java.util.Collections;
import java.util.Map;

public record StringTaskInput(Long taskId, String input, Map<String, String> attributes) implements TaskInput<String> {

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Long getTaskId() {
        return taskId;
    }
}
