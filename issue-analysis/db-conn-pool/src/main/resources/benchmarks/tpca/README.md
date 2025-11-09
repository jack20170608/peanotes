# TPC-A测试

TPC-A 是一个经典的**联机事务处理（OLTP）性能基准测试**，它模拟了一个**简单的银行转账场景**。虽然 TPC 委员会已经停止维护 TPC-A，但它仍然是学习和测试数据库基本事务性能的一个很好的入门案例。

与 TPC-C 复杂的多表、多事务类型场景不同，TPC-A 的设计非常简洁，主要包含以下几个部分：

1.  **一个核心表**：`accounts` 表，用于存储账户信息。
2.  **一个事务**：`New-Order` 事务，模拟从一个账户向另一个账户转账，并记录交易历史。
3.  **性能指标**：主要关注 **TPS (Transactions Per Second)**，即每秒能够处理的事务数。

---

### TPC-A 测试脚本

以下是基于 TPC-A 规范生成的测试 SQL 脚本，包括表结构创建、索引创建、示例数据插入及核心事务测试脚本。

这些脚本基于 **PostgreSQL** 语法编写。你可以根据实际使用的数据库（如 MySQL, Oracle）稍作调整。

#### 1. 表结构创建

TPC-A 主要包含一个 `accounts` 表和一个 `history` 表。

```sql
-- 创建账户表 (accounts)
CREATE TABLE accounts (
    acc_id         INTEGER      NOT NULL,
    acc_balance    DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    acc_fname      VARCHAR(20),
    acc_lname      VARCHAR(20),
    acc_pin        CHAR(4),
    acc_status     CHAR(1)      NOT NULL DEFAULT 'A', -- 'A' for Active
    PRIMARY KEY (acc_id)
);

-- 创建历史表 (history)
-- 用于记录所有转账交易
CREATE TABLE history (
    hist_id        SERIAL       NOT NULL,
    hist_acc_id    INTEGER      NOT NULL,
    hist_date      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    hist_amount    DECIMAL(15,2) NOT NULL,
    hist_type      CHAR(1)      NOT NULL, -- 'D' for Deposit, 'W' for Withdrawal, 'T' for Transfer
    hist_src_acc_id INTEGER, -- Source account for transfers
    PRIMARY KEY (hist_id),
    FOREIGN KEY (hist_acc_id) REFERENCES accounts(acc_id),
    FOREIGN KEY (hist_src_acc_id) REFERENCES accounts(acc_id)
);
```

#### 2. 创建索引

为了优化查询性能，特别是在查询账户和历史记录时，需要创建索引。

```sql
-- 为 history 表创建索引，以加速按账户ID和日期的查询
CREATE INDEX idx_history_acc_id ON history(hist_acc_id);
CREATE INDEX idx_history_date ON history(hist_date);
```

#### 3. 生成测试数据

TPC-A 测试需要大量的账户数据。下面的脚本会生成 `N` 个账户，每个账户初始余额为 1000 美元。

```sql
-- 定义要生成的账户数量
-- 在实际测试中，这个数量通常非常大（例如 1,000,000）
\set num_accounts 100000

-- 使用 generate_series 快速生成数据
INSERT INTO accounts (acc_id, acc_balance, acc_fname, acc_lname, acc_pin)
SELECT
    generate_series(1, :num_accounts) AS acc_id,
    1000.00 AS acc_balance,
    'First' || generate_series(1, :num_accounts),
    'Last' || generate_series(1, :num_accounts),
    lpad(floor(random() * 10000)::TEXT, 4, '0') -- 生成随机4位PIN码
;

-- 确认数据插入
SELECT COUNT(*) FROM accounts;
```
**说明**：`\set num_accounts 100000` 是 PostgreSQL 的 `psql` 命令。如果你在其他客户端执行，可以直接在 `INSERT` 语句中替换 `:num_accounts` 为具体的数字，或者使用相应客户端的变量设置方法。

#### 4. 核心事务脚本 (New-Order)

这是 TPC-A 唯一的事务，它执行以下操作：
1.  从一个账户（源账户）扣除一定金额。
2.  向另一个账户（目标账户）添加相同的金额。
3.  在 `history` 表中为两个账户各记录一条交易记录。

```sql
-- TPC-A New-Order Transaction
-- 输入：源账户ID (src_acc_id), 目标账户ID (dest_acc_id), 转账金额 (amount)
-- 输出：成功或失败

BEGIN;

-- 定义变量 (在实际应用中，这些值由测试程序动态生成)
\set src_acc_id 1001
\set dest_acc_id 2002
\set amount 100.00

-- 1. 从源账户扣款
-- 使用 FOR UPDATE 锁定行，防止并发问题
UPDATE accounts
SET acc_balance = acc_balance - :amount
WHERE acc_id = :src_acc_id AND acc_balance >= :amount AND acc_status = 'A'
RETURNING acc_balance INTO _src_new_balance;

-- 检查扣款是否成功
GET DIAGNOSTICS updated_rows = ROW_COUNT;
IF updated_rows = 0 THEN
    ROLLBACK;
    RAISE NOTICE 'Transaction failed: Insufficient funds or account invalid for source account %', :src_acc_id;
END IF;

-- 2. 向目标账户存款
UPDATE accounts
SET acc_balance = acc_balance + :amount
WHERE acc_id = :dest_acc_id AND acc_status = 'A'
RETURNING acc_balance INTO _dest_new_balance;

-- 检查存款是否成功
GET DIAGNOSTICS updated_rows = ROW_COUNT;
IF updated_rows = 0 THEN
    ROLLBACK;
    RAISE NOTICE 'Transaction failed: Destination account % invalid', :dest_acc_id;
END IF;

-- 3. 记录交易历史 (源账户 - 支出)
INSERT INTO history (hist_acc_id, hist_amount, hist_type, hist_src_acc_id)
VALUES (:src_acc_id, :amount, 'W', NULL); -- 'W' for Withdrawal

-- 4. 记录交易历史 (目标账户 - 收入)
INSERT INTO history (hist_acc_id, hist_amount, hist_type, hist_src_acc_id)
VALUES (:dest_acc_id, :amount, 'D', :src_acc_id); -- 'D' for Deposit, src_acc_id indicates transfer source

COMMIT;
RAISE NOTICE 'Transaction successful. Transferred $% from account % to account %. New balances: % -> $%, % -> $%',
    :amount, :src_acc_id, :dest_acc_id,
    :src_acc_id, _src_new_balance,
    :dest_acc_id, _dest_new_balance;

```
**说明**：
*   这是一个 PL/pgSQL 代码块。你可以将其放在一个函数中，或者在 `psql` 中使用 `\i` 命令执行。
*   `BEGIN;` 和 `COMMIT;` 确保了事务的原子性。如果任何一步失败，`ROLLBACK;` 会撤销所有更改。
*   `FOR UPDATE` 子句在 `SELECT ... FOR UPDATE` 中使用，但在这个简化的 `UPDATE` 版本中，PostgreSQL 会自动对被更新的行加锁，直到事务结束。这对于保证并发转账的正确性至关重要。

---

### 如何运行 TPC-A 测试

1.  **环境准备**：
  *   安装并配置好你的数据库（如 PostgreSQL）。
  *   创建一个新的数据库（如 `tpca_db`）。

2.  **执行初始化脚本**：
  *   连接到数据库，执行**第 1 步（表结构）**和**第 2 步（索引）**的脚本。

3.  **生成测试数据**：
  *   执行**第 3 步（生成测试数据）**的脚本。根据你的测试需求调整 `num_accounts` 的值。生成大量数据可能需要一些时间。

4.  **执行事务**：
  *   **手动测试**：你可以修改**第 4 步（核心事务脚本）**中的 `src_acc_id`, `dest_acc_id`, 和 `amount`，然后反复执行来观察效果。
  *   **自动化测试（推荐）**：TPC-A 的真正价值在于测试高并发下的性能。手动执行无法模拟这种场景。你需要一个测试驱动程序，例如：
    *   **编写代码**：使用 Java (JDBC), Python (psycopg2), Go 等语言编写一个多线程程序。
    *   **核心逻辑**：
      1.  程序启动大量线程。
      2.  每个线程循环执行以下操作：
          a.  随机生成一个源账户 ID、一个目标账户 ID（确保两者不同）和一个转账金额（例如，1 到 1000 美元之间）。
          b.  连接到数据库。
          c.  执行**第 4 步**中的事务逻辑。
          d.  记录事务执行时间。
      3.  测试运行一段时间后，程序停止。
      4.  计算并输出总的 **TPS (Transactions Per Second)** 和平均事务响应时间。

### 关键性能指标

*   **TPS (Transactions Per Second)**：这是 TPC-A 最主要的指标，表示系统每秒能够成功完成的转账事务数量。数值越高，说明数据库处理并发事务的能力越强。
*   **平均事务响应时间 (Average Transaction Response Time)**：完成一个事务的平均时间（通常以毫秒为单位）。这个指标反映了系统的响应速度。

通过以上脚本和步骤，你可以搭建一个基础的 TPC-A 测试环境，并对数据库的 OLTP 性能有一个直观的认识。对于更专业和准确的测试，建议使用成熟的开源或商业基准测试工具。
