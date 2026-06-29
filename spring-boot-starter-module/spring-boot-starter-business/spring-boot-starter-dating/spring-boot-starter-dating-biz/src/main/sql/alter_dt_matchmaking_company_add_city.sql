-- 婚介公司：所在城市编码与名称

SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaking_company' AND COLUMN_NAME = 'mk_company_city_code') = 0,
    'ALTER TABLE `dt_matchmaking_company` ADD COLUMN `mk_company_city_code` varchar(64) NULL DEFAULT NULL COMMENT ''所在城市编码'' AFTER `mk_company_address_detail`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaking_company' AND COLUMN_NAME = 'mk_company_city_name') = 0,
    'ALTER TABLE `dt_matchmaking_company` ADD COLUMN `mk_company_city_name` varchar(128) NULL DEFAULT NULL COMMENT ''所在城市名称'' AFTER `mk_company_city_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
