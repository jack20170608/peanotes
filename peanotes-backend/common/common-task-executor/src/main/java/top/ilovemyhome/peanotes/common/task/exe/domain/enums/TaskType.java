package top.ilovemyhome.peanotes.common.task.exe.domain.enums;

public enum TaskType {

    BEAN("BEAN", false, null, null),
    GLUE_GROOVY("GLUE(Java)", false, null, null),
    SHELL("Shell", true, "bash", ".sh"),
    PYTHON("Python", true, "python", ".py"),
    PHP("PHP", true, "php", ".php"),
    NODEJS("Nodejs", true, "node", ".js"),
    POWERSHELL("PowerShell", true, "powershell", ".ps1");

    private String desc;
    private boolean isScript;
    private String cmd;
    private String suffix;

    TaskType(String desc, boolean isScript, String cmd, String suffix) {
        this.desc = desc;
        this.isScript = isScript;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isScript() {
        return isScript;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getCmd() {
        return cmd;
    }
}
