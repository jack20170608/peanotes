package top.ilovemyhome.task.si;

import java.util.Map;

public record TaskInput<I>(Long taskId, I input, Map<String, String> attributes) {}
