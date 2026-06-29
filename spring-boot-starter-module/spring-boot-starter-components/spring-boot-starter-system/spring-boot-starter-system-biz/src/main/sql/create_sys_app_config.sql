-- APP 配置表
-- 执行前请确认表不存在，避免重复执行报错

CREATE TABLE IF NOT EXISTS `sys_app_config` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `app_config_key` varchar(64) NOT NULL COMMENT '配置 key',
  `app_config_value` text NULL COMMENT '配置值（JSON）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_app_config_key`(`app_config_key` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'APP配置' ROW_FORMAT = DYNAMIC;
