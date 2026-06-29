-- 微信小程序配置（后台维护，启动/刷新时加载到 WxMaService）
CREATE TABLE IF NOT EXISTS `wx_mini_config` (
  `wx_mini_config_code` varchar(64) NOT NULL COMMENT '配置编码（主键）',
  `wx_mini_config_name` varchar(128) DEFAULT NULL COMMENT '配置名称',
  `wx_mini_config_app_id` varchar(64) NOT NULL COMMENT '小程序 AppId',
  `wx_mini_config_app_secret` varchar(128) NOT NULL COMMENT '小程序 AppSecret',
  `wx_mini_config_msg_data_format` varchar(16) DEFAULT 'JSON' COMMENT '消息格式 JSON/XML',
  `wx_mini_config_enabled_code` varchar(8) DEFAULT '1' COMMENT '启用：1-是 0-否',
  `wx_mini_config_remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `org_code` varchar(64) DEFAULT NULL,
  `version` varchar(32) DEFAULT NULL,
  `seq_no` bigint DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`wx_mini_config_code`),
  KEY `idx_wx_mini_config_app_id` (`wx_mini_config_app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信小程序配置';
