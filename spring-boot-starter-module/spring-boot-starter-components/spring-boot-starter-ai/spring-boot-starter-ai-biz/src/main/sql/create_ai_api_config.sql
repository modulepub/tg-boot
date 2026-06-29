-- AI 接口配置表
CREATE TABLE IF NOT EXISTS `ai_api_config` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `ai_api_config_code` varchar(64) NOT NULL COMMENT 'AI 接口配置业务编码',
  `ai_api_config_name` varchar(128) NOT NULL COMMENT '配置名称',
  `ai_provider_code` varchar(32) NOT NULL COMMENT 'AI 提供商编码',
  `ai_api_config_base_url` varchar(512) NOT NULL COMMENT 'API Base URL（OpenAI 兼容）',
  `ai_api_config_api_key` varchar(512) NOT NULL COMMENT 'API Key',
  `ai_api_config_default_model` varchar(128) NOT NULL COMMENT '默认模型名称',
  `ai_api_config_input_price_per1k` decimal(18,8) NULL DEFAULT 0 COMMENT '输入单价（每1K tokens，元）',
  `ai_api_config_output_price_per1k` decimal(18,8) NULL DEFAULT 0 COMMENT '输出单价（每1K tokens，元）',
  `ai_api_config_enabled_code` varchar(8) NULL DEFAULT '1' COMMENT '启用：1-是 0-否',
  `ai_api_config_remark` varchar(512) NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_ai_api_config_code`(`ai_api_config_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 接口配置' ROW_FORMAT = DYNAMIC;
