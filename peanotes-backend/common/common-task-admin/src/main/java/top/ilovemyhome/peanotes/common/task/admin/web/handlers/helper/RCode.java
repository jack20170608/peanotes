package top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper;

public enum RCode implements IRCode {

    SUCCESS                  ("20000", "OK"),

    /* ──────────────────────────────────────────── 400 ────────────────────────────────────────────────*/
    BAD_REQUEST              ("40000", "Bad Request"),
    NO_PERMISSION            ("40300", "No permission"),
    TOO_MANY_REQUESTS        ("42901", "Bad Request, please retry later."),

    /* ──────────────────────────────────────────── 403 ────────────────────────────────────────────────*/
    SERVER_DENIED            ("40301", "Service Denied."),
    SERVER_DENIED_CUR_ENV    ("40302", "Service Denied, Not enough resource."),

    /* ──────────────────────────────────────────── 404 ────────────────────────────────────────────────────────────*/
    NOT_FOUND                ("40400", "Not Found."),

    /* ──────────────────────────────────────────── 500 ────────────────────────────────────────*/
    INTERNAL_SERVER_ERROR    ("50000", "Internal Server Error."),
    INTERNAL_SQL_ERROR       ("50001", "Internal SQL Error."),

    /* ──────────────────────────────────────────── 503 ────────────────────────────────────────*/
    SERVER_UNAVAILABLE       ("50300", "Server Unavailable, please try again."),
    ;

    private String code;
    private String msg;

    RCode(String code, String message) {
        this.code = code;
        this.msg = message;
    }

    public static RCode byCode(String code) {
        for (RCode item : RCode.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return RCode.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String toString() {
        return this.name();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
