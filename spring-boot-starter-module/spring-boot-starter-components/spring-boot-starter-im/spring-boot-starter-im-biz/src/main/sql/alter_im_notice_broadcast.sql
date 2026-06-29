-- 已有 im_notice 表（旧库）时执行

ALTER TABLE `im_notice`
  MODIFY COLUMN `im_notice_name` varchar(128) DEFAULT NULL COMMENT '通知标题',
  MODIFY COLUMN `im_notice_url` varchar(2048) DEFAULT NULL COMMENT '跳转链接';

ALTER TABLE `im_notice`
  ADD COLUMN `im_notice_sender_user_code` varchar(64) DEFAULT NULL COMMENT '发送人IM用户编码' AFTER `im_notice_url`;

ALTER TABLE `im_notice`
  ADD COLUMN `im_notice_send_count` int DEFAULT 0 COMMENT '发送成功数' AFTER `im_notice_target_type_code`,
  ADD COLUMN `im_notice_fail_count` int DEFAULT 0 COMMENT '发送失败数' AFTER `im_notice_send_count`;

ALTER TABLE `im_notice_recipient`
  ADD COLUMN `im_nc_rp_user_code` varchar(64) DEFAULT NULL COMMENT '接收人IM用户编码' AFTER `im_notice_code`;
