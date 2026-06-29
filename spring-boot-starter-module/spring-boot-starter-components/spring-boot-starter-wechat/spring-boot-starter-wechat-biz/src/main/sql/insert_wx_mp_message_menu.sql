-- 微信模块管理端菜单：公众号消息（上线库执行一次）
-- 结构：manage -> wxSystem -> wxWxMpMessageIndex

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'f5a6b7c8d9e011f0b6af00355d013008', 7, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'wxSystem', 'wxWxMpMessageIndex', '公众号消息', 'wx/wxMpMessage/index', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'wxWxMpMessageIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'f5a6b7c8d9e011f0b6af00355d013108', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'wxWxMpMessageIndex', 'admin', 'f5a6b7c8d9e011f0b6af00355d013208', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'wxWxMpMessageIndex' AND `deleted` = '0');
