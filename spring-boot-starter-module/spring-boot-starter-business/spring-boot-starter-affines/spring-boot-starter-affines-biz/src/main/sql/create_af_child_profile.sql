-- 家长相亲：孩子资料卡

CREATE TABLE IF NOT EXISTS `af_child_profile` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',

  `af_child_profile_code` varchar(64) NULL DEFAULT NULL COMMENT '孩子资料卡编码',
  `af_parent_user_code` varchar(64) NULL DEFAULT NULL COMMENT '家长用户编码',

  `af_child_name` varchar(64) NULL DEFAULT NULL COMMENT '孩子姓名',
  `af_child_nick_name` varchar(64) NULL DEFAULT NULL COMMENT '孩子昵称',
  `af_child_sex_code` varchar(8) NULL DEFAULT NULL COMMENT '孩子性别',
  `af_child_age` int NULL DEFAULT NULL COMMENT '孩子年龄',
  `af_child_birthday` date NULL DEFAULT NULL COMMENT '孩子生日',
  `af_child_height` int NULL DEFAULT NULL COMMENT '身高(cm)',
  `af_child_weight` int NULL DEFAULT NULL COMMENT '体重(kg)',
  `af_child_education_code` varchar(32) NULL DEFAULT NULL COMMENT '学历编码',
  `af_child_education_name` varchar(64) NULL DEFAULT NULL COMMENT '学历名称',
  `af_child_marital_status_code` varchar(4) NULL DEFAULT '0' COMMENT '婚姻状况 0否 1是',
  `af_child_remarriage_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否二婚 0否 1是',
  `af_child_disabled_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否残疾 0否 1是',
  `af_child_city_residence_code` varchar(64) NULL DEFAULT NULL COMMENT '生活城市编码',
  `af_child_city_residence_name` varchar(128) NULL DEFAULT NULL COMMENT '生活城市名称',
  `af_child_occupational_description` varchar(512) NULL DEFAULT NULL COMMENT '职业描述',
  `af_child_avatar` varchar(512) NULL DEFAULT NULL COMMENT '头像',
  `af_child_life_photo` varchar(512) NULL DEFAULT NULL COMMENT '生活照',

  `af_child_have_car_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否有车 0否 1是',
  `af_child_have_house_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否有房 0否 1是',
  `af_child_annual_income_amount` decimal(18,2) NULL DEFAULT NULL COMMENT '年收入',

  `af_child_desc` text NULL COMMENT '简要描述',
  `af_child_hidden_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否隐藏 0否 1是',
  `af_child_publish_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否发布 0否 1是',

  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_af_child_profile_code`(`af_child_profile_code` ASC) USING BTREE,
  INDEX `idx_af_parent_user_code`(`af_parent_user_code` ASC) USING BTREE,
  INDEX `idx_af_child_publish_status_code`(`af_child_publish_status_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家长相亲-孩子资料卡' ROW_FORMAT = DYNAMIC;
