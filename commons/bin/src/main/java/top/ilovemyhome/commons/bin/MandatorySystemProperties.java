package top.ilovemyhome.commons.bin;

public enum MandatorySystemProperties {
    APP_NAME("p.app.name"),
    PROC_NAME("p.proc.name"),
    LOG_HOME("p.log.home"),
    PID_FILE("p.pid.file"),
    STATE_FILE("p.state.file");

    private final String dotName;

    MandatorySystemProperties(String dotName) {
        this.dotName   = dotName;
    }

    public String getDotName() {
        return dotName;
    }
}
