package top.ilovemyhome.peanotes.backend.common.task;

import top.ilovemyhome.peanotes.backend.common.task.impl.OrderType;

import java.util.Map;

public interface TaskOrder {

    String getKey();

    String getName();

    OrderType getOrderType();

    Map<String, String> getOtherKeys();
}
