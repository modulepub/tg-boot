-- 管理端菜单：用户账单汇总
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f370080044b211f1ad9bf83dc624a801', 4, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionSystem', 'distributionDistUserBillSummaryIndex', '用户账单汇总',
  'distribution/distUserBillSummary/index', NULL, '0', '0', 'icon-file-text'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'distributionDistUserBillSummaryIndex'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f370090044b211f1ad9bf83dc624a801', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionDistUserBillSummaryIndex', 'admin', 'rp_f370090044b211f1ad9bf83dc624a801'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'distributionDistUserBillSummaryIndex' AND `role_code` = 'admin'
);
