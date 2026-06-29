-- IM 消息中心菜单（上线库执行一次）
INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011009', 3, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imSystem', 'imMessageCenterIndex', '消息中心', 'im/imMessageCenter/index', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imMessageCenterIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011109', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imMessageCenterIndex', 'admin', 'a1b2c3d4e5f011f0b6af00255d011209', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imMessageCenterIndex' AND `deleted` = '0');
