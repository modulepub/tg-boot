-- 用户账单汇总增加用户/邀请人真实姓名字段

ALTER TABLE `dist_user_bill_summary`
  ADD COLUMN `dist_user_real_name` varchar(100) NULL DEFAULT NULL COMMENT '用户真实姓名（冗余）' AFTER `dist_user_nick_name`,
  ADD COLUMN `dist_inviter_user_real_name` varchar(100) NULL DEFAULT NULL COMMENT '邀请人真实姓名（冗余）' AFTER `dist_inviter_user_nick_name`;

UPDATE `dist_user_bill_summary` s
LEFT JOIN `sys_user` u ON u.`user_code` = s.`dist_user_code` AND u.`deleted` = '0'
LEFT JOIN `sys_user` inv ON inv.`user_code` = u.`user_reference_user_code` AND inv.`deleted` = '0'
SET s.`dist_user_real_name` = NULLIF(TRIM(u.`user_real_name`), ''),
    s.`dist_inviter_user_real_name` = NULLIF(TRIM(inv.`user_real_name`), '')
WHERE u.`user_code` IS NOT NULL;
