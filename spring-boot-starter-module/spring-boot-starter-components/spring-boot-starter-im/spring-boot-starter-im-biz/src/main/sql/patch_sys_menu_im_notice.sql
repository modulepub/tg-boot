-- IM 管理端菜单（上线库执行一次；per_code 唯一，重复执行前请先确认是否已存在）
-- 结构：manage -> imSystem -> imImNoticeIndex -> 按钮权限

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011001', 7, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'manage', 'imSystem', 'IM系统', 'im', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imSystem' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011002', 1, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imSystem', 'imImNoticeIndex', 'IM全员通知', 'im/imNotice/index', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImNoticeIndex' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011003', 1, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImNoticeIndex', 'imImNoticeAdd', '新增', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImNoticeAdd' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011004', 2, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImNoticeIndex', 'imImNoticeModify', '修改', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImNoticeModify' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011005', 3, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImNoticeIndex', 'imImNoticeDelete', '删除', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImNoticeDelete' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011006', 4, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'imImNoticeIndex', 'imImNoticePublish', '全员发送', '', NULL, '1', '0', ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'imImNoticePublish' AND `deleted` = 0);

-- 为 admin 角色授权（按需调整 role_code；其他角色请在管理端「角色管理」中勾选）
INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011101', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imSystem', 'admin', 'a1b2c3d4e5f011f0b6af00155d011201', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imSystem' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011102', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImNoticeIndex', 'admin', 'a1b2c3d4e5f011f0b6af00155d011202', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImNoticeIndex' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011103', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImNoticeAdd', 'admin', 'a1b2c3d4e5f011f0b6af00155d011203', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImNoticeAdd' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011104', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImNoticeModify', 'admin', 'a1b2c3d4e5f011f0b6af00155d011204', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImNoticeModify' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011105', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImNoticeDelete', 'admin', 'a1b2c3d4e5f011f0b6af00155d011205', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImNoticeDelete' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'a1b2c3d4e5f011f0b6af00155d011106', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'imImNoticePublish', 'admin', 'a1b2c3d4e5f011f0b6af00155d011206', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'imImNoticePublish' AND `deleted` = '0');
