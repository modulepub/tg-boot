-- 结算记录表增加结算批次编码及冗余批次结算状态
ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `dist_settle_batch_code` varchar(60) NULL DEFAULT NULL COMMENT '结算批次编码' AFTER `dist_settled_at`,
  ADD COLUMN `dist_settle_batch_status_code` varchar(32) NULL DEFAULT NULL COMMENT '结算批次状态（冗余）YES-已完成 NO-未完成' AFTER `dist_settle_batch_code`,
  ADD INDEX `idx_dist_bill_settle_batch`(`dist_settle_batch_code`);
