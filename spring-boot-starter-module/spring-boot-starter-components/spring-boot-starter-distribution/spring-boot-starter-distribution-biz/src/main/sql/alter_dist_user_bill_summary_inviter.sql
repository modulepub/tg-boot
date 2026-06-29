-- dist_invitee_user_* 重命名为 dist_inviter_user_*，并恢复「用户 + 业务线」唯一索引
-- 若建表脚本已使用 dist_inviter_user_code 则仅需执行 DELETE 明细行与索引部分

-- 删除按被邀请人拆分的明细行（若存在）
DELETE FROM `dist_user_bill_summary`
WHERE `dist_invitee_user_code` IS NOT NULL AND `dist_invitee_user_code` <> '';

ALTER TABLE `dist_user_bill_summary`
  CHANGE COLUMN `dist_invitee_user_code` `dist_inviter_user_code` varchar(64) NULL DEFAULT NULL COMMENT '邀请人用户编码（冗余）',
  CHANGE COLUMN `dist_invitee_user_nick_name` `dist_inviter_user_nick_name` varchar(100) NULL DEFAULT NULL COMMENT '邀请人昵称（冗余）';

ALTER TABLE `dist_user_bill_summary`
  DROP INDEX `uk_dist_user_bill_summary_user`,
  ADD UNIQUE INDEX `uk_dist_user_bill_summary_user`(`dist_biz_line_code`, `dist_user_code`);

UPDATE `dist_user_bill_summary` s
LEFT JOIN `sys_user` u ON u.`user_code` = s.`dist_user_code` AND u.`deleted` = '0'
LEFT JOIN `sys_user` inv ON inv.`user_code` = u.`user_reference_user_code` AND inv.`deleted` = '0'
SET s.`dist_inviter_user_code` = NULLIF(TRIM(u.`user_reference_user_code`), ''),
    s.`dist_inviter_user_nick_name` = COALESCE(inv.`user_nick_name`, NULLIF(TRIM(u.`user_reference_user_code`), ''))
WHERE u.`user_code` IS NOT NULL;
