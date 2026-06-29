-- 系统管理-账号注销申请菜单
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0ccc011f0b6af00155d01120a', 3, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'setting', 'sysUserCancellationIndex', '账号注销',
  'sys/userCancellation/index', NULL, '0', '0', 'icon-user'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysUserCancellationIndex'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0ccc111f0b6af00155d01120a', 1, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserCancellationIndex', 'sysUserCancellationList', '查看',
  '', 'system:sysUserCancellation:list', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysUserCancellationList'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0ccc211f0b6af00155d01120a', 2, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserCancellationIndex', 'sysUserCancellationProcess', '已处理',
  '', 'system:sysUserCancellation:process', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysUserCancellationProcess'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0ccc311f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserCancellationIndex', 'admin', 'rp_f1a2b3c0ccc311f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysUserCancellationIndex' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0ccc411f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserCancellationProcess', 'admin', 'rp_f1a2b3c0ccc411f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysUserCancellationProcess' AND `role_code` = 'admin'
);
