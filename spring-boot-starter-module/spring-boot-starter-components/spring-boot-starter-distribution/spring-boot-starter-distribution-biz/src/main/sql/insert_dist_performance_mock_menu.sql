-- 管理端菜单：业绩测试（生成/清除业绩测试数据）
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'a1b2c3d4dist011f0b6af00155d01130a', 9, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionSystem', 'distributionSystemTestIndex', '业绩测试',
  'distribution/systemTest/index', NULL, '0', '0', 'icon-experiment'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'distributionSystemTestIndex' AND `deleted` = '0'
);

UPDATE `sys_permission`
SET
  `per_parent_code` = 'distributionSystem',
  `per_name` = '业绩测试',
  `per_url` = 'distribution/systemTest/index',
  `per_type_code` = '0',
  `per_open_style_code` = '0',
  `per_icon` = 'icon-experiment',
  `seq_no` = 9,
  `deleted` = '0',
  `update_time` = NOW(),
  `update_by` = 'admin'
WHERE `per_code` = 'distributionSystemTestIndex';

-- 给所有已拥有分销相关菜单的角色授予「业绩测试」权限
INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  CONCAT('a1b2c3d4dist02', SUBSTRING(MD5(CONCAT('distributionSystemTestIndex', src.role_code)), 1, 26)),
  0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionSystemTestIndex', src.role_code,
  CONCAT('rp_distributionSystemTest_', src.role_code)
FROM (
  SELECT DISTINCT `role_code`
  FROM `sys_role_permission`
  WHERE `deleted` = '0'
    AND `role_code` IS NOT NULL
    AND `role_code` <> ''
    AND `per_code` IN ('distributionSystem', 'distributionDistUserBillSummaryIndex')
) src
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` rp
  WHERE rp.`deleted` = '0'
    AND rp.`per_code` = 'distributionSystemTestIndex'
    AND rp.`role_code` = src.`role_code`
);
