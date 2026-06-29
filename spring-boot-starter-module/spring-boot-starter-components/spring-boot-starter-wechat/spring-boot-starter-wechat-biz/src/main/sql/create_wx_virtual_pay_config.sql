-- 微信小程序虚拟支付配置（主键：wx_virtual_pay_config_code）
CREATE TABLE IF NOT EXISTS `wx_virtual_pay_config` (
  `wx_virtual_pay_config_code` varchar(64) NOT NULL COMMENT '虚拟支付配置编码（主键）',
  `wx_virtual_pay_config_app_id` varchar(64) NOT NULL COMMENT '小程序 AppId',
  `wx_virtual_pay_config_offer_id` varchar(64) NOT NULL COMMENT '米大师 OfferId',
  `wx_virtual_pay_config_app_key_sandbox` varchar(256) DEFAULT NULL COMMENT '沙箱 AppKey',
  `wx_virtual_pay_config_app_key_prod` varchar(256) DEFAULT NULL COMMENT '现网 AppKey',
  `wx_virtual_pay_config_use_sandbox` tinyint NOT NULL DEFAULT 0 COMMENT '是否沙箱：0-否，1-是',
  `wx_virtual_pay_config_notify_url` varchar(512) DEFAULT NULL COMMENT '发货/支付通知 URL（可选）',
  `wx_virtual_pay_config_enabled_code` varchar(32) DEFAULT '1' COMMENT '启用状态编码',
  `wx_virtual_pay_config_remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `org_code` varchar(64) DEFAULT NULL COMMENT '所属组织',
  `version` varchar(32) DEFAULT NULL COMMENT '版本',
  `seq_no` bigint DEFAULT NULL COMMENT '序号',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`wx_virtual_pay_config_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信小程序虚拟支付配置';
