-- 公众号粉丝会话（按 openId + 公众号配置聚合）
CREATE TABLE IF NOT EXISTS `wx_mp_fan` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `wx_mp_fan_code` varchar(64) NOT NULL COMMENT '粉丝会话业务编码',
  `wx_mp_config_code` varchar(64) NOT NULL COMMENT '公众号配置编码',
  `wx_mp_fan_open_id` varchar(128) NOT NULL COMMENT '粉丝 OpenId',
  `wx_mp_fan_nickname` varchar(128) DEFAULT NULL COMMENT '昵称',
  `ai_chat_session_code` varchar(64) DEFAULT NULL COMMENT '关联 AI 对话会话编码',
  `wx_mp_fan_last_message_content` varchar(1024) DEFAULT NULL COMMENT '最近一条消息摘要',
  `wx_mp_fan_last_message_time` datetime DEFAULT NULL COMMENT '最近消息时间',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `org_code` varchar(64) DEFAULT NULL,
  `version` varchar(32) DEFAULT NULL,
  `seq_no` bigint DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wx_mp_fan_code` (`wx_mp_fan_code`),
  UNIQUE KEY `uk_wx_mp_fan_config_open_id` (`wx_mp_config_code`, `wx_mp_fan_open_id`),
  KEY `idx_wx_mp_fan_last_time` (`wx_mp_fan_last_message_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信公众号粉丝会话';
