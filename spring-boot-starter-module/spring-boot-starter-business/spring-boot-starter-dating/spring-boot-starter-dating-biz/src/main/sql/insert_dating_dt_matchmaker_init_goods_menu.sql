-- 红娘管理-一键初始化全部服务权限
INSERT INTO `sys_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`,
  `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`
)
SELECT
  'c4d5e6f0ddd511f0b6af00155d01120a', 7, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'datingDtMatchmakerIndex', 'datingDtMatchmakerInitGoods', '初始化全部服务',
  '', 'dating:dtMatchmaker:initGoods', '1', '0', ''
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_permission` WHERE `per_code` = 'datingDtMatchmakerInitGoods' AND `deleted` = 0
);

INSERT INTO `sys_role_permission` (
  `id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`,
  `per_code`, `role_code`, `role_per_code`, `org_code`
)
SELECT
  'c4d5e6f0ddd611f0b6af00155d01120a', '0', 0, NULL, 'admin', NOW(), NULL, NOW(),
  'datingDtMatchmakerInitGoods', 'admin', 'rp_c4d5e6f0ddd611f0b6af00155d01120a', NULL
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_role_permission`
  WHERE `role_code` = 'admin' AND `per_code` = 'datingDtMatchmakerInitGoods' AND `deleted` = '0'
);
