-- 用户账号注销申请表
-- 执行前请确认表不存在，避免重复执行报错

CREATE TABLE IF NOT EXISTS `sys_user_cancellation_apply` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `cancellation_code` varchar(64) NULL DEFAULT NULL COMMENT '注销申请编码',
  `user_code` varchar(64) NULL DEFAULT NULL COMMENT '用户编码',
  `user_phone` varchar(32) NULL DEFAULT NULL COMMENT '用户手机号（冗余）',
  `user_nick_name` varchar(100) NULL DEFAULT NULL COMMENT '用户昵称（冗余）',
  `cancellation_process_code` varchar(10) NULL DEFAULT '0' COMMENT '处理状态：0 待处理 1 已处理',
  `process_by` varchar(64) NULL DEFAULT NULL COMMENT '处理人用户编码',
  `process_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_user_cancellation_user_code`(`user_code` ASC) USING BTREE,
  INDEX `idx_sys_user_cancellation_process_code`(`cancellation_process_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户账号注销申请' ROW_FORMAT = DYNAMIC;
