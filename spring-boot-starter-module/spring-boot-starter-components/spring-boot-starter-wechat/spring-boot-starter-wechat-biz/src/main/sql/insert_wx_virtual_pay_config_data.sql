-- 初始化虚拟支付配置（示例小程序后台参数，上线库执行一次）
-- 沙箱 AppKey 请在小程序后台「虚拟支付-基础配置」查看后补全

INSERT INTO `wx_virtual_pay_config` (
  `wx_virtual_pay_config_code`,
  `wx_virtual_pay_config_app_id`,
  `wx_virtual_pay_config_offer_id`,
  `wx_virtual_pay_config_app_key_sandbox`,
  `wx_virtual_pay_config_app_key_prod`,
  `wx_virtual_pay_config_use_sandbox`,
  `wx_virtual_pay_config_enabled_code`,
  `wx_virtual_pay_config_remark`,
  `create_by`,
  `create_time`,
  `update_time`,
  `seq_no`,
  `deleted`
)
SELECT
  'demo-default',
  'wxxxxxxxxxxxxxxx',
  '1234567890',
  NULL,
  'your-prod-app-key-here',
  0,
  '1',
  '示例小程序虚拟支付默认配置',
  'admin',
  NOW(),
  NOW(),
  1,
  0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `wx_virtual_pay_config`
  WHERE `wx_virtual_pay_config_code` = 'demo-default' AND `deleted` = 0
);
