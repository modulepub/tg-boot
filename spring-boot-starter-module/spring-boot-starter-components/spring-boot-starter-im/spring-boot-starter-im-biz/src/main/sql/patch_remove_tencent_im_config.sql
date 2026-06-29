-- 移除腾讯云 IM 废弃配置（上线库执行一次）
UPDATE `biz_config`
SET `deleted` = '1', `update_time` = NOW()
WHERE `config_code` = 'tencent-im' AND `deleted` = '0';
