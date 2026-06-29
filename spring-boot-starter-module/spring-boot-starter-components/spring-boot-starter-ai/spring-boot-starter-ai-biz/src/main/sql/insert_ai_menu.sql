-- AI 模块管理端菜单（上线库执行一次）
-- 结构：manage -> aiSystem -> 各子页面

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014001', 10, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'manage', 'aiSystem', 'AI 能力', 'ai', NULL, '0', '0', 'icon-robot'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'aiSystem' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014002', 1, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'aiSystem', 'aiAiApiConfigIndex', 'AI 接口配置', 'ai/aiApiConfig/index', NULL, '0', '0', 'icon-setting'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'aiAiApiConfigIndex' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014003', 2, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'aiSystem', 'aiAiAgentIndex', '智能体配置', 'ai/aiAgent/index', NULL, '0', '0', 'icon-user'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'aiAiAgentIndex' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014004', 3, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'aiSystem', 'aiAiUsageRecordIndex', '消耗明细', 'ai/aiUsageRecord/index', NULL, '0', '0', 'icon-insertrowbelow'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'aiAiUsageRecordIndex' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014005', 4, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'aiSystem', 'aiAiChatSessionIndex', '对话记录', 'ai/aiChatSession/index', NULL, '0', '0', 'icon-message'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'aiAiChatSessionIndex' AND `deleted` = 0);

INSERT INTO `sys_permission` (`id`, `seq_no`, `version`, `create_by`, `create_time`, `update_time`, `update_by`, `org_code`, `deleted`, `per_parent_code`, `per_code`, `per_name`, `per_url`, `per_authority`, `per_type_code`, `per_open_style_code`, `per_icon`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014006', 5, '1.0', 'admin', NOW(), NOW(), NULL, NULL, 0, 'aiSystem', 'aiAiChatDemoIndex', '调用示例', 'ai/aiChatDemo/index', NULL, '0', '0', 'icon-experiment'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `per_code` = 'aiAiChatDemoIndex' AND `deleted` = 0);

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014101', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'aiSystem', 'admin', 'd1e2f3a4b5c611f0b6af00455d014201', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'aiSystem' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014102', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'aiAiApiConfigIndex', 'admin', 'd1e2f3a4b5c611f0b6af00455d014202', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'aiAiApiConfigIndex' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014103', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'aiAiAgentIndex', 'admin', 'd1e2f3a4b5c611f0b6af00455d014203', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'aiAiAgentIndex' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014104', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'aiAiUsageRecordIndex', 'admin', 'd1e2f3a4b5c611f0b6af00455d014204', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'aiAiUsageRecordIndex' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014105', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'aiAiChatSessionIndex', 'admin', 'd1e2f3a4b5c611f0b6af00455d014205', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'aiAiChatSessionIndex' AND `deleted` = '0');

INSERT INTO `sys_role_permission` (`id`, `deleted`, `seq_no`, `version`, `create_by`, `create_time`, `update_by`, `update_time`, `per_code`, `role_code`, `role_per_code`, `org_code`)
SELECT 'd1e2f3a4b5c611f0b6af00455d014106', '0', 0, NULL, 'admin', NOW(), NULL, NOW(), 'aiAiChatDemoIndex', 'admin', 'd1e2f3a4b5c611f0b6af00455d014206', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE `role_code` = 'admin' AND `per_code` = 'aiAiChatDemoIndex' AND `deleted` = '0');
