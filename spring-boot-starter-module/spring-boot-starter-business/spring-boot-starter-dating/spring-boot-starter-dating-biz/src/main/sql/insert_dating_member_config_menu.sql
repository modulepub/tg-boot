-- 婚恋系统菜单：会员配置（菜单 + 角色授权）
-- 页面路径 dating/memberConfig/index，父级菜单 datingSystem。

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'd4e5f6a0bbb111f0b6af00155d01130a', 11, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'datingSystem', 'datingMemberConfigIndex', '会员配置',
  'dating/memberConfig/index', NULL, '0', '0', 'icon-vip'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'datingMemberConfigIndex' AND `deleted` = '0'
);

UPDATE `sys_permission`
SET
  `per_parent_code` = 'datingSystem',
  `per_name` = '会员配置',
  `per_url` = 'dating/memberConfig/index',
  `per_type_code` = '0',
  `per_open_style_code` = '0',
  `per_icon` = 'icon-vip',
  `seq_no` = 11,
  `deleted` = '0',
  `update_time` = NOW(),
  `update_by` = 'admin'
WHERE `per_code` = 'datingMemberConfigIndex';

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  CONCAT('d4e5f6a0bbb3', SUBSTRING(MD5(CONCAT('datingMemberConfigIndex', src.role_code)), 1, 26)),
  0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'datingMemberConfigIndex', src.role_code,
  CONCAT('rp_datingMemberConfig_', src.role_code)
FROM (
  SELECT DISTINCT `role_code`
  FROM `sys_role_permission`
  WHERE `deleted` = '0'
    AND `role_code` IS NOT NULL
    AND `role_code` <> ''
    AND `per_code` IN ('datingSystem', 'datingSystemTestIndex', 'datingDtMatchmakerIndex', 'datingDtMatchmakingCompanyIndex')
) src
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` rp
  WHERE rp.`deleted` = '0'
    AND rp.`per_code` = 'datingMemberConfigIndex'
    AND rp.`role_code` = src.`role_code`
);
