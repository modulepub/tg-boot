-- 家长相亲：孩子意向对象条件

CREATE TABLE IF NOT EXISTS `af_child_intention` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',

  `af_child_intention_code` varchar(64) NULL DEFAULT NULL COMMENT '意向编码',
  `af_child_profile_code` varchar(64) NULL DEFAULT NULL COMMENT '孩子资料卡编码',
  `af_parent_user_code` varchar(64) NULL DEFAULT NULL COMMENT '家长用户编码',

  `af_intention_min_age` int NULL DEFAULT NULL COMMENT '意向最小年龄',
  `af_intention_max_age` int NULL DEFAULT NULL COMMENT '意向最大年龄',
  `af_intention_sex_code` varchar(8) NULL DEFAULT NULL COMMENT '期望嘉宾性别',
  `af_intention_have_house_code` varchar(4) NULL DEFAULT '0' COMMENT '是否有房 0否 1是',
  `af_intention_have_car_code` varchar(4) NULL DEFAULT '0' COMMENT '是否有车 0否 1是',
  `af_intention_city_code` varchar(64) NULL DEFAULT NULL COMMENT '意向城市编码',
  `af_intention_ldr_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否接受异地 0否 1是',
  `af_intention_disabled_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否接受残疾 0否 1是',
  `af_intention_higher_education_status_code` varchar(4) NULL DEFAULT '0' COMMENT '高学历优先 0否 1是',

  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_af_child_intention_code`(`af_child_intention_code` ASC) USING BTREE,
  INDEX `idx_af_child_profile_code`(`af_child_profile_code` ASC) USING BTREE,
  INDEX `idx_af_intention_parent_user_code`(`af_parent_user_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家长相亲-孩子意向对象条件' ROW_FORMAT = DYNAMIC;
