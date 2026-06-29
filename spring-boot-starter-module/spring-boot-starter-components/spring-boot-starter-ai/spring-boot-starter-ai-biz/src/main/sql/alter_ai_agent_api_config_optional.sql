-- 智能体接口配置编码改为可选（已执行 create_ai_agent.sql 的库执行一次）
ALTER TABLE `ai_agent`
  MODIFY COLUMN `ai_api_config_code` varchar(64) NULL DEFAULT NULL COMMENT '关联 AI 接口配置编码（可选）';
