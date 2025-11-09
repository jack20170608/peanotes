package top.ilovemyhome.issue.analysis.dbconnpool.common.testsuite;

public enum TestSuite {
    SIMPLE("simple"),
    TPC_A("tpca"),
    TPC_C("tpcc"),
    USER_DEFINED("user_defined");

    TestSuite(String  resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    private String resourceFolder;

    public String getResourceFolder() {
        return resourceFolder;
    }
}
