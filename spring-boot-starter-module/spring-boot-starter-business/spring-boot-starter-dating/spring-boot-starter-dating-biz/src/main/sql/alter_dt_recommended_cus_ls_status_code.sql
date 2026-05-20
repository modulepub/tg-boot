-- 客户表、推荐表冗余「爱与诚」点亮状态（1 点亮，其它未点亮）
-- 执行前请确认列不存在，避免重复执行报错

ALTER TABLE `customer`
  ADD COLUMN `cus_ls_status_code` varchar(10) NULL DEFAULT NULL COMMENT '是否点亮爱与诚：1 点亮' AFTER `cus_name`;

ALTER TABLE `dt_recommended`
  ADD COLUMN `cus_ls_status_code` varchar(10) NULL DEFAULT NULL COMMENT '是否点亮爱与诚（冗余）' AFTER `cus_name`;

UPDATE `dt_recommended` r
INNER JOIN `customer` c ON r.`cus_code` = c.`cus_code`
SET r.`cus_ls_status_code` = c.`cus_ls_status_code`
WHERE c.`cus_ls_status_code` IS NOT NULL AND TRIM(c.`cus_ls_status_code`) <> '';
