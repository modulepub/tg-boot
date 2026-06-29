-- 系统管理-APP配置菜单
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0appc011f0b6af00155d01120a', 5, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'setting', 'sysAppConfigIndex', 'APP配置',
  'sys/appConfig/index', NULL, '0', '0', 'icon-mobile'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysAppConfigIndex'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0appc111f0b6af00155d01120a', 1, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigIndex', 'sysAppConfigList', '查看',
  '', 'system:sysAppConfig:list', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysAppConfigList'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0appc211f0b6af00155d01120a', 2, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigIndex', 'sysAppConfigAdd', '新增',
  '', 'system:sysAppConfig:add', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysAppConfigAdd'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0appc311f0b6af00155d01120a', 3, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigIndex', 'sysAppConfigEdit', '修改',
  '', 'system:sysAppConfig:edit', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysAppConfigEdit'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0appc411f0b6af00155d01120a', 4, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigIndex', 'sysAppConfigDelete', '删除',
  '', 'system:sysAppConfig:delete', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysAppConfigDelete'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0appc511f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigIndex', 'admin', 'rp_f1a2b3c0appc511f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysAppConfigIndex' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0appc611f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigList', 'admin', 'rp_f1a2b3c0appc611f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysAppConfigList' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0appc711f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigAdd', 'admin', 'rp_f1a2b3c0appc711f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysAppConfigAdd' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0appc811f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigEdit', 'admin', 'rp_f1a2b3c0appc811f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysAppConfigEdit' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0appc911f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysAppConfigDelete', 'admin', 'rp_f1a2b3c0appc911f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysAppConfigDelete' AND `role_code` = 'admin'
);
