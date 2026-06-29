-- 修正婚恋域订阅消息模板 ID（与微信公众平台一致；占位符需替换为实际模板 ID）
UPDATE `wx_ma_subscribe_template`
SET `wx_ma_subscribe_template_id` = 'PLACEHOLDER_TEMPLATE_FRIEND_ADD_SUCCESS',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `wx_ma_subscribe_template_code` = 'friendAddSuccess'
  AND `deleted` = 0
  AND `wx_ma_subscribe_template_id` <> 'PLACEHOLDER_TEMPLATE_FRIEND_ADD_SUCCESS';

UPDATE `wx_ma_subscribe_template`
SET `wx_ma_subscribe_template_id` = 'PLACEHOLDER_TEMPLATE_MATCH_REQUEST',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `wx_ma_subscribe_template_code` = 'matchRequest'
  AND `deleted` = 0
  AND `wx_ma_subscribe_template_id` <> 'PLACEHOLDER_TEMPLATE_MATCH_REQUEST';

UPDATE `wx_ma_subscribe_template`
SET `wx_ma_subscribe_template_id` = 'PLACEHOLDER_TEMPLATE_FREE_RECOMMEND',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `wx_ma_subscribe_template_code` = 'freeRecommend'
  AND `deleted` = 0
  AND `wx_ma_subscribe_template_id` <> 'PLACEHOLDER_TEMPLATE_FREE_RECOMMEND';
