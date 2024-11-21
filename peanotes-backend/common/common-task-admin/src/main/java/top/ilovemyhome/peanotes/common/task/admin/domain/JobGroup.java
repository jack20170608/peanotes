package top.ilovemyhome.peanotes.common.task.admin.domain;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xuxueli on 16/9/30.
 */
public class JobGroup {

    private Long id;
    private String appName;
    private String title;
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
    private LocalDateTime updateTime;

    public enum Field {
        id("ID", true)
        , appName("APP_NAME")
        , title("TITLE")
        , addressType("ADDRESS_TYPE")
        , addressList("ADDRESS_LIST")
        , updateTime("UPDATE_TIME");

        private final String dbColumn;
        private final boolean isId;

        Field(String dbColumn) {
            this.dbColumn = dbColumn;
            this.isId = false;
        }

        Field(String dbColumn, boolean isId) {
            this.dbColumn = dbColumn;
            this.isId = isId;
        }

        public String getDbColumn() {
            return dbColumn;
        }

        public boolean isId() {
            return isId;
        }
    }

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(Field.values())
        .collect(Collectors.toMap(Field::name, Field::getDbColumn)));

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)

    public List<String> getRegistryList() {
        if (addressList != null && addressList.trim().length() > 0) {
            registryList = new ArrayList<>(Arrays.asList(addressList.split(",")));
        }
        return registryList;
    }
    public Long getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    public String getTitle() {
        return title;
    }

    public int getAddressType() {
        return addressType;
    }

    public String getAddressList() {
        return addressList;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String appName;
        private String title;
        private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
        private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
        private LocalDateTime updateTime;

        private Builder() {
        }
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withAddressType(int addressType) {
            this.addressType = addressType;
            return this;
        }

        public Builder withAddressList(String addressList) {
            this.addressList = addressList;
            return this;
        }

        public Builder withUpdateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public JobGroup build() {
            JobGroup jobGroup = new JobGroup();
            jobGroup.addressType = this.addressType;
            jobGroup.id = this.id;
            jobGroup.addressList = this.addressList;
            jobGroup.appName = this.appName;
            jobGroup.updateTime = this.updateTime;
            jobGroup.title = this.title;
            return jobGroup;
        }
    }
}
