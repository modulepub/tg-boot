-- 用户账单汇总幂等事件表
CREATE TABLE IF NOT EXISTS `dist_user_bill_event` (
  `id` varchar(60) NOT NULL COMMENT '主键',
  `create_by` varchar(60) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(60) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `org_code` varchar(60) DEFAULT NULL COMMENT '所属组织',
  `version` varchar(60) DEFAULT NULL COMMENT '版本',
  `seq_no` bigint DEFAULT NULL COMMENT '序号',
  `deleted` int DEFAULT 0 COMMENT '逻辑删除标识：0-未删除，1-已删除',
  `dist_user_bill_event_code` varchar(60) NOT NULL COMMENT '事件流水编码',
  `dist_biz_line_code` varchar(60) NOT NULL COMMENT '业务线编码',
  `dist_bill_event_source_type_code` varchar(60) NOT NULL COMMENT '事件来源类型',
  `dist_bill_event_source_id` varchar(60) NOT NULL COMMENT '事件来源 ID',
  `dist_payer_user_code` varchar(60) NOT NULL COMMENT '付款用户编码',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_dist_bill_event_source`(`dist_bill_event_source_type_code`, `dist_bill_event_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账单汇总幂等事件';
