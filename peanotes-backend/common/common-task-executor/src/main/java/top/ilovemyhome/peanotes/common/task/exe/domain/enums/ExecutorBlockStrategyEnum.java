package top.ilovemyhome.peanotes.common.task.exe.domain.enums;

public enum ExecutorBlockStrategyEnum {

    SERIAL_EXECUTION("Serial execution"),
    DISCARD_LATER("Discard Later"),
    COVER_EARLY("Cover Early");

    private String title;
    ExecutorBlockStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
