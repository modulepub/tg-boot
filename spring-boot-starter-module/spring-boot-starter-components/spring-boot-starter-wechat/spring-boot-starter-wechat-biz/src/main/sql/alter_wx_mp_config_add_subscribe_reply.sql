-- 公众号配置：关注回复（图文消息）
ALTER TABLE `wx_mp_config`
  ADD COLUMN `wx_mp_config_subscribe_reply_status_code` varchar(8) DEFAULT '0' COMMENT '关注回复：0-否 1-是' AFTER `wx_mp_config_ai_auto_reply_status_code`,
  ADD COLUMN `wx_mp_config_subscribe_reply_json` mediumtext COMMENT '关注回复图文 JSON（title/description/picUrl/url）' AFTER `wx_mp_config_subscribe_reply_status_code`;
