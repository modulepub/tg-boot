-- 初始化 matchmaker APP 配置（H5 跳转前缀）
INSERT INTO `sys_app_config` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `app_config_key`, `app_config_value`
)
SELECT
  'f1a2b3c0seed011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'matchmaker', '{"h5UrlPrefix":"https://h5.example.com","h5UrlUseHash":false,"showBenefitUsage":true}'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_app_config` WHERE `app_config_key` = 'matchmaker'
);
