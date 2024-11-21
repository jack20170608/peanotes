package top.ilovemyhome.peanotes.common.task.admin.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xuxueli 2020-04-11 22:27
 */
public class LogParam implements Serializable {
    private static final long serialVersionUID = 42L;

    public LogParam() {
    }
    public LogParam(LocalDateTime logDateTim, long logId, int fromLineNum) {
        this.logDateTim = logDateTim;
        this.logId = logId;
        this.fromLineNum = fromLineNum;
    }

    private LocalDateTime logDateTim;
    private long logId;
    private int fromLineNum;

    public LocalDateTime getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(LocalDateTime logDateTim) {
        this.logDateTim = logDateTim;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

}
