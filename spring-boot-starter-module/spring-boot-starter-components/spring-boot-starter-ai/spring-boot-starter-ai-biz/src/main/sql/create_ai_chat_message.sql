-- AI 对话消息表
CREATE TABLE IF NOT EXISTS `ai_chat_message` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `ai_chat_message_code` varchar(64) NOT NULL COMMENT '消息业务编码',
  `ai_chat_session_code` varchar(64) NOT NULL COMMENT '会话业务编码',
  `ai_chat_message_role_code` varchar(16) NOT NULL COMMENT '消息角色：system/user/assistant',
  `ai_chat_message_content` longtext NOT NULL COMMENT '消息内容',
  `ai_chat_message_sort_no` int NULL DEFAULT NULL COMMENT '消息排序号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_ai_chat_message_code`(`ai_chat_message_code` ASC) USING BTREE,
  INDEX `idx_ai_chat_message_session`(`ai_chat_session_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI 对话消息' ROW_FORMAT = DYNAMIC;
