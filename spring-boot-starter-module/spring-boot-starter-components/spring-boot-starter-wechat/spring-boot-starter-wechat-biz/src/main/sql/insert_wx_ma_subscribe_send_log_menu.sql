-- 微信模块管理端菜单：订阅消息发送记录（上线库执行一次）
-- 结构：manage -> wxSystem -> wxWxMaSubscribeSendLogIndex

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00355d013001', 9, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'manage', 'wxSystem', '微信能力', 'wx', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'wxSystem' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00355d013002', 1, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'wxSystem', 'wxWxMaSubscribeSendLogIndex', '订阅消息发送记录', 'wx/wxMaSubscribeSendLog/index', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'wxWxMaSubscribeSendLogIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00355d013101', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'wxSystem', 'admin', 'a1b2c3d4e5f011f0b6af00355d013201', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'wxSystem' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00355d013102', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'wxWxMaSubscribeSendLogIndex', 'admin', 'a1b2c3d4e5f011f0b6af00355d013202', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'wxWxMaSubscribeSendLogIndex' AND `deleted` = '0');
