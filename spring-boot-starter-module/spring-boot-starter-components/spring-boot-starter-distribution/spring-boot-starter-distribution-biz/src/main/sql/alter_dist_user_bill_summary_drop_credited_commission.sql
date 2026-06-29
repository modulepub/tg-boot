-- 移除账单汇总表「已入账」与「分佣」累计字段（改由结算明细 dist_user_bill_settle_record 承载）
ALTER TABLE `dist_user_bill_summary`
  DROP COLUMN `dist_credited_total_amount`,
  DROP COLUMN `dist_sub_credited_total_amount`,
  DROP COLUMN `dist_commission_total_amount`,
  DROP COLUMN `dist_sub_commission_total_amount`;
