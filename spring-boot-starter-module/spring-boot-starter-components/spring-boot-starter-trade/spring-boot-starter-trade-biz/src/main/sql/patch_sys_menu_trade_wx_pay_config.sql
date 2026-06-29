-- 已上线库：微信支付配置菜单与新路由对齐（任选执行一次）
UPDATE `sys_permission`
SET `per_code` = 'tradeTdWxPayConfigIndex',
    `per_url` = 'trade/pay/tdWxPayConfig/index'
WHERE `id` = 'f352b01044b211f1ad9bf83dc624a707';

UPDATE `sys_role_permission`
SET `per_code` = 'tradeTdWxPayConfigIndex'
WHERE `id` = 'f352c30144b211f1ad9bf83dc624a707';
