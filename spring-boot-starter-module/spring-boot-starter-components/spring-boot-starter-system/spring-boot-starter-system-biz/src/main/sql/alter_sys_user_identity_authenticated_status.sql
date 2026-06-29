-- sys_user 增加实名认证状态；上线库执行一次

ALTER TABLE `sys_user`
  ADD COLUMN `user_identity_authenticated_status_code` varchar(10) DEFAULT '0' COMMENT '实名认证状态：1 已认证 0 未认证' AFTER `user_real_name`;

-- 从客户表回填已实名用户（一次性迁移）
UPDATE `sys_user` u
INNER JOIN `customer` c ON c.cus_user_code = u.user_code AND c.deleted = '0'
SET u.user_identity_authenticated_status_code = c.cus_identity_authenticated_status_code,
    u.user_real_name = COALESCE(NULLIF(TRIM(u.user_real_name), ''), NULLIF(TRIM(c.cus_name), ''))
WHERE u.deleted = '0'
  AND c.cus_identity_authenticated_status_code = '1';
