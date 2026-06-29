-- 微信小程序订阅消息模板（后台可改 templateId / content，不可新增；新增模板由开发执行 seed SQL）
CREATE TABLE IF NOT EXISTS `wx_ma_subscribe_template` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `wx_ma_subscribe_template_code` varchar(64) NOT NULL COMMENT '模板编码（与业务侧常量一致）',
  `wx_ma_subscribe_template_id` varchar(128) NOT NULL COMMENT '微信订阅消息模板 ID',
  `wx_ma_subscribe_template_content` text NULL COMMENT '模板说明（场景名、字段映射等，供运营维护）',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `org_code` varchar(64) DEFAULT NULL,
  `version` varchar(32) DEFAULT NULL,
  `seq_no` bigint DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wx_ma_subscribe_template_code` (`wx_ma_subscribe_template_code`),
  KEY `idx_wx_ma_subscribe_template_id` (`wx_ma_subscribe_template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信小程序订阅消息模板';
