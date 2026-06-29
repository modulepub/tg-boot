-- 结算批次增加红娘用户编码（按红娘单独申请结算）
ALTER TABLE `dist_settle_batch`
  ADD COLUMN `dist_matchmaker_user_code` varchar(64) NULL DEFAULT NULL COMMENT '红娘用户编码（按红娘结算时填写）' AFTER `mk_company_admin_user_code`,
  ADD INDEX `idx_dist_settle_batch_matchmaker`(`mk_company_code`, `dist_matchmaker_user_code`, `dist_settled_status_code`);
