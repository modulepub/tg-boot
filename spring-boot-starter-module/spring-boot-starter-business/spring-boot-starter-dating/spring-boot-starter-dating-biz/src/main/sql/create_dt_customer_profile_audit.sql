-- 客户资料审核明细表 dt_customer_profile_audit
-- 方案B：废弃整行快照编辑表 dt_customer_profile_edit，改为「按 用户 + 字段 + 子项」存放待审核值与审核状态。
-- 本脚本兼容由旧表 dt_customer_profile_edit_audit 升级，并最终删除 dt_customer_profile_edit。

SET @db = DATABASE();

-- 1) 旧审核明细表存在且新表不存在时，整表改名
SET @old_audit = (SELECT COUNT(*) FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_customer_profile_edit_audit');
SET @new_audit = (SELECT COUNT(*) FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_customer_profile_audit');
SET @sql = IF(@old_audit > 0 AND @new_audit = 0,
    'RENAME TABLE `dt_customer_profile_edit_audit` TO `dt_customer_profile_audit`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) 新表不存在则直接创建
SET @new_audit = (SELECT COUNT(*) FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_customer_profile_audit');
SET @sql = IF(@new_audit = 0,
    'CREATE TABLE `dt_customer_profile_audit` (
      `id` varchar(64) NOT NULL COMMENT ''主键ID'',
      `seq_no` int NULL DEFAULT NULL COMMENT ''序列编号'',
      `org_code` varchar(64) NULL DEFAULT NULL COMMENT ''机构编码'',
      `update_by` varchar(64) NULL DEFAULT NULL COMMENT ''更新人'',
      `update_time` datetime NULL DEFAULT NULL COMMENT ''更新时间'',
      `create_by` varchar(64) NULL DEFAULT NULL COMMENT ''创建人'',
      `create_time` datetime NULL DEFAULT NULL COMMENT ''创建时间'',
      `version` varchar(10) NULL DEFAULT NULL COMMENT ''乐观锁版本号'',
      `deleted` varchar(10) NULL DEFAULT ''0'' COMMENT ''逻辑删除'',
      `cus_profile_audit_code` varchar(64) NOT NULL COMMENT ''资料审核业务编码'',
      `cus_user_code` varchar(64) NOT NULL COMMENT ''所属用户编码'',
      `cus_profile_audit_field_name` varchar(64) NOT NULL COMMENT ''客户字段名（Java 属性）'',
      `cus_profile_audit_field_item_index` int NULL DEFAULT NULL COMMENT ''多值字段子项序号'',
      `cus_profile_audit_pending_value` text NULL COMMENT ''该字段/子项提交的待审核值'',
      `cm_record_code` varchar(64) NULL DEFAULT NULL COMMENT ''内容审核记录业务编码'',
      `cus_profile_audit_process_code` varchar(8) NULL DEFAULT NULL COMMENT ''审核流程：0待审核 1审核中 2结束'',
      `cus_profile_audit_passed_status_code` varchar(8) NULL DEFAULT NULL COMMENT ''是否通过：1是 0否'',
      `cus_profile_audit_not_passed_tip` varchar(512) NULL DEFAULT NULL COMMENT ''未通过提示'',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_cus_profile_audit_code` (`cus_profile_audit_code`),
      KEY `idx_cus_profile_audit_user` (`cus_user_code`),
      KEY `idx_cus_profile_audit_cm` (`cm_record_code`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''客户资料审核明细''',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) 列改名（由旧表升级而来时）
SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_audit_code')
    AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_code'),
    'ALTER TABLE `dt_customer_profile_audit` CHANGE COLUMN `cus_profile_edit_audit_code` `cus_profile_audit_code` varchar(64) NOT NULL COMMENT ''资料审核业务编码''',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_audit_field_name')
    AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_field_name'),
    'ALTER TABLE `dt_customer_profile_audit` CHANGE COLUMN `cus_profile_edit_audit_field_name` `cus_profile_audit_field_name` varchar(64) NOT NULL COMMENT ''客户字段名（Java 属性）''',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_audit_field_item_index')
    AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_field_item_index'),
    'ALTER TABLE `dt_customer_profile_audit` CHANGE COLUMN `cus_profile_edit_audit_field_item_index` `cus_profile_audit_field_item_index` int NULL DEFAULT NULL COMMENT ''多值字段子项序号''',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_audit_process_code')
    AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_process_code'),
    'ALTER TABLE `dt_customer_profile_audit` CHANGE COLUMN `cus_profile_edit_audit_process_code` `cus_profile_audit_process_code` varchar(8) NULL DEFAULT NULL COMMENT ''审核流程：0待审核 1审核中 2结束''',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_audit_passed_status_code')
    AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_passed_status_code'),
    'ALTER TABLE `dt_customer_profile_audit` CHANGE COLUMN `cus_profile_edit_audit_passed_status_code` `cus_profile_audit_passed_status_code` varchar(8) NULL DEFAULT NULL COMMENT ''是否通过：1是 0否''',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_audit_not_passed_tip')
    AND NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_not_passed_tip'),
    'ALTER TABLE `dt_customer_profile_audit` CHANGE COLUMN `cus_profile_edit_audit_not_passed_tip` `cus_profile_audit_not_passed_tip` varchar(512) NULL DEFAULT NULL COMMENT ''未通过提示''',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) 新增 cus_user_code 列
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_user_code'),
    'ALTER TABLE `dt_customer_profile_audit` ADD COLUMN `cus_user_code` varchar(64) NULL DEFAULT NULL COMMENT ''所属用户编码'' AFTER `cus_profile_audit_code`',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5) 新增 cus_profile_audit_pending_value 列
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_pending_value'),
    'ALTER TABLE `dt_customer_profile_audit` ADD COLUMN `cus_profile_audit_pending_value` text NULL COMMENT ''该字段/子项提交的待审核值'' AFTER `cus_profile_audit_field_item_index`',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 6) 由旧编辑表回填 cus_user_code（旧明细通过 cus_profile_edit_code 关联编辑表）
SET @has_old_link = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_code');
SET @has_edit_tbl = (SELECT COUNT(*) FROM information_schema.TABLES
    WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_edit');
SET @sql = IF(@has_old_link > 0 AND @has_edit_tbl > 0,
    'UPDATE `dt_customer_profile_audit` a JOIN `dt_customer_profile_edit` e ON a.`cus_profile_edit_code` = e.`cus_profile_edit_code` SET a.`cus_user_code` = e.`cus_user_code` WHERE a.`cus_user_code` IS NULL',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 7) 删除旧的会话关联列 cus_profile_edit_code（新模型不再使用）
SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_edit_code'),
    'ALTER TABLE `dt_customer_profile_audit` DROP COLUMN `cus_profile_edit_code`',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 8) 索引补齐：业务编码唯一键
SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND COLUMN_NAME='cus_profile_audit_code')
    AND NOT EXISTS(SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND INDEX_NAME='uk_cus_profile_audit_code'),
    'ALTER TABLE `dt_customer_profile_audit` ADD UNIQUE INDEX `uk_cus_profile_audit_code` (`cus_profile_audit_code`)',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 9) 索引补齐：用户维度
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND INDEX_NAME='idx_cus_profile_audit_user'),
    'ALTER TABLE `dt_customer_profile_audit` ADD INDEX `idx_cus_profile_audit_user` (`cus_user_code`)',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 10) 索引补齐：内容审核记录维度
SET @sql = (SELECT IF(
    NOT EXISTS(SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_audit' AND INDEX_NAME='idx_cus_profile_audit_cm'),
    'ALTER TABLE `dt_customer_profile_audit` ADD INDEX `idx_cus_profile_audit_cm` (`cm_record_code`)',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 11) 丢弃旧的整行快照编辑表
SET @sql = (SELECT IF(
    EXISTS(SELECT 1 FROM information_schema.TABLES WHERE TABLE_SCHEMA=@db AND TABLE_NAME='dt_customer_profile_edit'),
    'DROP TABLE `dt_customer_profile_edit`',
    'SELECT 1'));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
