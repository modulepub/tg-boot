-- 家长相亲：孩子资料卡浏览记录

CREATE TABLE IF NOT EXISTS `af_child_profile_view` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',

  `af_child_profile_view_code` varchar(64) NULL DEFAULT NULL COMMENT '浏览记录编码',
  `af_child_profile_code` varchar(64) NULL DEFAULT NULL COMMENT '被浏览资料卡编码',
  `af_viewer_user_code` varchar(64) NULL DEFAULT NULL COMMENT '浏览者用户编码',

  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_af_child_profile_view_code`(`af_child_profile_view_code` ASC) USING BTREE,
  INDEX `idx_af_view_child_profile_code`(`af_child_profile_code` ASC) USING BTREE,
  INDEX `idx_af_viewer_user_code`(`af_viewer_user_code` ASC) USING BTREE,
  INDEX `idx_af_view_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家长相亲-孩子资料卡浏览记录' ROW_FORMAT = DYNAMIC;
