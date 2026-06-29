-- =============================================================================
-- 婚恋 mock：测试数据标记字段 + 后台菜单「系统测试」
-- 执行一次即可；字段脚本可重复执行（已存在则跳过）
-- 菜单路径：婚恋系统 -> 系统测试 -> dating/systemTest/index
-- 执行后请重新登录后台以刷新菜单
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 增加「是否测试」字段（test_status_code：1=是，0=否）
-- -----------------------------------------------------------------------------

SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaking_company' AND COLUMN_NAME = 'mk_company_test_status_code') = 0,
    'ALTER TABLE `dt_matchmaking_company` ADD COLUMN `mk_company_test_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否测试数据：1是 0否'' AFTER `mk_company_audit_at`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaker' AND COLUMN_NAME = 'mk_test_status_code') = 0,
    'ALTER TABLE `dt_matchmaker` ADD COLUMN `mk_test_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否测试数据：1是 0否'' AFTER `mk_score`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_customer' AND COLUMN_NAME = 'cus_test_status_code') = 0,
    'ALTER TABLE `dt_customer` ADD COLUMN `cus_test_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否测试数据：1是 0否'' AFTER `cus_ls_status_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'sys_user' AND COLUMN_NAME = 'user_test_status_code') = 0,
    'ALTER TABLE `sys_user` ADD COLUMN `user_test_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否测试数据：1是 0否'' AFTER `user_im_syn_status_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_cus_matchmaker_rel' AND COLUMN_NAME = 'cus_mk_rel_test_status_code') = 0,
    'ALTER TABLE `dt_cus_matchmaker_rel` ADD COLUMN `cus_mk_rel_test_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否测试数据：1是 0否'' AFTER `cus_phone`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'td_goods' AND COLUMN_NAME = 'td_gd_test_status_code') = 0,
    'ALTER TABLE `td_goods` ADD COLUMN `td_gd_test_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否测试数据：1是 0否'' AFTER `td_gd_commission_rate`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- -----------------------------------------------------------------------------
-- 2. 菜单：婚恋系统(datingSystem) -> 系统测试
-- -----------------------------------------------------------------------------

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

-- 为所有已拥有「婚恋系统 / 红娘管理 / 公司管理」菜单的角色授权
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
