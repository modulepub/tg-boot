-- 虚拟支付：补全沙箱 AppKey 并启用沙箱环境（开发者工具调试）
-- 执行后请在管理端点击「刷新运行时配置」，或重启应用

UPDATE `wx_virtual_pay_config`
SET
  `wx_virtual_pay_config_app_key_sandbox` = 'your-sandbox-app-key-here',
  `wx_virtual_pay_config_use_sandbox` = 1,
  `update_by` = 'admin',
  `update_time` = NOW()
WHERE `wx_virtual_pay_config_code` = 'demo-default'
  AND `deleted` = 0;
