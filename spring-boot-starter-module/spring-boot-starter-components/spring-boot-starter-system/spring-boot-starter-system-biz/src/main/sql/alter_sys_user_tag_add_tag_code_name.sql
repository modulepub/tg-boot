-- 【增量】用户标签表 sys_user_tag：新增 tag_code（标签编码，如红娘 matchmaker）与 tag_name（标签名称），
-- 并移除原 user_tag_name 列。唯一约束由 (user_code, user_tag_name) 调整为 (user_code, tag_code)。
-- 执行前请确认未重复执行。

ALTER TABLE `sys_user_tag`
  ADD COLUMN `tag_code` varchar(64) NULL DEFAULT NULL COMMENT '标签编码' AFTER `user_code`,
  ADD COLUMN `tag_name` varchar(64) NULL DEFAULT NULL COMMENT '标签名称' AFTER `tag_code`;

-- 历史数据迁移：将原标签名称同时回填到 tag_name 与 tag_code（编码缺省用名称兜底）
UPDATE `sys_user_tag`
  SET `tag_name` = `user_tag_name`,
      `tag_code` = `user_tag_name`
  WHERE `tag_name` IS NULL;

ALTER TABLE `sys_user_tag`
  DROP INDEX `uk_sys_user_tag_user_name`,
  DROP COLUMN `user_tag_name`,
  ADD UNIQUE INDEX `uk_sys_user_tag_user_tag`(`user_code` ASC, `tag_code` ASC) USING BTREE;
