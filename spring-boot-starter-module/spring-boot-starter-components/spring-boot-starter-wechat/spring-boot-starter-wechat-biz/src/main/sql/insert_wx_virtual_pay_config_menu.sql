-- 微信模块管理端菜单：虚拟支付配置

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'c2d3e4f5a6b711f0b6af00355d013005', 4, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'wxSystem', 'wxWxVirtualPayConfigIndex', '虚拟支付配置', 'wx/wxVirtualPayConfig/index', NULL, '0', '0', 'icon-wallet'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'wxWxVirtualPayConfigIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'c2d3e4f5a6b711f0b6af00355d013105', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'wxWxVirtualPayConfigIndex', 'admin', 'c2d3e4f5a6b711f0b6af00355d013205', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'wxWxVirtualPayConfigIndex' AND `deleted` = '0');
