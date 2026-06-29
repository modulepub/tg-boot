-- 管理端菜单：结算批次
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f3700c0044b211f1ad9bf83dc624a801', 6, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionSystem', 'distributionDistSettleBatchIndex', '结算批次',
  'distribution/distSettleBatch/index', NULL, '0', '0', 'icon-file-text'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'distributionDistSettleBatchIndex'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f3700d0044b211f1ad9bf83dc624a801', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionDistSettleBatchIndex', 'admin', 'rp_f3700d0044b211f1ad9bf83dc624a801'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'distributionDistSettleBatchIndex' AND `role_code` = 'admin'
);
