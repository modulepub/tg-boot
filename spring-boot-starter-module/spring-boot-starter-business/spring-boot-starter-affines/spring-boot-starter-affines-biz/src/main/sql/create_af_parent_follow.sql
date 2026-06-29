-- 家长相亲：家长关注

CREATE TABLE IF NOT EXISTS `af_parent_follow` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',

  `af_parent_follow_code` varchar(64) NULL DEFAULT NULL COMMENT '关注记录编码',
  `af_follower_user_code` varchar(64) NULL DEFAULT NULL COMMENT '关注者用户编码',
  `af_target_child_profile_code` varchar(64) NULL DEFAULT NULL COMMENT '被关注资料卡编码',
  `af_follow_status_code` varchar(4) NULL DEFAULT '1' COMMENT '是否关注 0否 1是',

  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_af_parent_follow_code`(`af_parent_follow_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_af_follower_target`(`af_follower_user_code` ASC, `af_target_child_profile_code` ASC) USING BTREE,
  INDEX `idx_af_target_child_profile_code`(`af_target_child_profile_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家长相亲-家长关注' ROW_FORMAT = DYNAMIC;
