-- 若环境 sys_user 尚无 user_real_name 列可执行（标准库 v3.6 已包含该列）

ALTER TABLE `sys_user`
  ADD COLUMN `user_real_name` varchar(100) DEFAULT NULL COMMENT '真名' AFTER `user_avatar`;
