-- 为已有 matchmaker 配置补充「今日权益使用」展示开关（默认显示）
UPDATE `sys_app_config`
SET `app_config_value` = JSON_SET(CAST(`app_config_value` AS JSON), '$.showBenefitUsage', true),
    `update_time` = NOW()
WHERE `app_config_key` = 'matchmaker'
  AND `deleted` = '0'
  AND JSON_EXTRACT(CAST(`app_config_value` AS JSON), '$.showBenefitUsage') IS NULL;
