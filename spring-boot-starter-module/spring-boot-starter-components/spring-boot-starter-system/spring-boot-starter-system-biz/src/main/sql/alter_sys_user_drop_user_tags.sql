-- sys_user：标签改为子表 sys_user_tag 管理，移除原冗余的逗号分隔标签列
-- 执行前如需保留历史标签，请先将 user_tags 数据迁移到 sys_user_tag
ALTER TABLE `sys_user`
  DROP COLUMN `user_tags`;
