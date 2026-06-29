-- 内容合法校验审核记录

-- 执行前请确认表不存在，避免重复执行报错



CREATE TABLE IF NOT EXISTS `vt_cm_record` (

  `id` varchar(64) NOT NULL COMMENT '主键ID',

  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',

  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',

  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',

  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',

  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',

  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',

  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',

  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',

  `cm_record_code` varchar(64) NULL DEFAULT NULL COMMENT '内容审核记录编码',

  `cm_record_source_module_code` varchar(64) NULL DEFAULT NULL COMMENT '发起方业务模块编码',

  `cm_record_biz_code` varchar(64) NULL DEFAULT NULL COMMENT '发起方业务主体编码',

  `cm_record_user_code` varchar(64) NULL DEFAULT NULL COMMENT '发起方用户编码',

  `cm_record_user_name` varchar(64) NULL DEFAULT NULL COMMENT '发起方用户姓名（冗余，便于展示）',

  `cm_record_content_type_code` varchar(16) NULL DEFAULT NULL COMMENT '内容类型 TEXT/IMAGE/VIDEO',

  `cm_record_content` text NULL COMMENT '审核内容（文本或媒体URL）',

  `cm_record_plugin_code` varchar(64) NULL DEFAULT NULL COMMENT '审核插件编码；空表示纯人工审核',

  `cm_record_passed_status_code` varchar(4) NULL DEFAULT NULL COMMENT '是否通过：1是 0否',

  `cm_record_not_passed_reason` varchar(512) NULL DEFAULT NULL COMMENT '未通过原因',

  `cm_record_async_status_code` varchar(4) NULL DEFAULT '0' COMMENT '是否异步：1是 0否',

  `cm_record_process_code` varchar(4) NULL DEFAULT '0' COMMENT '流程：0待审核 1审核中 2审核结束',

  `cm_record_remark` text NULL COMMENT '备注（第三方插件原始结果或人工驳回说明）',

  `cm_record_vendor_trace_id` varchar(128) NULL DEFAULT NULL COMMENT '上游 trace_id',

  `cm_record_audit_by` varchar(64) NULL DEFAULT NULL COMMENT '人工审核人用户编码',

  `cm_record_audit_at` datetime NULL DEFAULT NULL COMMENT '人工审核时间',

  PRIMARY KEY (`id`) USING BTREE,

  INDEX `idx_vt_cm_record_biz_code`(`cm_record_biz_code` ASC) USING BTREE,

  INDEX `idx_vt_cm_record_user_code`(`cm_record_user_code` ASC) USING BTREE,

  INDEX `idx_vt_cm_record_trace_id`(`cm_record_vendor_trace_id` ASC) USING BTREE,

  INDEX `idx_vt_cm_record_plugin_code`(`cm_record_plugin_code` ASC) USING BTREE,

  INDEX `idx_vt_cm_record_process_code`(`cm_record_process_code` ASC) USING BTREE,

  INDEX `idx_vt_cm_record_create_time`(`create_time` ASC) USING BTREE

) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '内容合法校验审核记录' ROW_FORMAT = DYNAMIC;

