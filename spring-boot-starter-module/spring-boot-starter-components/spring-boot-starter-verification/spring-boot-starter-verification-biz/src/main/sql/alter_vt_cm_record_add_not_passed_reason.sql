-- 【增量】内容审核记录：未通过原因
ALTER TABLE `vt_cm_record`
  ADD COLUMN `cm_record_not_passed_reason` varchar(512) NULL DEFAULT NULL COMMENT '未通过原因' AFTER `cm_record_passed_status_code`;
