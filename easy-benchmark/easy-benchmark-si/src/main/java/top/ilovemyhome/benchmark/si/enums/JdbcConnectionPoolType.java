package top.ilovemyhome.benchmark.si.enums;


public enum JdbcConnectionPoolType {

    NONE("none")
    , HIKARICP("hikaricp")
    , DBCP("dbcp")
    , DBCP2("dbcp2")
    , C3P0("c3p0")
    , USER_DEFINED("user-defined");

    private String name;

    JdbcConnectionPoolType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
