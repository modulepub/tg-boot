-- 结算记录表冗余下单人信息
ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `td_od_sys_user_code` varchar(64) NULL DEFAULT NULL COMMENT '下单人用户编码（冗余）' AFTER `dist_payer_user_code`,
  ADD COLUMN `td_od_sys_user_real_name` varchar(100) NULL DEFAULT NULL COMMENT '下单人姓名（冗余）' AFTER `td_od_sys_user_code`;
