-- WxConfig -> WxMiniConfig 迁移脚本（已有 wx_config 表的环境执行一次）
-- 1. 重命名表
RENAME TABLE `wx_config` TO `wx_mini_config`;

-- 2. 重命名业务字段
ALTER TABLE `wx_mini_config`
  CHANGE COLUMN `wx_config_code` `wx_mini_config_code` varchar(64) NOT NULL COMMENT '配置编码（业务主键）',
  CHANGE COLUMN `wx_config_name` `wx_mini_config_name` varchar(128) DEFAULT NULL COMMENT '配置名称',
  CHANGE COLUMN `wx_config_app_id` `wx_mini_config_app_id` varchar(64) NOT NULL COMMENT '小程序 AppId',
  CHANGE COLUMN `wx_config_app_secret` `wx_mini_config_app_secret` varchar(128) NOT NULL COMMENT '小程序 AppSecret',
  CHANGE COLUMN `wx_config_msg_data_format` `wx_mini_config_msg_data_format` varchar(16) DEFAULT 'JSON' COMMENT '消息格式 JSON/XML',
  CHANGE COLUMN `wx_config_enabled_code` `wx_mini_config_enabled_code` varchar(8) DEFAULT '1' COMMENT '启用：1-是 0-否',
  CHANGE COLUMN `wx_config_remark` `wx_mini_config_remark` varchar(512) DEFAULT NULL COMMENT '备注';

-- 3. 重命名索引（若存在 uk_wx_config_code / idx_wx_config_app_id）
ALTER TABLE `wx_mini_config`
  DROP INDEX `idx_wx_config_app_id`,
  ADD INDEX `idx_wx_mini_config_app_id` (`wx_mini_config_app_id`);

-- 若已执行 alter_wx_config_base_entity.sql，还需重命名唯一键
-- ALTER TABLE `wx_mini_config` DROP INDEX `uk_wx_config_code`, ADD UNIQUE KEY `uk_wx_mini_config_code` (`wx_mini_config_code`);

-- 4. 更新管理端菜单
UPDATE `sys_permission`
SET `per_code` = 'wxWxMiniConfigIndex', `per_url` = 'wx/wxMiniConfig/index'
WHERE `per_code` = 'wxWxConfigIndex' AND `deleted` = 0;

UPDATE `sys_role_permission`
SET `per_code` = 'wxWxMiniConfigIndex'
WHERE `per_code` = 'wxWxConfigIndex' AND `deleted` = '0';
