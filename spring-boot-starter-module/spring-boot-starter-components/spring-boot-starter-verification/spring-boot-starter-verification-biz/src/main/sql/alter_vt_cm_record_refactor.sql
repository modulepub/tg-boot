-- 【增量】内容审核记录表字段重构：是否通过 + 是否异步 + 流程状态 + 备注
-- 若已按旧版 create_vt_cm_record.sql 建表，执行本脚本迁移（列已存在时请跳过对应 ADD）

ALTER TABLE `vt_cm_record`
  ADD COLUMN `cm_record_async_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否异步：1是 0否' AFTER `cm_record_passed_status_code`;

ALTER TABLE `vt_cm_record`
  ADD COLUMN `cm_record_process_code` varchar(4) NULL DEFAULT '0' COMMENT '流程：0待审核 1审核中 2审核结束' AFTER `cm_record_async_status_code`;

ALTER TABLE `vt_cm_record`
  ADD COLUMN `cm_record_remark` text NULL COMMENT '备注（第三方插件原始结果或人工驳回说明）' AFTER `cm_record_process_code`;

ALTER TABLE `vt_cm_record`
  ADD COLUMN `cm_record_audit_by` varchar(64) NULL DEFAULT NULL COMMENT '人工审核人用户编码' AFTER `cm_record_vendor_trace_id`;

ALTER TABLE `vt_cm_record`
  ADD COLUMN `cm_record_audit_at` datetime NULL DEFAULT NULL COMMENT '人工审核时间' AFTER `cm_record_audit_by`;

-- 旧版数据迁移（存在 cm_record_suggest_code 等列时执行 UPDATE；新表可跳过）
UPDATE `vt_cm_record`
SET `cm_record_process_code` = CASE
  WHEN `cm_record_passed_status_code` = '2' THEN '1'
  WHEN `cm_record_passed_status_code` IN ('1', '0', 'E') THEN '2'
  ELSE '0'
END,
`cm_record_async_status_code` = CASE WHEN `cm_record_passed_status_code` = '2' THEN '1' ELSE '0' END,
`cm_record_passed_status_code` = CASE
  WHEN `cm_record_passed_status_code` = '1' THEN '1'
  WHEN `cm_record_passed_status_code` IN ('0', 'E') THEN '0'
  ELSE NULL
END
WHERE EXISTS (
  SELECT 1 FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vt_cm_record' AND COLUMN_NAME = 'cm_record_suggest_code'
);

UPDATE `vt_cm_record`
SET `cm_record_remark` = COALESCE(`cm_record_vendor_raw`, `cm_record_vendor_message`)
WHERE EXISTS (
  SELECT 1 FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'vt_cm_record' AND COLUMN_NAME = 'cm_record_vendor_raw'
)
  AND (`cm_record_remark` IS NULL OR `cm_record_remark` = '');

ALTER TABLE `vt_cm_record`
  MODIFY COLUMN `cm_record_passed_status_code` varchar(4) NULL DEFAULT NULL COMMENT '是否通过：1是 0否';

-- 旧列清理（确认无依赖后执行）
-- ALTER TABLE `vt_cm_record` DROP COLUMN `cm_record_suggest_code`;
-- ALTER TABLE `vt_cm_record` DROP COLUMN `cm_record_label_code`;
-- ALTER TABLE `vt_cm_record` DROP COLUMN `cm_record_vendor_message`;
-- ALTER TABLE `vt_cm_record` DROP COLUMN `cm_record_vendor_raw`;

CREATE INDEX `idx_vt_cm_record_process_code` ON `vt_cm_record` (`cm_record_process_code`);
