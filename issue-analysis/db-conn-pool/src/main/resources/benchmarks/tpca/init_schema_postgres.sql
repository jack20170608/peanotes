DROP TABLE IF EXISTS history CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;

-- 创建账户表 (accounts)
CREATE TABLE accounts
(
  acc_id      INTEGER        NOT NULL,
  acc_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
  acc_fname   VARCHAR(20),
  acc_lname   VARCHAR(20),
  acc_pin     CHAR(4),
  acc_status  CHAR(1)        NOT NULL DEFAULT 'A', -- 'A' for Active
  PRIMARY KEY (acc_id)
);

-- 创建历史表 (history)
-- 用于记录所有转账交易
CREATE TABLE history
(
  hist_id         SERIAL         NOT NULL,
  hist_acc_id     INTEGER        NOT NULL,
  hist_date       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hist_amount     DECIMAL(15, 2) NOT NULL,
  hist_type       CHAR(1)        NOT NULL, -- 'D' for Deposit, 'W' for Withdrawal, 'T' for Transfer
  hist_src_acc_id INTEGER,                 -- Source account for transfers
  PRIMARY KEY (hist_id),
  FOREIGN KEY (hist_acc_id) REFERENCES accounts (acc_id),
  FOREIGN KEY (hist_src_acc_id) REFERENCES accounts (acc_id)
);

-- 为 history 表创建索引，以加速按账户ID和日期的查询
CREATE INDEX idx_history_acc_id ON history(hist_acc_id);
CREATE INDEX idx_history_date ON history(hist_date);
