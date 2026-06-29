-- 企业入驻：是否已确认对公认证转账（1已转账 0未转账，null视同未转账）

SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaking_company' AND COLUMN_NAME = 'mk_company_transfer_status_code') = 0,
    'ALTER TABLE `dt_matchmaking_company` ADD COLUMN `mk_company_transfer_status_code` varchar(8) NULL DEFAULT ''0'' COMMENT ''是否已确认转账：1是 0否'' AFTER `mk_company_verify_skip_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
