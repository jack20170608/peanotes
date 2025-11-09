--init data
INSERT INTO accounts (acc_id, acc_balance, acc_fname, acc_lname, acc_pin)
SELECT
  generate_series(1, :num_accounts) AS acc_id,
  100000.00 AS acc_balance,
  'First' || generate_series(1, :num_accounts),
  'Last' || generate_series(1, :num_accounts),
  lpad(floor(random() * 10000)::TEXT, 4, '0') -- 生成随机4位PIN码
;
