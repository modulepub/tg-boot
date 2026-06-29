-- 核验系统-内容合法校验审核菜单（父级 verificationSystem）
-- 结构：manage -> verificationSystem -> cmRecordIndex

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'b1c2d3e0cm011f0b6af00155d01120a', 3, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'verificationSystem', 'cmRecordIndex', '内容审核',
  'verification/cmRecord/index', NULL, '0', '0', 'icon-audit'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'cmRecordIndex'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'b1c2d3e1cm011f0b6af00155d01120a', 1, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordIndex', 'cmRecordList', '查看',
  '', 'verification:cmRecord:list', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'cmRecordList'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'b1c2d3e2cm011f0b6af00155d01120a', 2, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordIndex', 'cmRecordApprove', '审核通过',
  '', 'verification:cmRecord:approve', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'cmRecordApprove'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'b1c2d3e3cm011f0b6af00155d01120a', 3, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordIndex', 'cmRecordReject', '审核驳回',
  '', 'verification:cmRecord:reject', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'cmRecordReject'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'b1c2d3e8cm011f0b6af00155d01120a', 4, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordIndex', 'cmRecordDelete', '批量删除',
  '', 'verification:cmRecord:delete', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'cmRecordDelete'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'b1c2d3e4cm011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordIndex', 'admin', 'rp_b1c2d3e4cm011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'cmRecordIndex' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'b1c2d3e9cm011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordDelete', 'admin', 'rp_b1c2d3e9cm011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'cmRecordDelete' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'b1c2d3e5cm011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordApprove', 'admin', 'rp_b1c2d3e5cm011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'cmRecordApprove' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'b1c2d3e6cm011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordReject', 'admin', 'rp_b1c2d3e6cm011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'cmRecordReject' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'b1c2d3e7cm011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'cmRecordList', 'admin', 'rp_b1c2d3e7cm011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'cmRecordList' AND `role_code` = 'admin'
);
