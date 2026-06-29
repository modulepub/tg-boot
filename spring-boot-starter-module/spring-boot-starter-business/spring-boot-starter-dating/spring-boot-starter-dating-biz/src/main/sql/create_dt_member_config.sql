-- 婚恋系统-会员配置表 dt_member_config（单行配置）
-- 字段 cfg_register_gift_freevip_status_code：是否「注册即赠钻石会员·体验7天」（商品编码 freevip）。0-关闭，1-开启。

CREATE TABLE IF NOT EXISTS `dt_member_config` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除',
  `cfg_register_gift_freevip_status_code` varchar(8) NULL DEFAULT '0' COMMENT '注册即赠钻石会员·体验7天开关：0-关闭，1-开启',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='婚恋系统-会员配置';

-- 初始化一行默认配置（关闭），仅当表为空时插入
INSERT INTO `dt_member_config` (
  `id`, `create_by`, `create_time`, `update_time`, `version`, `deleted`,
  `cfg_register_gift_freevip_status_code`
)
SELECT
  REPLACE(UUID(), '-', ''), 'admin', NOW(), NOW(), '1.0', '0', '0'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `dt_member_config` WHERE `deleted` = '0'
);
