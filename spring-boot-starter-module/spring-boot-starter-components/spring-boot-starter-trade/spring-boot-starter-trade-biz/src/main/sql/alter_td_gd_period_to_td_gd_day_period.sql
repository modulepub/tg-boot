-- 商品/订单商品：服务期字段改为整数天数 td_gd_day_period
-- 若 td_gd_period 存过「30天」等文案，请先手工清洗为纯数字后再执行

ALTER TABLE `td_goods`
  CHANGE COLUMN `td_gd_period` `td_gd_day_period` int NULL DEFAULT NULL COMMENT '服务期（天），null 或 0 表示无服务期';

ALTER TABLE `td_order_goods`
  CHANGE COLUMN `td_gd_period` `td_gd_day_period` int NULL DEFAULT NULL COMMENT '服务期（天），null 或 0 表示无服务期';
