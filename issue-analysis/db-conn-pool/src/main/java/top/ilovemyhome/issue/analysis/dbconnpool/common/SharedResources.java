package top.ilovemyhome.issue.analysis.dbconnpool.common;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SharedResources {

    public static final List<Long> insertedIds = new CopyOnWriteArrayList<>();

    public static Long getRandomId() {
        return SharedResources.insertedIds.get(RandomUtils.secure().randomInt(0, SharedResources.insertedIds.size()));
    }


}
