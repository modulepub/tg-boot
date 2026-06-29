-- 微信小程序「消息推送」接入所需配置：Token 与 EncodingAESKey。
-- 用于接收内容安全异步检测结果（Event=wxa_media_check）回调的 URL 验签与解密。
ALTER TABLE `wx_mini_config`
    ADD COLUMN `wx_mini_config_token` varchar(64) DEFAULT NULL COMMENT '消息推送 Token（URL 验签用）' AFTER `wx_mini_config_msg_data_format`,
    ADD COLUMN `wx_mini_config_aes_key` varchar(64) DEFAULT NULL COMMENT '消息推送 EncodingAESKey（安全模式 AES 解密用，明文模式可空）' AFTER `wx_mini_config_token`;
