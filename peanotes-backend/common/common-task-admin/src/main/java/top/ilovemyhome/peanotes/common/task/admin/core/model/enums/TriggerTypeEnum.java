package top.ilovemyhome.peanotes.common.task.admin.core.model.enums;


/**
 * trigger type enum
 *
 * @author xuxueli 2018-09-16 04:56:41
 */
public enum TriggerTypeEnum {

    MANUAL("manual"),
    CRON("cron"),
    RETRY("retry"),
    PARENT("parent"),
    API("api"),
    MISFIRE("misfire");

    private TriggerTypeEnum(String title){
        this.title = title;
    }
    private String title;
    public String getTitle() {
        return title;
    }

}
