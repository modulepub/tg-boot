-- 【增量】资产认证菜单：修正父级（原误挂 setting，应挂 verificationSystem）
-- 若从未执行 insert_vt_asset_cert_menu.sql，请直接执行最新版 insert 脚本即可，无需本脚本
-- 执行后请重新登录后台或刷新菜单缓存

UPDATE `sys_permission`
SET
  `per_parent_code` = 'verificationSystem',
  `seq_no` = 2,
  `per_icon` = 'icon-safetycertificate',
  `update_time` = NOW()
WHERE `per_code` = 'vtAssetCertIndex'
  AND `deleted` = '0';

INSERT INTO `sys_role_permission` (
  `id`, `seq_no`, `org_code`, `update_by`, `update_time`, `create_by`, `create_time`,
  `version`, `deleted`, `per_code`, `role_code`, `role_per_code`
)
SELECT
  'a1b2c3d7vt011f0b6af00155d01120a', 0, NULL, NULL, NOW(), 'admin', NOW(),
  '1.0', '0', 'vtAssetCertList', 'admin', 'rp_a1b2c3d7vt011f0b6af00155d01120a'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'vtAssetCertList' AND `deleted` = '0')
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` WHERE `per_code` = 'vtAssetCertList' AND `role_code` = 'admin'
  );
