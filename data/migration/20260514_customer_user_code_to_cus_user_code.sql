-- customer：客户绑定的系统用户编码列由 user_code 重命名为 cus_user_code（与 Java 字段 cusUserCode / JSON cusUserCode 对齐）
-- 执行前请备份数据库。
-- 说明：主键原为 (cus_code, user_code)，需先删除主键、改名列、再重建主键。

ALTER TABLE `customer` DROP PRIMARY KEY;

ALTER TABLE `customer`
  CHANGE COLUMN `user_code` `cus_user_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户用户号';

ALTER TABLE `customer` ADD PRIMARY KEY (`cus_code`, `cus_user_code`) USING BTREE;
