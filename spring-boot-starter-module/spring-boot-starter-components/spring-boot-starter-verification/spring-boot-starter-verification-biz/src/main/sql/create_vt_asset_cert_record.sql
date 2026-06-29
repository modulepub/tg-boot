-- 资产认证记录表（爱与诚辅助认证）
-- 执行前请确认表不存在，避免重复执行报错

CREATE TABLE IF NOT EXISTS `vt_asset_cert_record` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `seq_no` int NULL DEFAULT NULL COMMENT '序列编号',
  `org_code` varchar(64) NULL DEFAULT NULL COMMENT '机构编码',
  `update_by` varchar(64) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `version` varchar(10) NULL DEFAULT NULL COMMENT '乐观锁版本号',
  `deleted` varchar(10) NULL DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `asset_cert_code` varchar(64) NULL DEFAULT NULL COMMENT '资产认证记录编码',
  `cus_code` varchar(64) NULL DEFAULT NULL COMMENT '客户编码',
  `cus_nick_name` varchar(64) NULL DEFAULT NULL COMMENT '客户昵称（冗余）',
  `submit_mk_code` varchar(64) NULL DEFAULT NULL COMMENT '提交红娘编码',
  `submit_mk_name` varchar(64) NULL DEFAULT NULL COMMENT '提交红娘姓名（冗余）',
  `vehicle_license_photo` varchar(512) NULL DEFAULT NULL COMMENT '行驶证照片',
  `real_estate_certificate_photo` varchar(512) NULL DEFAULT NULL COMMENT '房产证照片',
  `marital_status_proof_photo` varchar(512) NULL DEFAULT NULL COMMENT '婚姻状态证明照片',
  `asset_cert_process_code` varchar(10) NULL DEFAULT '1' COMMENT '流程：0待提交 1审核中 2审核通过 3审核拒绝',
  `reject_reason` varchar(500) NULL DEFAULT NULL COMMENT '驳回原因',
  `audit_by` varchar(64) NULL DEFAULT NULL COMMENT '审核人用户编码',
  `audit_at` datetime NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_vt_asset_cert_cus_code`(`cus_code` ASC) USING BTREE,
  INDEX `idx_vt_asset_cert_process_code`(`asset_cert_process_code` ASC) USING BTREE,
  INDEX `idx_vt_asset_cert_mk_code`(`submit_mk_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资产认证记录' ROW_FORMAT = DYNAMIC;
