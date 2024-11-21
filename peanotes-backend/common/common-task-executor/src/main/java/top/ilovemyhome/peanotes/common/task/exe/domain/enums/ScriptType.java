package top.ilovemyhome.peanotes.common.task.exe.domain.enums;

public enum ScriptType {

    SHELL("Shell",  "bash", ".sh"),
    PYTHON("Python",  "python", ".py"),
    PHP("PHP", "php", ".php"),
    NODEJS("Nodejs", "node", ".js"),
    POWERSHELL("PowerShell", "powershell", ".ps1");

    private String desc;
    private boolean isScript;
    private String cmd;
    private String suffix;

    ScriptType(String desc, String cmd, String suffix) {
        this.desc = desc;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public String getDesc() {
        return desc;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getCmd() {
        return cmd;
    }
}
