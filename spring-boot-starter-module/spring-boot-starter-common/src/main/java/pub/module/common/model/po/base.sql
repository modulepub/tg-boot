-- 为指定表新增id、seq_no等所有字段，替换your_table_name为实际表名
ALTER TABLE xxxx
    ADD COLUMN `id` VARCHAR(60) NOT NULL COMMENT '主键ID' FIRST,
    ADD COLUMN `seq_no` int DEFAULT NULL COMMENT '序列编号' AFTER `id`,
    ADD COLUMN `org_code` varchar(64) DEFAULT NULL COMMENT '机构编码' AFTER `seq_no`,
    ADD COLUMN `update_by` varchar(64) DEFAULT NULL COMMENT '更新人' AFTER `org_code`,
    ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ONUPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `update_by`,
    ADD COLUMN `create_by` varchar (64) DEFAULT NULL COMMENT '创建人' AFTER `update_time`,
    ADD COLUMN `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_by`,
    ADD COLUMN `version` VARCHAR (10) COMMENT '乐观锁版本号' AFTER `create_time`,
    ADD COLUMN `deleted` VARCHAR (10) COMMENT '逻辑删除 0-未删 1-已删' AFTER `version`;