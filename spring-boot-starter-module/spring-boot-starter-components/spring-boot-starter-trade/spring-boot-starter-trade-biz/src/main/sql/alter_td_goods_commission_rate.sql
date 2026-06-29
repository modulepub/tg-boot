-- 商品表增加分佣比例，默认 90%
ALTER TABLE `td_goods`
  ADD COLUMN `td_gd_commission_rate` decimal(8, 4) NOT NULL DEFAULT 0.9000 COMMENT '分佣比例' AFTER `td_gd_inventory_num`;

UPDATE `td_goods` SET `td_gd_commission_rate` = 0.9000 WHERE `td_gd_commission_rate` IS NULL;
