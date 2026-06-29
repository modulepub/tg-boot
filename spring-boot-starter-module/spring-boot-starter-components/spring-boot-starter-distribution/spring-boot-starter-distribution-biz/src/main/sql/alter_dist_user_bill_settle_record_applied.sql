-- 结算记录表增加是否申请结算字段
ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `dist_settle_applied_status_code` varchar(32) NOT NULL DEFAULT 'NO' COMMENT '是否申请结算 YES-是 NO-否' AFTER `dist_settled_status_code`;
