package top.ilovemyhome.peanotes.security.config;

import top.ilovemyhome.peanotes.security.domain.enums.TokenStyle;

import java.io.Serializable;

import static top.ilovemyhome.peanotes.security.domain.enums.TokenStyle.UUID;

public class SecurityConfig implements Serializable {

    private String tokenName = "token";

    /**
     * Token TTL time to live
     * By default 30 days, -1 will never expiration
     */
    private long tokenTTL = 60 * 60 * 24 * 30;

    /**
     * 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
     */
    private boolean isSSO = true;

    /**
     * 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
     */
    private boolean isShare = true;

    /**
     * 同一账号最大登录数量，-1代表不限 （只有在 isConcurrent=true, isShare=false 时此配置项才有意义）
     */
    private int maxLoginCount = 12;

    /**
     * 在每次创建 token 时的最高循环次数，用于保证 token 唯一性（-1=不循环尝试，直接使用）
     */
    private int maxTryTimes = 12;

    /**
     * token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
     */
    private TokenStyle tokenStyle = UUID;

    /**
     * jwt秘钥（只有集成 jwt 相关模块时此参数才会生效）
     */
    private String jwtSecretKey;

    private CookieConfig cookieConfig;

    private SigningKeyConfig singingKeyConfig;



}
