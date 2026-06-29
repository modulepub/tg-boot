-- 红娘视频号：审核流程（ProcessCode）与驳回原因

SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaker' AND COLUMN_NAME = 'mk_channels_process_code') = 0,
    'ALTER TABLE `dt_matchmaker` ADD COLUMN `mk_channels_process_code` varchar(8) NULL DEFAULT NULL COMMENT ''视频号审核流程（ProcessCode：0待提交 1待审核 2审核通过 3审核失败）'' AFTER `mk_channels_audit_status_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaker' AND COLUMN_NAME = 'mk_channels_reject_reason') = 0,
    'ALTER TABLE `dt_matchmaker` ADD COLUMN `mk_channels_reject_reason` varchar(500) NULL DEFAULT NULL COMMENT ''视频号审核失败原因'' AFTER `mk_channels_process_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaker' AND COLUMN_NAME = 'mk_channels_audit_by') = 0,
    'ALTER TABLE `dt_matchmaker` ADD COLUMN `mk_channels_audit_by` varchar(64) NULL DEFAULT NULL COMMENT ''视频号审核人'' AFTER `mk_channels_reject_reason`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaker' AND COLUMN_NAME = 'mk_channels_audit_at') = 0,
    'ALTER TABLE `dt_matchmaker` ADD COLUMN `mk_channels_audit_at` datetime NULL DEFAULT NULL COMMENT ''视频号审核时间'' AFTER `mk_channels_audit_by`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 历史已生效数据回填为审核通过
UPDATE `dt_matchmaker`
SET `mk_channels_process_code` = '2'
WHERE `mk_channels_audit_status_code` = '1'
  AND TRIM(IFNULL(`mk_channels_finder_user_name`, '')) <> ''
  AND (`mk_channels_process_code` IS NULL OR TRIM(`mk_channels_process_code`) = '');
