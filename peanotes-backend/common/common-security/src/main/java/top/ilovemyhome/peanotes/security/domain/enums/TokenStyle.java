package top.ilovemyhome.peanotes.security.domain.enums;


/**
 * uuid、simple-uuid、random-32、random-64、random-128、tik
 */
public enum TokenStyle {


    /**
     * Token风格: 简单uuid (不带下划线)
     */
    UUID
    /**
     * 简单UUID
     */
    , SIMPLE_UUID
    /**
     * 32到128位随机字符串
     */
    , RANDOM32
    , RANDOM64
    , RANDOM128
    /**
     * tik风格 (2_14_16)
     */
    , TIK;
}
