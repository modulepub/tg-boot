-- dist_user_bill_summary：dist_sys_user_code 重命名为 dist_user_code
-- 若建表脚本已使用 dist_user_code 则无需执行

ALTER TABLE `dist_user_bill_summary`
  CHANGE COLUMN `dist_sys_user_code` `dist_user_code` varchar(64) NOT NULL COMMENT '用户编码';

ALTER TABLE `dist_user_bill_summary`
  DROP INDEX `uk_dist_user_bill_summary_user`,
  ADD UNIQUE INDEX `uk_dist_user_bill_summary_user`(`dist_biz_line_code`, `dist_user_code`);
