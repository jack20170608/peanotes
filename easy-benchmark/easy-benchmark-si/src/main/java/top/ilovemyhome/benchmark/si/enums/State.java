package top.ilovemyhome.benchmark.si.enums;

public enum State {
    INIT
    , RUNNING
    , STOPPING
    , SUCCESS
    , ERROR;


    private boolean finalState() {
        return this == SUCCESS || this == ERROR;
    }
}
