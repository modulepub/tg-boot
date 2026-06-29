-- 用户账单汇总（按用户 + 业务线）
-- 执行前请确认表不存在，避免重复执行报错

CREATE TABLE IF NOT EXISTS `dist_user_bill_summary` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `dist_user_bill_summary_code` varchar(60) NOT NULL COMMENT '汇总流水编码',
  `dist_user_code` varchar(64) NOT NULL COMMENT '用户编码（账单所属用户）',
  `dist_user_nick_name` varchar(100) NULL DEFAULT NULL COMMENT '用户昵称（冗余）',
  `dist_user_real_name` varchar(100) NULL DEFAULT NULL COMMENT '用户真实姓名（冗余）',
  `dist_inviter_user_code` varchar(64) NULL DEFAULT NULL COMMENT '邀请人用户编码（冗余）',
  `dist_inviter_user_nick_name` varchar(100) NULL DEFAULT NULL COMMENT '邀请人昵称（冗余）',
  `dist_inviter_user_real_name` varchar(100) NULL DEFAULT NULL COMMENT '邀请人真实姓名（冗余）',
  `dist_biz_line_code` varchar(32) NOT NULL COMMENT '业务线编码',
  `dist_paid_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '付费总金额（本人消费）',
  `dist_in_service_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '服务期内总金额（待结算佣金）',
  `dist_credited_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '已入账总金额（已结算佣金）',
  `dist_sub_paid_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '子级用户付费总金额',
  `dist_sub_in_service_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '子级服务期内总金额',
  `dist_sub_credited_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '子级已入账总金额',
  `dist_commission_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '间推分佣金额（上级5%奖励累计）',
  `dist_sub_commission_total_amount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '直推分佣金额（下级消费分佣累计）',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_dist_user_bill_summary_user`(`dist_biz_line_code`, `dist_user_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '分销用户账单汇总';
