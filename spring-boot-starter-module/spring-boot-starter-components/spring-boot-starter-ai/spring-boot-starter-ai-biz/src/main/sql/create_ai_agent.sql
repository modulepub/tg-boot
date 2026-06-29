-- AI 智能体表
CREATE TABLE IF NOT EXISTS `ai_agent` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `ai_agent_code` varchar(64) NOT NULL COMMENT '智能体业务编码',
  `ai_agent_name` varchar(128) NOT NULL COMMENT '智能体名称',
  `ai_agent_persona` longtext NULL COMMENT '人设/系统提示词',
  `ai_api_config_code` varchar(64) NULL DEFAULT NULL COMMENT '关联 AI 接口配置编码（可选）',
  `ai_agent_model` varchar(128) NULL DEFAULT NULL COMMENT '模型名称（为空则用接口默认）',
  `ai_agent_enabled_code` varchar(8) NULL DEFAULT '1' COMMENT '启用：1-是 0-否',
  `ai_agent_remark` varchar(512) NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_ai_agent_code`(`ai_agent_code` ASC) USING BTREE,
  INDEX `idx_ai_agent_api_config`(`ai_api_config_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 智能体' ROW_FORMAT = DYNAMIC;
