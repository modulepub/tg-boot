-- 已用旧 DDL 建好 td_wx_pay_config（varchar 路径列）的环境：升级为 PEM 文本列（数据需手工改为 PEM）
ALTER TABLE `td_wx_pay_config`
  CHANGE COLUMN `wx_pay_config_private_key_path` `wx_pay_config_private_key` mediumtext NULL COMMENT '商户 API 私钥 PEM 全文',
  CHANGE COLUMN `wx_pay_config_private_cert_path` `wx_pay_config_private_cert` mediumtext NULL COMMENT '商户 API 证书 PEM 全文';
