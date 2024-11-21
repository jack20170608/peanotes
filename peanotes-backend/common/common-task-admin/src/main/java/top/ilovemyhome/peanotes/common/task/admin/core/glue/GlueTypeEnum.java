package top.ilovemyhome.peanotes.common.task.admin.core.glue;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xuxueli on 17/4/26.
 */
public enum GlueTypeEnum {

    BEAN("BEAN", false, null, null),
    GLUE_SHELL("GLUE(Shell)", true, "bash", ".sh"),
    GLUE_PYTHON("GLUE(Python)", true, "python", ".py"),
    GLUE_PHP("GLUE(PHP)", true, "php", ".php"),
    GLUE_NODEJS("GLUE(Nodejs)", true, "node", ".js"),
    GLUE_POWERSHELL("GLUE(PowerShell)", true, "powershell", ".ps1");

    private String title;
    private boolean isScript;
    private String cmd;
    private String suffix;

    private GlueTypeEnum(String title, boolean isScript, String cmd, String suffix) {
        this.title = title;
        this.isScript = isScript;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public String getTitle() {
        return title;
    }

    public boolean isScript() {
        return isScript;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSuffix() {
        return suffix;
    }

    public static final Map<String, GlueTypeEnum> TITLE_MAP = ImmutableMap.copyOf(Stream.of(GlueTypeEnum.values())
        .collect(Collectors.toMap(GlueTypeEnum::getTitle, e -> e)));

}
