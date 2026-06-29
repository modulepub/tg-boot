-- 客户表：审核流程字段（1待修改 2审核中 3审核通过）

SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_customer' AND COLUMN_NAME = 'cus_audit_process_code') = 0,
    'ALTER TABLE `dt_customer` ADD COLUMN `cus_audit_process_code` varchar(8) NULL DEFAULT NULL COMMENT ''审核流程：1待修改 2审核中 3审核通过'' AFTER `cus_comlete_profile_status_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE `dt_customer` SET `cus_audit_process_code` = '3' WHERE `cus_audit_process_code` IS NULL;
