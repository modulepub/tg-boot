-- 微信小程序订阅消息发送日志（幂等 / 排查）
CREATE TABLE IF NOT EXISTS `wx_ma_subscribe_send_log` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `send_log_code` varchar(64) DEFAULT NULL COMMENT '业务编码',
  `idempotent_key` varchar(128) DEFAULT NULL COMMENT '幂等键',
  `to_open_id` varchar(128) DEFAULT NULL COMMENT '接收人 openId',
  `template_id` varchar(128) DEFAULT NULL COMMENT '模板 ID',
  `jump_page` varchar(512) DEFAULT NULL COMMENT '跳转小程序页面',
  `send_data_json` text COMMENT '发送内容 JSON',
  `send_status_code` varchar(32) DEFAULT NULL COMMENT '发送状态 yes/no',
  `wx_err_code` varchar(32) DEFAULT NULL COMMENT '微信错误码',
  `wx_err_msg` varchar(512) DEFAULT NULL COMMENT '微信错误信息',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `org_code` varchar(64) DEFAULT NULL,
  `version` varchar(32) DEFAULT NULL,
  `seq_no` bigint DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_wx_ma_subscribe_idempotent` (`idempotent_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信小程序订阅消息发送日志';
