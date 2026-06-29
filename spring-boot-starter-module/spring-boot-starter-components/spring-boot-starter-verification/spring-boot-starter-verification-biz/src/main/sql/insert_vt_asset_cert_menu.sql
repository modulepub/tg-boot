-- 核验系统-资产认证审核菜单（与「二要素配置」同级，父级 verificationSystem）
-- 结构：manage -> verificationSystem -> vtAssetCertIndex

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'a1b2c3d0vt011f0b6af00155d01120a', 2, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'verificationSystem', 'vtAssetCertIndex', '资产认证',
  'verification/assetCert/index', NULL, '0', '0', 'icon-safetycertificate'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'vtAssetCertIndex'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'a1b2c3d1vt011f0b6af00155d01120a', 1, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertIndex', 'vtAssetCertList', '查看',
  '', 'verification:vtAssetCert:list', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'vtAssetCertList'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'a1b2c3d2vt011f0b6af00155d01120a', 2, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertIndex', 'vtAssetCertApprove', '审核通过',
  '', 'verification:vtAssetCert:approve', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'vtAssetCertApprove'
);

INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'a1b2c3d3vt011f0b6af00155d01120a', 3, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertIndex', 'vtAssetCertReject', '审核驳回',
  '', 'verification:vtAssetCert:reject', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'vtAssetCertReject'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'a1b2c3d4vt011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertIndex', 'admin', 'rp_a1b2c3d4vt011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'vtAssetCertIndex' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'a1b2c3d5vt011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertApprove', 'admin', 'rp_a1b2c3d5vt011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'vtAssetCertApprove' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'a1b2c3d6vt011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertReject', 'admin', 'rp_a1b2c3d6vt011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'vtAssetCertReject' AND `role_code` = 'admin'
);

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'a1b2c3d7vt011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertList', 'admin', 'rp_a1b2c3d7vt011f0b6af00155d01120a'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'vtAssetCertList' AND `role_code` = 'admin'
);
