package top.ilovemyhome.peanotes.common.task.admin.web.handlers.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class R<T> implements Serializable {

    private String code;

    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ex;

    private T data;

    private R() {
    }

    public boolean isSuccess(){
        return this.code.equals(RCode.SUCCESS.getCode());
    }

    public static R<?> ok() {
        return R.initR(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), null, null);
    }

    public static <T> R<T> ok(T data) {
        return R.initR(RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg(), null, data);
    }

    public static <T> R<T> ok(T data, String msg) {
        return R.initR(RCode.SUCCESS.getCode(), msg, null, data);
    }

    public static R fail() {
        return R.initR(RCode.INTERNAL_SERVER_ERROR.getCode(), RCode.INTERNAL_SERVER_ERROR.getMsg(), null, null);
    }

    public static R fail(String msg) {
        return R.initR(RCode.INTERNAL_SERVER_ERROR.getCode(), msg, null, null);
    }

    public static R fail(String code, String msg) {
        return R.initR(code, msg != null ? clearStr(msg) : RCode.byCode(code).getMsg(), null, null);
    }

    public static R fail(String code, String msg, String ex) {
        return R.initR(code, msg != null ? clearStr(msg) : RCode.byCode(code).getMsg(), ex, null);
    }

    private static <T> R<T> initR(String code, String msg, String ex, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setEx(ex);
        r.setData(data);
        return r;
    }

    private static String clearStr(String str) {
        return StringUtils.replace(str, "\"", "");
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
