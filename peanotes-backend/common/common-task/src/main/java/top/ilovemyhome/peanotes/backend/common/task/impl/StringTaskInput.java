package top.ilovemyhome.peanotes.backend.common.task.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.backend.common.task.SimpleTaskOrder;
import top.ilovemyhome.peanotes.backend.common.task.TaskInput;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonDeserialize(builder = StringTaskInput.Builder.class)
public class StringTaskInput implements TaskInput<String> {

    @Override
    public SimpleTaskOrder getTaskOrder() {
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

    public StringTaskInput(SimpleTaskOrder taskOrder, String input, Map<String, String> attributes) {
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


    private final SimpleTaskOrder taskOrder;
    private final String input;
    private final Map<String, String> attributes ;

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder()
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
        private SimpleTaskOrder taskOrder;
        private String input;
        private Map<String, String> attributes ;

        private Builder() {
        }

        public Builder withTaskOrder(SimpleTaskOrder taskOrder) {
            this.taskOrder = taskOrder;
            return this;
        }

        public Builder withInput(String input) {
            this.input = input;
            return this;
        }

        public Builder withAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public StringTaskInput build() {
            return new StringTaskInput(taskOrder, input, attributes);
        }
    }
}
