package top.ilovemyhome.peanotes.backend.common.task.impl;

import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;
import top.ilovemyhome.peanotes.backend.common.task.TaskOrder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringTaskInput implements TaskInput<String> {

    @Override
    public TaskOrder getTaskOrder() {
        return taskOrder;
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }


    @Override
    public String toJson() {
        return JacksonUtil.toJson(this);
    }

    public StringTaskInput(TaskOrder taskOrder, String input, Map<String, String> attributes) {
        this.taskOrder = taskOrder;
        this.input = input;
        this.attributes = new HashMap<>(0);
        if (attributes != null) {
            this.attributes.putAll(attributes);
        }
    }

    @Override
    public String toString() {
        return "StringTaskInput{" +
            "taskOrder=" + taskOrder +
            ", input='" + input + '\'' +
            ", attributes=" + attributes +
            '}';
    }

    private final TaskOrder taskOrder;
    private final String input;
    private final Map<String, String> attributes ;
}
