package top.ilovemyhome.peanotes.backend.common.db.dao.sample.domain;

import top.ilovemyhome.peanotes.backend.common.db.dao.common.Persistable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bar implements Persistable<Long> {

    private Long id;
    private String name;
    private String others;

    public Bar(Long id, String name, String others) {
        this.id = id;
        this.name = name;
        this.others = others;
    }

    public static Bar of(String name, String others){
        return new Bar(null, name, others);
    }

    public enum Field {
        id("ID", true),
        name("NAME"),
        others("OTHERS");

        private final String dbColumn;
        private final boolean isId;

        Field(String dbColumn) {
            this.dbColumn = dbColumn;
            this.isId = false;
        }

        Field(String dbColumn, boolean isId) {
            this.dbColumn = dbColumn;
            this.isId = isId;
        }

        public String getDbColumn() {
            return dbColumn;
        }

        public boolean isId() {
            return isId;
        }
    }

    public static final Map<String, String> FIELD_COLUMN_MAP
        = Collections.unmodifiableMap(Stream.of(Bar.Field.values())
        .collect(Collectors.toMap(Bar.Field::name, Bar.Field::getDbColumn)));

    public static final String ID_FIELD = Foo.Field.id.name();



    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getOthers() {
        return others;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bar bar = (Bar) o;
        return Objects.equals(id, bar.id) && Objects.equals(name, bar.name) && Objects.equals(others, bar.others);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, others);
    }
}
