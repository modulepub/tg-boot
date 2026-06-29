-- 若已创建 im_user_sig，可先执行 im_user.sql 建表，再运行本脚本迁移数据后删除旧表

INSERT INTO `im_user` (
  `id`, `create_by`, `create_time`, `update_by`, `update_time`, `org_code`, `deleted`, `seq_no`, `version`,
  `im_user_code`, `im_user_user_code`, `im_user_sdk_app_id`, `im_user_sig_value`, `im_user_sig_expire_time`
)
SELECT
  `id`, `create_by`, `create_time`, `update_by`, `update_time`, `org_code`, `deleted`, `seq_no`, `version`,
  `im_user_sig_code`, `im_user_sig_user_code`, `im_user_sig_sdk_app_id`, `im_user_sig_value`, `im_user_sig_expire_time`
FROM `im_user_sig`
WHERE NOT EXISTS (
  SELECT 1 FROM `im_user` u WHERE u.`im_user_user_code` = `im_user_sig`.`im_user_sig_user_code`
);

DROP TABLE IF EXISTS `im_user_sig`;
