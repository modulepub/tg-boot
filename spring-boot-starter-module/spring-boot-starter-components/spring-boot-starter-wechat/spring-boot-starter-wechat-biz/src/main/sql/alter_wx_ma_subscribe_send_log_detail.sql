-- 微信小程序订阅消息发送日志：补充跳转页与发送内容（已有表执行本脚本）
ALTER TABLE `wx_ma_subscribe_send_log`
    ADD COLUMN `jump_page` varchar(512) NULL DEFAULT NULL COMMENT '跳转小程序页面' AFTER `template_id`,
    ADD COLUMN `send_data_json` text NULL COMMENT '发送内容 JSON' AFTER `jump_page`;
