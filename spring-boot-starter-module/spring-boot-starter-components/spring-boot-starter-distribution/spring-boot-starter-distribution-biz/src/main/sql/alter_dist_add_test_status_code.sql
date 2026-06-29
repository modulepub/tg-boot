-- 为分销账单相关表新增「是否测试数据」标记列（0否 1是），用于后台业绩测试数据的生成与清除。
ALTER TABLE `dist_user_bill_summary`
  ADD COLUMN `dist_test_status_code` varchar(2) NULL DEFAULT '0' COMMENT '是否测试数据 0否 1是';

ALTER TABLE `dist_user_bill_settle_record`
  ADD COLUMN `dist_test_status_code` varchar(2) NULL DEFAULT '0' COMMENT '是否测试数据 0否 1是';

ALTER TABLE `dist_user_bill_event`
  ADD COLUMN `dist_test_status_code` varchar(2) NULL DEFAULT '0' COMMENT '是否测试数据 0否 1是';
