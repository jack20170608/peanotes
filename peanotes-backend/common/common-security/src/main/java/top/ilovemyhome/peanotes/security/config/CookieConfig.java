package top.ilovemyhome.peanotes.security.config;

public final class CookieConfig {

    private String domain;

    /**
     * 路径 （一般只有当你在一个域名下部署多个项目时才会用到此值。）
     */
    private String path;

    /**
     * 是否禁止 js 操作 Cookie
     */
    private boolean httpOnly = false;

    /**
     * 是否只在 https 协议下有效
     */
    private boolean secure = false;

    /**
     * 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
     */
    private String sameSite;
}
