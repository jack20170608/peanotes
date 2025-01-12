package top.ilovemyhome.peanotes.backend.domain;

import java.util.List;
import java.util.Map;

public record QueryResultV1(String sql, List<Map<String, Object>> data, int rowCount, String timeCost){

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String sql;
        private List<Map<String, Object>> data;
        private int rowCount;
        private String timeCost;

        private Builder() {
        }



        public Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Builder data(List<Map<String, Object>> data) {
            this.data = data;
            return this;
        }

        public Builder rowCount(int rowCount) {
            this.rowCount = rowCount;
            return this;
        }

        public Builder timeCost(String timeCost) {
            this.timeCost = timeCost;
            return this;
        }

        public QueryResultV1 build() {
            return new QueryResultV1(sql, data, rowCount, timeCost);
        }
    }
}
