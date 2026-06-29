-- 婚恋系统菜单：系统测试（仅菜单部分）
-- 完整版请执行：dating_mock_test_fields_and_menu.sql

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'd4e5f6a0bbb011f0b6af00155d01130a', 10, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'datingSystem', 'datingSystemTestIndex', '系统测试',
  'dating/systemTest/index', NULL, '0', '0', 'icon-experiment'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'datingSystemTestIndex' AND `deleted` = '0'
);

UPDATE `sys_permission`
SET
  `per_parent_code` = 'datingSystem',
  `per_name` = '系统测试',
  `per_url` = 'dating/systemTest/index',
  `per_type_code` = '0',
  `per_open_style_code` = '0',
  `per_icon` = 'icon-experiment',
  `seq_no` = 10,
  `deleted` = '0',
  `update_time` = NOW(),
  `update_by` = 'admin'
WHERE `per_code` = 'datingSystemTestIndex';

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  CONCAT('d4e5f6a0bbb2', SUBSTRING(MD5(CONCAT('datingSystemTestIndex', src.role_code)), 1, 26)),
  0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'datingSystemTestIndex', src.role_code,
  CONCAT('rp_datingSystemTest_', src.role_code)
FROM (
  SELECT DISTINCT `role_code`
  FROM `sys_role_permission`
  WHERE `deleted` = '0'
    AND `role_code` IS NOT NULL
    AND `role_code` <> ''
    AND `per_code` IN ('datingSystem', 'datingDtMatchmakerIndex', 'datingDtMatchmakingCompanyIndex')
) src
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` rp
  WHERE rp.`deleted` = '0'
    AND rp.`per_code` = 'datingSystemTestIndex'
    AND rp.`role_code` = src.`role_code`
);
