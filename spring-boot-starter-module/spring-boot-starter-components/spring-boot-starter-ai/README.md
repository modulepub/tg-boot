# spring-boot-starter-ai

TG-boot AI 能力组件：OpenAI 兼容协议对接、智能体配置、对话记录与消耗明细。

## 模块结构

- `spring-boot-starter-ai-api`：跨模块契约（`ApiAiChatService`、DTO、枚举）
- `spring-boot-starter-ai-biz`：管理端 CRUD、SPI 渠道实现、对话编排

## 数据库

在目标库依次执行 `spring-boot-starter-ai-biz/src/main/sql/` 下脚本：

1. `create_ai_api_config.sql`
2. `create_ai_agent.sql`
3. `create_ai_chat_session.sql`
4. `create_ai_chat_message.sql`
5. `create_ai_usage_record.sql`
6. `insert_ai_menu.sql`（管理端菜单）

## 管理端能力

| 页面 | 路径 | 说明 |
|------|------|------|
| AI 接口配置 | `/mgt/ai/aiApiConfig/*` | Base URL、API Key、单价 |
| 智能体配置 | `/mgt/ai/aiAgent/*` | 名称、人设、关联接口 |
| 消耗明细 | `/mgt/ai/aiUsageRecord/list` | 只读，含 token 与总价 |
| 对话记录 | `/mgt/ai/aiChatSession/*` | 会话列表与消息详情 |
| 调用示例 | `POST /mgt/ai/chat` | 管理端对话演示 |

## 跨模块调用

```java
@Resource
private ApiAiChatService apiAiChatService;

AiChatRequestDTO req = new AiChatRequestDTO();
req.setAiAgentCode("xxx");
req.setUserCode("user001");
req.setMessage("你好");
AiChatResponseDTO resp = apiAiChatService.chat(req);
```

实体须继承 `BaseEntity`，约定见 [AGENTS.md](../../../AGENTS.md)。
