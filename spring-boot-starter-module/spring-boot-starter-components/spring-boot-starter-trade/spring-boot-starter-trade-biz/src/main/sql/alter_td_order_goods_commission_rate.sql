-- 订单商品冗余商品分佣比例快照
ALTER TABLE `td_order_goods`
  ADD COLUMN `td_gd_commission_rate` decimal(8, 4) NOT NULL DEFAULT 0.9000 COMMENT '分佣比例（冗余）' AFTER `td_od_sys_user_code`;

UPDATE `td_order_goods` SET `td_gd_commission_rate` = 0.9000 WHERE `td_gd_commission_rate` IS NULL;
