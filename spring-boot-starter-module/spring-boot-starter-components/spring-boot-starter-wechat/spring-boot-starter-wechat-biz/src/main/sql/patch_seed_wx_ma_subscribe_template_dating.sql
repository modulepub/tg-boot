-- 婚恋域订阅消息模板初始数据（模板编码与 DatingWxSubscribeTemplateCode 保持一致）
-- 模板 ID 为占位符，部署时请替换为微信公众平台实际模板 ID
INSERT INTO `wx_ma_subscribe_template` (
  `id`, `wx_ma_subscribe_template_code`, `wx_ma_subscribe_template_id`, `wx_ma_subscribe_template_content`,
  `create_by`, `create_time`, `update_by`, `update_time`, `deleted`
)
SELECT 'wx_ma_sub_tpl_friendRequestReceived', 'friendRequestReceived', 'PLACEHOLDER_TEMPLATE_FRIEND_REQUEST',
  '收到好友申请通知\ndate1 申请时间 | name3 申请人 | thing2 申请内容',
  'admin', NOW(), 'admin', NOW(), 0
FROM DUAL WHERE NOT EXISTS (
  SELECT 1 FROM `wx_ma_subscribe_template` WHERE `wx_ma_subscribe_template_code` = 'friendRequestReceived' AND `deleted` = 0
);

INSERT INTO `wx_ma_subscribe_template` (
  `id`, `wx_ma_subscribe_template_code`, `wx_ma_subscribe_template_id`, `wx_ma_subscribe_template_content`,
  `create_by`, `create_time`, `update_by`, `update_time`, `deleted`
)
SELECT 'wx_ma_sub_tpl_friendAddSuccess', 'friendAddSuccess', 'PLACEHOLDER_TEMPLATE_FRIEND_ADD_SUCCESS',
  '添加好友成功通知\nthing2 提示文案 | date1 同意时间',
  'admin', NOW(), 'admin', NOW(), 0
FROM DUAL WHERE NOT EXISTS (
  SELECT 1 FROM `wx_ma_subscribe_template` WHERE `wx_ma_subscribe_template_code` = 'friendAddSuccess' AND `deleted` = 0
);

INSERT INTO `wx_ma_subscribe_template` (
  `id`, `wx_ma_subscribe_template_code`, `wx_ma_subscribe_template_id`, `wx_ma_subscribe_template_content`,
  `create_by`, `create_time`, `update_by`, `update_time`, `deleted`
)
SELECT 'wx_ma_sub_tpl_matchRequest', 'matchRequest', 'PLACEHOLDER_TEMPLATE_MATCH_REQUEST',
  '牵线请求通知\nname1 申请人 | thing3 请求说明 | thing4 申请人 | time5 请求时间',
  'admin', NOW(), 'admin', NOW(), 0
FROM DUAL WHERE NOT EXISTS (
  SELECT 1 FROM `wx_ma_subscribe_template` WHERE `wx_ma_subscribe_template_code` = 'matchRequest' AND `deleted` = 0
);

INSERT INTO `wx_ma_subscribe_template` (
  `id`, `wx_ma_subscribe_template_code`, `wx_ma_subscribe_template_id`, `wx_ma_subscribe_template_content`,
  `create_by`, `create_time`, `update_by`, `update_time`, `deleted`
)
SELECT 'wx_ma_sub_tpl_freeRecommend', 'freeRecommend', 'PLACEHOLDER_TEMPLATE_FREE_RECOMMEND',
  '相亲对象推荐通知\nthing1 推荐说明 | thing5 温馨提示',
  'admin', NOW(), 'admin', NOW(), 0
FROM DUAL WHERE NOT EXISTS (
  SELECT 1 FROM `wx_ma_subscribe_template` WHERE `wx_ma_subscribe_template_code` = 'freeRecommend' AND `deleted` = 0
);
