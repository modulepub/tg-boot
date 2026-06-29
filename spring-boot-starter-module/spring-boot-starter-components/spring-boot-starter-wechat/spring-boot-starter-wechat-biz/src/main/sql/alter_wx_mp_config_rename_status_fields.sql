-- 修正 wx_mp_config 布尔状态字段命名（符合 *StatusCode 约定，已执行旧脚本时运行一次）

ALTER TABLE `wx_mp_config`
  CHANGE COLUMN `wx_mp_config_enabled_code` `wx_mp_config_enabled_status_code` varchar(8) DEFAULT '1' COMMENT '启用状态：0-否 1-是';

-- 若曾使用 wx_mp_config_ai_auto_reply_code 错误列名，则重命名
ALTER TABLE `wx_mp_config`
  CHANGE COLUMN `wx_mp_config_ai_auto_reply_code` `wx_mp_config_ai_auto_reply_status_code` varchar(8) DEFAULT '0' COMMENT 'AI 自动回复：0-否 1-是';
