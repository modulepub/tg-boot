-- CMS 节点增加文件字段
ALTER TABLE `cms_node`
  ADD COLUMN `node_file` varchar(2000) DEFAULT NULL COMMENT '文件' AFTER `node_link`;
