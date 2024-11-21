package top.ilovemyhome.peanotes.common.task.admin.domain;


import com.google.common.collect.ImmutableMap;
import org.flywaydb.core.internal.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xuxueli 2019-05-04 16:43:12
 */
public class JobUser {

	private int id;
	private String username;		// 账号
	private String password;		// 密码
	private int role;				// 角色：0-普通用户、1-管理员
	private String permission;	// 权限：执行器ID列表，多个逗号分割

    public enum Field {
        id("ID", true)
        , username("USERNAME" )
        , password("PASSWORD" )
        , role("ROLE" )
        , permission("PERMISSION" );

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

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(JobGroup.Field.values())
        .collect(Collectors.toMap(JobGroup.Field::name, JobGroup.Field::getDbColumn)));

	// plugin
	public boolean validPermission(int jobGroup){
		if (this.role == 1) {
			return true;
		} else {
			if (StringUtils.hasText(this.permission)) {
				for (String permissionItem : this.permission.split(",")) {
					if (String.valueOf(jobGroup).equals(permissionItem)) {
						return true;
					}
				}
			}
			return false;
		}
	}

    public JobUser(int id, String username, String password, int role, String permission) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }

    public String getPermission() {
        return permission;
    }
}
