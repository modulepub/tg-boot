-- 管理端菜单：用户账单结算记录
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'f3700a0044b211f1ad9bf83dc624a801', 5, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionSystem', 'distributionDistUserBillSettleRecordIndex', '用户账单结算记录',
  'distribution/distUserBillSettleRecord/index', NULL, '0', '0', 'icon-file-text'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'distributionDistUserBillSettleRecordIndex'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'f3700b0044b211f1ad9bf83dc624a801', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'distributionDistUserBillSettleRecordIndex', 'admin', 'rp_f3700b0044b211f1ad9bf83dc624a801'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'distributionDistUserBillSettleRecordIndex' AND `role_code` = 'admin'
);
