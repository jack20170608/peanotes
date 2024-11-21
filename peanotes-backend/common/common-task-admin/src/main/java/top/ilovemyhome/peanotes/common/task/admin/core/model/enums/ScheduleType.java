package top.ilovemyhome.peanotes.common.task.admin.core.model.enums;


import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ScheduleType {

    NONE("none"),

    CRON("cron"),

    FIX_RATE("fixRate");

    private String title;

    ScheduleType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static final Map<String, ScheduleType> TITLE_MAP = ImmutableMap.copyOf(Stream.of(ScheduleType.values())
        .collect(Collectors.toMap(ScheduleType::getTitle, e -> e)));


}
