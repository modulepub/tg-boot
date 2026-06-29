-- wx_pay_config 对齐 BaseEntity：新增技术主键 id，业务编码 wx_pay_config_code 改唯一键
ALTER TABLE `wx_pay_config`
  ADD COLUMN `id` varchar(64) NULL COMMENT '主键ID' FIRST;

UPDATE `wx_pay_config`
SET `id` = `wx_pay_config_code`
WHERE `id` IS NULL OR `id` = '';

ALTER TABLE `wx_pay_config`
  DROP PRIMARY KEY,
  MODIFY COLUMN `id` varchar(64) NOT NULL COMMENT '主键ID',
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_wx_pay_config_code` (`wx_pay_config_code`);
