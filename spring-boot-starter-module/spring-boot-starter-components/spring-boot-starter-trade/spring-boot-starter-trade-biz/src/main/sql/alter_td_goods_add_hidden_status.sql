-- 商品表增加“是否隐藏”字段：1 隐藏 / 0 显示。移动端不查询隐藏商品（如赠送会员 freevip）。
ALTER TABLE `td_goods`
  ADD COLUMN `td_gd_hidden_status_code` char(1) NOT NULL DEFAULT '0' COMMENT '是否隐藏：1是 0否（移动端不展示隐藏商品）' AFTER `td_gd_test_status_code`;

UPDATE `td_goods` SET `td_gd_hidden_status_code` = '0' WHERE `td_gd_hidden_status_code` IS NULL;

-- 赠送会员商品归入 vip 类目并隐藏（兼容历史上类目被建成 freevip 的数据）
UPDATE `td_goods`
SET `td_gd_cgy_code` = 'vip',
    `td_gd_cgy_name` = '会员套餐',
    `td_gd_hidden_status_code` = '1'
WHERE `td_gd_code` = 'freevip';
