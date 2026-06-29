-- 结算记录表增加是否在服务期内字段
ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `dist_in_service_status_code` varchar(32) NOT NULL DEFAULT 'NO' COMMENT '是否在服务期内 YES-是 NO-否' AFTER `dist_settle_applied_status_code`;
