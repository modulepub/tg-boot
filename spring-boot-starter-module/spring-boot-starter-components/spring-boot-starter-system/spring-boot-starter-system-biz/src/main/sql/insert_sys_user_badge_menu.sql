-- 系统管理-用户角标菜单
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0badge011f0b6af00155d01120a', 4, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'setting', 'sysUserBadgeIndex', '用户角标',
  'sys/userBadge/index', NULL, '0', '0', 'icon-bell'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysUserBadgeIndex'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f1a2b3c0badge111f0b6af00155d01120a', 1, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserBadgeIndex', 'sysUserBadgeList', '查看',
  '', 'system:sysUserBadge:list', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'sysUserBadgeList'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0badge211f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserBadgeIndex', 'admin', 'rp_f1a2b3c0badge211f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysUserBadgeIndex' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f1a2b3c0badge311f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'sysUserBadgeList', 'admin', 'rp_f1a2b3c0badge311f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'sysUserBadgeList' AND `role_code` = 'admin'
);
