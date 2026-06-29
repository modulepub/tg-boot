-- 用户账单汇总增加分佣金额字段
ALTER TABLE `dist_user_bill_summary`
  ADD COLUMN `dist_commission_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '间推分佣金额（上级5%奖励累计）' AFTER `dist_sub_credited_total_amount`,
  ADD COLUMN `dist_sub_commission_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '直推分佣金额（下级消费分佣累计）' AFTER `dist_commission_total_amount`;
