-- 推荐表：用户从历史推荐列表删除标记（1=用户已删除不可见，0=可见）
SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_recommended' AND COLUMN_NAME = 'recommended_cus_del_status_code') = 0,
    'ALTER TABLE `dt_recommended` ADD COLUMN `recommended_cus_del_status_code` varchar(10) NULL DEFAULT ''0'' COMMENT ''客户删除状态：1 用户已删除 0 可见'' AFTER `recommended_match_score`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
