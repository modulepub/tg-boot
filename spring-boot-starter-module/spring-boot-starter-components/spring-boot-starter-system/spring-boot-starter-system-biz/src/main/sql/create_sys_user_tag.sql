-- 用户标签表
-- 执行前请确认表不存在，避免重复执行报错

CREATE TABLE IF NOT EXISTS `sys_user_tag` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `user_tag_code` varchar(64) NULL DEFAULT NULL COMMENT '业务主键',
  `user_code` varchar(64) NULL DEFAULT NULL COMMENT '用户编码',
  `tag_code` varchar(64) NULL DEFAULT NULL COMMENT '标签编码',
  `tag_name` varchar(64) NULL DEFAULT NULL COMMENT '标签名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_tag_code`(`user_tag_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_tag_user_tag`(`user_code` ASC, `tag_code` ASC) USING BTREE,
  INDEX `idx_sys_user_tag_user_code`(`user_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户标签' ROW_FORMAT = DYNAMIC;
