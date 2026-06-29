-- 结算记录冗余商品分佣比例快照
ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `td_gd_commission_rate` decimal(8, 4) NOT NULL DEFAULT 0.9000 COMMENT '分佣比例（冗余）' AFTER `td_gd_name`;

UPDATE `dist_user_bill_settle_record` SET `td_gd_commission_rate` = 0.9000 WHERE `td_gd_commission_rate` IS NULL;
