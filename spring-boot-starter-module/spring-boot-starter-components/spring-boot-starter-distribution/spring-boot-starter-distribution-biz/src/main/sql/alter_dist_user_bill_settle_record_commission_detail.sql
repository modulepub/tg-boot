-- 结算记录增加佣金明细
ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `dist_commission_pool_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '分佣池金额（付费×商品分佣比例）' AFTER `dist_paid_amount`,
  ADD COLUMN `dist_inviter_user_code` varchar(64) NULL DEFAULT NULL COMMENT '直推邀请人用户编码' AFTER `dist_commission_pool_amount`,
  ADD COLUMN `dist_inviter_commission_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '直推邀请人分佣金额' AFTER `dist_inviter_user_code`,
  ADD COLUMN `dist_superior_inviter_user_code` varchar(64) NULL DEFAULT NULL COMMENT '上级邀请人用户编码' AFTER `dist_inviter_commission_amount`,
  ADD COLUMN `dist_superior_commission_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '上级邀请人分佣金额（付费5%）' AFTER `dist_superior_inviter_user_code`,
  ADD COLUMN `dist_commission_applied_status_code` varchar(32) NOT NULL DEFAULT 'NO' COMMENT '分佣是否已计入汇总 YES-是 NO-否' AFTER `dist_superior_commission_amount`;
