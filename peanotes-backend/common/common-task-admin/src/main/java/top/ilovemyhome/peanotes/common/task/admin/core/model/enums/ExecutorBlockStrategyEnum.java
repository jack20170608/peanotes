package top.ilovemyhome.peanotes.common.task.admin.core.model.enums;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xuxueli on 17/5/9.
 */
public enum ExecutorBlockStrategyEnum {

    SERIAL_EXECUTION("Serial execution"),
    DISCARD_LATER("Discard Later"),
    COVER_EARLY("Cover Early");

    private String title;
    private ExecutorBlockStrategyEnum(String title) {
        this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public static final Map<String, ExecutorBlockStrategyEnum> TITLE_MAP = ImmutableMap.copyOf(Stream.of(ExecutorBlockStrategyEnum.values())
        .collect(Collectors.toMap(ExecutorBlockStrategyEnum::getTitle, e -> e)));
}
