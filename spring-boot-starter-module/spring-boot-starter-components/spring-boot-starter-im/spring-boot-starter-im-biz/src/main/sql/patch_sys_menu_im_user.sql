-- IM 用户管理菜单（上线库执行一次）
-- 依赖 imSystem 父菜单（见 patch_sys_menu_im_notice.sql）

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011001', 2, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imSystem', 'imImUserIndex', 'IM用户管理', 'im/imUser/index', NULL, '0', '0', 'icon-user'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImUserIndex' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011002', 1, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImUserIndex', 'imImUserDelete', '删除', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImUserDelete' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011003', 2, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImUserIndex', 'imImUserRefresh', '拉取更新', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImUserRefresh' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011004', 3, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImUserIndex', 'imImUserSync', '同步用户', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImUserSync' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011005', 4, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImUserIndex', 'imImUserTag', '设置标签', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImUserTag' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011006', 5, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImUserIndex', 'imImUserSendMsg', '发消息', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImUserSendMsg' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011101', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImUserIndex', 'admin', 'a1b2c3d4e5f011f0b6af00255d011201', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImUserIndex' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011102', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImUserDelete', 'admin', 'a1b2c3d4e5f011f0b6af00255d011202', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImUserDelete' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011103', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImUserRefresh', 'admin', 'a1b2c3d4e5f011f0b6af00255d011203', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImUserRefresh' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011104', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImUserSync', 'admin', 'a1b2c3d4e5f011f0b6af00255d011204', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImUserSync' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011105', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImUserTag', 'admin', 'a1b2c3d4e5f011f0b6af00255d011205', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImUserTag' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00255d011106', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImUserSendMsg', 'admin', 'a1b2c3d4e5f011f0b6af00255d011206', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImUserSendMsg' AND `deleted` = '0');
