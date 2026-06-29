-- mock / 测试数据标记：1=是测试数据，0=否（null 视同否）
-- 完整版（含菜单）请执行：dating_mock_test_fields_and_menu.sql

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
