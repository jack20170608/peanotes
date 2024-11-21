package top.ilovemyhome.peanotes.common.task.admin.core.model.enums;


import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xuxueli 2020-10-29 21:11:23
 */
public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING("DoNothing"),

    /**
     * fire once now
     */
    FIRE_ONCE_NOW("FireOnceNow"),;

    private String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static final Map<String, MisfireStrategyEnum> TITLE_MAP = ImmutableMap.copyOf(Stream.of(MisfireStrategyEnum.values())
        .collect(Collectors.toMap(MisfireStrategyEnum::getTitle, e -> e)));


}
