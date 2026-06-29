-- sys_user 增加 IM 同步状态；上线库执行一次

ALTER TABLE `sys_user`
  ADD COLUMN `user_im_syn_status_code` varchar(10) DEFAULT '0' COMMENT 'IM同步状态 0未同步 1已同步' AFTER `user_oline_status_code`;

-- 已有 im_user 记录的用户标记为已同步（跨库一次性迁移，后续由业务维护）
UPDATE `sys_user` u
INNER JOIN `im_user` i ON i.im_user_user_code = u.user_code AND i.deleted = 0
SET u.user_im_syn_status_code = '1'
WHERE u.deleted = 0;
