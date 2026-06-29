-- 赠送会员（freevip）服务期统一为「体验 7 天」，与「注册即赠钻石会员·体验7天」配置保持一致。
-- 幂等：仅当当前服务期为空或不等于 7 时更新。

UPDATE `td_goods`
SET `td_gd_day_period` = 7,
    `update_time` = NOW(),
    `update_by` = 'admin'
WHERE `td_gd_code` = 'freevip'
  AND `deleted` = '0'
  AND (`td_gd_day_period` IS NULL OR `td_gd_day_period` <> 7);
