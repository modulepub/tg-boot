-- 商品权益子表
CREATE TABLE IF NOT EXISTS `td_goods_benefit` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `td_gd_bnf_code` varchar(64) NOT NULL COMMENT '商品权益编码',
  `td_gd_code` varchar(64) NOT NULL COMMENT '商品编码',
  `td_gd_bnf_key` varchar(64) NOT NULL COMMENT '权益key',
  `td_gd_bnf_value` bigint NULL DEFAULT NULL COMMENT '权益值',
  `td_gd_bnf_desc` varchar(255) NULL DEFAULT NULL COMMENT '权益描述',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_td_gd_bnf_code`(`td_gd_bnf_code`),
  INDEX `idx_td_gd_bnf_td_gd_code`(`td_gd_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品权益';
