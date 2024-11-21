package top.ilovemyhome.peanotes.common.task.admin.dao.helper;

import java.util.Objects;

public final class SqlHelper {

    public static String fuzzyString(String keyword, boolean left, boolean right) {
        if (Objects.isNull(keyword)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (left && !keyword.startsWith("%")) {
            stringBuilder.append("%");
        }
        stringBuilder.append(keyword);
        if (right && !keyword.endsWith("%")) {
            stringBuilder.append("%");
        }
        return stringBuilder.toString();
    }

    private SqlHelper() {
    }

}
