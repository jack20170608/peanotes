package top.ilovemyhome.benchmark.si.enums;

public enum BenchmarkType {
    SIMPLE("simple")
    , TPC_A("tpc-a")
    , TPC_C("tpc-c")
    , TPC_E("tpc-e")
    , TPC_H("tpc-h")
    , USER_DEFINED("user-defined");

    private final String code;

    BenchmarkType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
