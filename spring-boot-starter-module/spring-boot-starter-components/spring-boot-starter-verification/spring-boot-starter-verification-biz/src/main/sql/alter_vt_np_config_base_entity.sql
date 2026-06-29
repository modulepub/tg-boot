-- vt_np_config 对齐 BaseEntity：回填空 id，业务编码加唯一约束
UPDATE `vt_np_config`
SET `id` = `np_config_code`
WHERE `id` IS NULL OR `id` = '';

ALTER TABLE `vt_np_config`
  ADD UNIQUE KEY `uk_vt_np_config_code` (`np_config_code`);
