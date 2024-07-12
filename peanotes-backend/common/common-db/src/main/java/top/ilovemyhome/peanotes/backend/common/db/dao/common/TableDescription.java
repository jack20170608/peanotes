package top.ilovemyhome.peanotes.backend.common.db.dao.common;

import org.flywaydb.core.internal.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.List.copyOf;

public class TableDescription {

    private final boolean idAutoGenerate;
    private final String name;
    private final TreeMap<String, String> fieldColumnMap;
    private final String idField;
    private final String fromClause;

    public String getName() {
        return name;
    }

    public String getIdField() {
        return idField;
    }


    public String getFromClause() {
        return fromClause;
    }

    public boolean isIdAutoGenerate() {
        return idAutoGenerate;
    }

    public TreeMap<String, String> getFieldColumnMap() {
        return fieldColumnMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean idAutoGenerate;
        private String name;
        private Map<String, String> fieldColumnMap;
        private String idField;
        private String fromClause;

        private Builder() {
        }

        public Builder withIdAutoGenerate(boolean idAutoGenerate) {
            this.idAutoGenerate = idAutoGenerate;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withFieldColumnMap(Map<String, String> fieldColumnMap) {
            this.fieldColumnMap = fieldColumnMap;
            return this;
        }

        public Builder withIdField(String idField) {
            this.idField = idField;
            return this;
        }

        public Builder withFromClause(String fromClause) {
            this.fromClause = fromClause;
            return this;
        }

        public TableDescription build() {
            return new TableDescription(name, idAutoGenerate, fromClause, fieldColumnMap, idField);
        }
    }

    private TableDescription(@NotNull String name, boolean idAutoGenerate, String fromClause, @NotNull Map<String, String> fieldColumnMap
        , String idField) {
        this.name = name;
        this.idAutoGenerate = idAutoGenerate;
        this.idField = idField;
        if (StringUtils.hasText(fromClause)) {
            this.fromClause = fromClause;
        } else {
            this.fromClause = name;
        }
        this.fieldColumnMap = Objects.nonNull(fieldColumnMap) ? new TreeMap<>(fieldColumnMap) : null;
    }

}
