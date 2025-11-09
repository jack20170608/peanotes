package top.ilovemyhome.issue.analysis.dbconnpool.benchmark.tpca;

import java.math.BigDecimal;

public record Accounts(Long accId, BigDecimal accBalance
    , String accFname
    , String accLname
    , String accPin) {
}
