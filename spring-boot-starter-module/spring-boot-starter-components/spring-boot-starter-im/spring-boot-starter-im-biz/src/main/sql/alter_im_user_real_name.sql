ALTER TABLE `im_user`
  ADD COLUMN `im_user_real_name` varchar(128) DEFAULT NULL COMMENT '真实姓名' AFTER `im_user_avatar`;
