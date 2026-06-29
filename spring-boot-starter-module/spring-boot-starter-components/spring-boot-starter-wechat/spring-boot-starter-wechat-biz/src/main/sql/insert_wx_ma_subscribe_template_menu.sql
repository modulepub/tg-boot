-- 微信模块管理端菜单：订阅消息模板配置（仅编辑，不可新增）
-- 结构：manage -> wxSystem -> wxWxMaSubscribeTemplateIndex
-- 注意：013003/013103/013203 已被 wxWxMiniConfigIndex 占用，勿复用

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd3e4f5a6b7c811f0b6af00355d013006', 5, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'wxSystem', 'wxWxMaSubscribeTemplateIndex', '订阅消息模板', 'wx/wxMaSubscribeTemplate/index', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'wxWxMaSubscribeTemplateIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd3e4f5a6b7c811f0b6af00355d013106', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'wxWxMaSubscribeTemplateIndex', 'admin', 'd3e4f5a6b7c811f0b6af00355d013206', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'wxWxMaSubscribeTemplateIndex' AND `deleted` = '0');
