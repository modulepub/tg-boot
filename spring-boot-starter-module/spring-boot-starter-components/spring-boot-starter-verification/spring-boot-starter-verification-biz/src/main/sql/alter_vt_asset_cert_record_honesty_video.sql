-- 【增量】资产认证记录表：诚实守信录制视频（仅存认证模块，不同步客户表）
-- 前置：已执行 create_vt_asset_cert_record.sql
-- 执行前请确认列不存在，避免重复执行报错

ALTER TABLE `vt_asset_cert_record`
  ADD COLUMN `honesty_video_file` varchar(512) NULL DEFAULT NULL COMMENT '诚实守信录制视频文件' AFTER `marital_status_proof_photo`;
