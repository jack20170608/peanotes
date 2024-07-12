package top.ilovemyhome.common.oss;


import top.ilovemyhome.peanotes.backend.common.text.StrUtils;

public class IaasProperties {
    /**
     * 对象存储类型
     */
    private String osType;
    /**
     * 阿里 OSS 配置
     */
    private OSS oss;
    private BLOS blos;
    /**
     * Ali OSS 配置
     */
    public static class OSS {
        /**
         * oss endpoint
         */
        private String endpoint;
        /**
         * oss accessKeyId
         */
        private String accessKeyId;
        /**
         * oss accessKeySecret
         */
        private String secretAccessKey;
        /**
         * oss bucket名称
         */
        private String bucketName;
        /**
         * oss 的访问地址, 可以通过域名映射
         */
        private String domain;
        /**
         * oss 默认上传地址, 不填则默认上传至 bucket 根目录下, 以 / 结尾
         */
        private String defaultPath;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public void setAccessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
        }

        public String getSecretAccessKey() {
            return secretAccessKey;
        }

        public void setSecretAccessKey(String secretAccessKey) {
            this.secretAccessKey = secretAccessKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getDefaultPath() {
            return defaultPath;
        }

        public void setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
        }
    }

    public static class BLOS {
        private String domain;
        /**
         * BLOS 默认上传地址, 不能为空, 注意不同系统的区分, 末尾带有 "/" 会自动清除
         */
        private String defaultPath;

        public String getDomain() {
            return domain;
        }

        public String getDefaultPath() {
            return defaultPath;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public void setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
        }
    }

    public void init() {
        if (oss != null) {
            oss.setDomain(formatDomain(oss.getDomain()));
            oss.setDefaultPath(formatPath(oss.getDefaultPath()));
        }
        if (blos != null) {
            String domain = formatDomain(blos.getDomain());
            if (!StrUtils.endWith(domain, "/pic")) {
                domain = domain + "/pic";
            }
            blos.setDomain(domain);
            blos.setDefaultPath(formatPath(blos.getDefaultPath()));
        }
    }

    /**
     * 会将末尾的 "/" 删除
     */
    private String formatDomain(String str) {
        if (StrUtils.isNotBlank(str) && StrUtils.endWith(str, "/")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private String formatPath(String str) {
        if (StrUtils.isNotBlank(str) && StrUtils.endWith(str, "/")) {
            str = str.substring(0, str.length() - 1);
        }
        if (StrUtils.isNotBlank(str) && !StrUtils.startWith(str, "/")) {
            str = "/" + str;
        }
        return str;
    }

    public String getOsType() {
        return osType;
    }

    public OSS getOss() {
        return oss;
    }
}
