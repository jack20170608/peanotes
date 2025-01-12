package top.ilovemyhome.peanotes.backend.domain;

import java.util.List;
import java.util.Map;

public record QueryResultV2(String sql, List<String> columns, List<Object[]> data, int rowCount, String timeCost){

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private String sql;
        private List<String> columns;
        private List<Object[]> data;
        private int rowCount;
        private String timeCost;

        private Builder() {
        }

        public static Builder aQueryResultV2() {
            return new Builder();
        }

        public Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Builder columns(List<String> columns) {
            this.columns = columns;
            return this;
        }

        public Builder data(List<Object[]> data) {
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

        public QueryResultV2 build() {
            return new QueryResultV2(sql, columns, data, rowCount, timeCost);
        }
    }
}
