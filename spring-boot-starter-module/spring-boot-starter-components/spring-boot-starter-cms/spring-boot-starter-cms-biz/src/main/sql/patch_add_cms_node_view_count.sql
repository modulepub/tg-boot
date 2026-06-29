-- CMS 节点增加阅读数
ALTER TABLE `cms_node`
  ADD COLUMN `node_view_count` bigint NOT NULL DEFAULT 0 COMMENT '阅读数' AFTER `node_content`;
