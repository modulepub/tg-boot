-- AI 对话会话表
CREATE TABLE IF NOT EXISTS `ai_chat_session` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `ai_chat_session_code` varchar(64) NOT NULL COMMENT '会话业务编码',
  `user_code` varchar(64) NOT NULL COMMENT '用户编码',
  `ai_agent_code` varchar(64) NOT NULL COMMENT '智能体业务编码',
  `ai_chat_session_title` varchar(256) NULL DEFAULT NULL COMMENT '会话标题',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_ai_chat_session_code`(`ai_chat_session_code` ASC) USING BTREE,
  INDEX `idx_ai_chat_session_user`(`user_code` ASC) USING BTREE,
  INDEX `idx_ai_chat_session_agent`(`ai_agent_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 对话会话' ROW_FORMAT = DYNAMIC;
