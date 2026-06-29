-- 公众号配置：关联 AI 智能体与自动回复开关（*StatusCode 命名）
ALTER TABLE `wx_mp_config`
  ADD COLUMN `wx_mp_config_ai_agent_code` varchar(64) DEFAULT NULL COMMENT '接管回复的 AI 智能体编码' AFTER `wx_mp_config_menu_published_time`,
  ADD COLUMN `wx_mp_config_ai_auto_reply_status_code` varchar(8) DEFAULT '0' COMMENT 'AI 自动回复：0-否 1-是' AFTER `wx_mp_config_ai_agent_code`;
