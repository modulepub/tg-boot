-- 微信模块管理端菜单：公众号配置（上线库执行一次）
-- 结构：manage -> wxSystem -> wxWxMpConfigIndex

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'e4f5a6b7c8d911f0b6af00355d013007', 6, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'wxSystem', 'wxWxMpConfigIndex', '公众号配置', 'wx/wxMpConfig/index', NULL, '0', '0', 'icon-setting'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'wxWxMpConfigIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'e4f5a6b7c8d911f0b6af00355d013107', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'wxWxMpConfigIndex', 'admin', 'e4f5a6b7c8d911f0b6af00355d013207', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'wxWxMpConfigIndex' AND `deleted` = '0');
