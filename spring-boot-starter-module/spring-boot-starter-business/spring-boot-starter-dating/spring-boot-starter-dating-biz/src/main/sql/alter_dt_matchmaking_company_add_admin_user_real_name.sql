-- 婚介公司：冗余管理员真实姓名（列表展示用）

SET @db = DATABASE();

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'dt_matchmaking_company' AND COLUMN_NAME = 'mk_company_admin_user_real_name') = 0,
    'ALTER TABLE `dt_matchmaking_company` ADD COLUMN `mk_company_admin_user_real_name` varchar(64) NULL DEFAULT NULL COMMENT ''管理员真实姓名（冗余）'' AFTER `mk_company_admin_user_code`',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE dt_matchmaking_company c
INNER JOIN sys_user u ON c.mk_company_admin_user_code = u.user_code AND u.deleted = '0'
SET c.mk_company_admin_user_real_name = COALESCE(NULLIF(TRIM(u.user_real_name), ''), u.user_name)
WHERE c.deleted = '0'
  AND c.mk_company_admin_user_code IS NOT NULL
  AND TRIM(c.mk_company_admin_user_code) <> '';
