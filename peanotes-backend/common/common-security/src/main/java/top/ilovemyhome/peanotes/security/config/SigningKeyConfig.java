package top.ilovemyhome.peanotes.security.config;

public class SigningKeyConfig {

    /**
     * API 调用签名秘钥
     */
    private String secretKey;


    /**
     * 接口调用时的时间戳允许的差距（单位：ms），-1 代表不校验差距，默认15分钟
     *
     * <p> 比如此处你配置了60秒，当一个请求从 client 发起后，如果 server 端60秒内没有处理，60秒后再想处理就无法校验通过了。</p>
     * <p> timestamp + nonce 有效防止重放攻击。 </p>
     */
    private long timestampDisparity = 1000  * 60 * 15;





}
