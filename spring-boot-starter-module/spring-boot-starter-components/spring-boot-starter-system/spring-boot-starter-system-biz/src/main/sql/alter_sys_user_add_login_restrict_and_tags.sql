-- sys_user：是否限制登录、用户标签
ALTER TABLE `sys_user`
  ADD COLUMN `user_login_restrict_status_code` varchar(10) DEFAULT '0' COMMENT '是否限制登录：0-不限制 1-限制' AFTER `user_test_status_code`,
  ADD COLUMN `user_tags` varchar(500) DEFAULT NULL COMMENT '用户标签（逗号分隔）' AFTER `user_login_restrict_status_code`;
