-- 【增量】内容审核记录冗余「用户姓名」：便于管理端审核列表直接展示提交人，免去跨模块联表。
-- 历史数据不必回填（旧记录该列为空即可）。
ALTER TABLE `vt_cm_record`
    ADD COLUMN `cm_record_user_name` varchar(64) NULL DEFAULT NULL COMMENT '发起方用户姓名（冗余，便于展示）' AFTER `cm_record_user_code`;
