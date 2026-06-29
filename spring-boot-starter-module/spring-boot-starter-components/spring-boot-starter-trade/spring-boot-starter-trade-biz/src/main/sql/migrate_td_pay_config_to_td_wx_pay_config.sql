-- 自旧表迁移数据（在无外键依赖时执行；迁移后可删除 td_pay_config）
INSERT INTO `td_wx_pay_config` (
  `wx_pay_config_code`,
  `wx_pay_config_app_id`,
  `wx_pay_config_mch_id`,
  `wx_pay_config_api_v3_key`,
  `wx_pay_config_notify_url`,
  `wx_pay_config_private_key`,
  `wx_pay_config_private_cert`,
  `wx_pay_config_use_sandbox`,
  `wx_pay_config_enabled_code`,
  `wx_pay_config_remark`,
  `create_by`,
  `create_time`,
  `update_by`,
  `update_time`,
  `org_code`,
  `version`,
  `seq_no`,
  `deleted`
)
SELECT
  `pay_config_code`,
  `wx_app_id`,
  `wx_mch_id`,
  `wx_api_v3_key`,
  `wx_notify_url`,
  NULL,
  NULL,
  COALESCE(`wx_use_sandbox`, 0),
  COALESCE(NULLIF(TRIM(`td_pc_enabled_code`), ''), '1'),
  `remark`,
  `create_by`,
  `create_time`,
  `update_by`,
  `update_time`,
  `org_code`,
  `version`,
  `seq_no`,
  COALESCE(`deleted`, 0)
FROM `td_pay_config`
WHERE NOT EXISTS (
  SELECT 1 FROM `td_wx_pay_config` t WHERE t.`wx_pay_config_code` = `td_pay_config`.`pay_config_code`
);
