# spring-boot-starter-im

**本地即时通讯组件**：基于 **WebSocket + STOMP** 实时推送与 **MySQL** 消息持久化，为应用提供 C2C 会话、消息、好友关系及全员通知能力。已从腾讯云 IM 迁移为自研实现；`initImUser` 返回的连接凭证为占位符 `"local"`，不再签发 UserSig。

> **AI / 开发者**：新增或修改本模块 `crud/entity` 前必读仓库根目录 [AGENTS.md](../../../AGENTS.md)（实体必须 `extends BaseEntity`，技术主键 `id`，业务编码 `imXxxCode` 不得标 `@TableId`）。跨模块协作见 [CROSS_MODULE_COLLABORATION.md](../../../CROSS_MODULE_COLLABORATION.md)。

## 能力概览

| 能力 | 说明 |
|------|------|
| IM 用户 | 与 `sys_user` 同步昵称/头像，维护未读角标 |
| 好友关系 | `im_friend` 双向关系；好友申请在交友域 `dt_contact_apply` |
| C2C 会话与消息 | 文本 `text`、图文 `rich`；支持 `content` 内嵌 JSON 自定义业务消息 |
| 实时通道 | STOMP over WebSocket；事务提交后推送（`TransactionAfterCommit`） |
| 全员通知 | 管理端草稿 → 向 `im_user` 全员群发图文消息 |
| 管理端私信 | 固定系统账号 `system` 与用户会话（见 `ImSpecialUserConstants`） |

## Maven 结构

| 模块 | 职责 |
|------|------|
| **`spring-boot-starter-im-api`** | `Api**Service` 接口、DTO、`*CodeEnum`；**禁止**依赖 `-biz` |
| **`spring-boot-starter-im-biz`** | `BizImAutoConfiguration`、CRUD、HTTP Controller、WebSocket、实现类 |

- 业务模块（如 `dating-biz`）仅依赖 **`spring-boot-starter-im-api`**，通过 `ApiImService` 等同步调用。
- Runner 聚合引入 **`spring-boot-starter-im-biz`** 即可获得自动配置与全部实现。

## 用户标识约定

全链路统一使用 **`sys_user.user_code`**（表列 `im_user_user_code` 等），**不是** `cusCode`。交友域通过客户的 `cusUserCode` 映射到该编码。

特殊账号（`ImSpecialUserConstants`）：

| 常量 | 值 | 用途 |
|------|-----|------|
| `MGT_SYSTEM_USER_CODE` | `system` | 管理端私信发送方 |
| `SYSTEM_NOTICE_USER_CODE` | `TZ0001` | 系统通知用户 |
| `SYSTEM_NOTICE_USER_NAME` | 系统通知 | 展示名 |

## 数据库表

脚本目录：`spring-boot-starter-im-biz/src/main/sql/` 与 `src/main/resources/sql/`。

| 表 | 说明 |
|----|------|
| `im_user` | IM 用户镜像；`im_user_tag`、`im_user_unread_count` |
| `im_friend` | 好友关系；`im_friend_status_code`：`0` 删除 / `1` 正常 |
| `im_conversation` | C2C 会话；`user_a`/`user_b` 按 userCode **字典序** 固定为 A/B |
| `im_message` | 消息记录；`im_message_type_code`：`text` / `rich` |
| `im_user_session` | 多端 WebSocket 会话 |
| `im_notice` | 全员通知；`im_notice_publish_state_code`：`0` 草稿 / `1` 已发送 |
| `im_notice_recipient` | 通知每人发送结果；`im_nc_np_status_code`：`0` 失败 / `1` 成功 |

**已废弃**：`im_friend_apply`（好友申请已迁至交友域）；`im_user` 上腾讯云 SDK 相关列已移除（见 `patch_remove_tencent_im_config.sql`、`alter_im_local_20260615.sql`）。

本地 IM 增量建表见：`src/main/resources/sql/alter_im_local_20260615.sql`。

## 跨模块 API（`spring-boot-starter-im-api`）

### `ApiImService`

账号与兼容桥接（实现：`ApiImLocalServiceImpl`）。

| 方法 | 说明 |
|------|------|
| `saveOrUpdateAccount` | 创建/更新 `im_user` |
| `addFriend` / `removeFriendBidirectional` | 建立/删除 IM 好友（双向） |
| `clearC2cChatBidirectional` | 清空双方会话与漫游消息 |
| `notifyContactRemoved` | WebSocket 推送 `contact_removed` |
| `sendC2CTextMessage` / `sendC2CRichMessage` | 发送文本/图文 |
| `generateUserSig` | 兼容旧接口，返回 `"local"` |
| `logoutByUserCode` | 登出（本地实现保留接口） |

**典型调用方**：`dating-biz` — 联系人申请通过时 `addFriend`；删除联系人时 `removeFriendBidirectional` + `clearC2cChatBidirectional` + `notifyContactRemoved`。

### `ApiImFriendService`

`addFriend`、`isFriend`、`removeFriendBidirectional`、`listFriends`。

### `ApiImMessageService`

`sendMessage`、`markRead`、`listMessages`、`getUnreadCount`、`listUnreadMessages`、`getOrCreateConversation`、`listConversations`、`listMessagesByUserForMgt`。

### `ApiImUserService`

`refreshFromSysUser`、`batchSyncFromSysUsers`（管理端从系统用户同步 IM 资料）。

### `ApiImNoticeService`

`publishAndBroadcast(noticeId)` — 向除发送人外全部 `im_user` 群发图文消息。

## HTTP 接口

统一响应：`Result<T>`。用户端接口从 JWT 解析当前 `userCode`；管理端需相应权限。

### 用户端 `/cus/im`

| 路径 | 方法 | 说明 |
|------|------|------|
| `/cus/im/initImUser` | POST | 初始化 IM 用户，可选 body `tag`；返回 `"local"` |
| `/cus/im/logout` | POST | 退出 |
| `/cus/im/saveOrUpdateProfile` | POST | 同步昵称/头像/姓名 |
| `/cus/im/message/send` | POST | 发消息（HTTP 备用，实时推荐 WebSocket） |
| `/cus/im/message/read` | POST | 标记已读 |
| `/cus/im/message/list` | GET | 会话消息列表 `conversationCode, pageNo, pageSize` |
| `/cus/im/message/unread/count` | GET | 未读总数 |
| `/cus/im/message/conversation/list` | GET | 会话列表 |
| `/cus/im/friend/list` | GET | 好友列表 |
| `/cus/im/friend/delete` | POST | 删除好友 `friendUserCode` |

Controller：`CusImController`、`CusImMessageController`、`CusImFriendController`。

### 管理端 `/mgt/im`

| 路径 | 说明 |
|------|------|
| `/mgt/im/imUser/*` | IM 用户分页、删除、同步、设标签、加系统好友 |
| `/mgt/im/message/*` | 以 `system` 账号查会话/发消息/已读；`listByUser` 审计某用户全部聊天 |
| `/mgt/im/imNotice/*` | 通知 CRUD；`POST /publish` 全员发送 |

管理端发消息前校验：`ApiDtContactService.isMutualContact("system", toUserCode)`。

## WebSocket + STOMP

### 端点

| 路径 | 说明 |
|------|------|
| `/ws/im` | SockJS（浏览器） |
| `/ws/im-native` | 原生 WebSocket（小程序、管理端） |

Security 已放行上述路径；认证由 `ImWebSocketInterceptor` 完成。

### 认证

握手时提供 JWT：

- URL 参数：`token={jwt}`
- 或 Header：`Authorization: Bearer {jwt}`

管理端以系统账号收发时附加：`imUserCode=system`（须与 `MGT_SYSTEM_USER_CODE` 一致）。

示例：

```text
wss://{host}/ws/im-native?token={jwt}&imUserCode=system
```

连接成功后 STOMP `Principal` 为对应 `userCode`。

### STOMP 目的地

| 方向 | 路径 | 说明 |
|------|------|------|
| 发送 | `/app/im/send` | body：`ImMessageSendDTO` |
| 发送 | `/app/im/read` | body：`ImMessageReadDTO` |
| 发送 | `/app/im/ping` | 心跳；回复 `/user/queue/pong` |
| 订阅 | `/user/queue/messages` | 新消息（含 `self_*` 多端同步） |
| 订阅 | `/user/queue/notifications` | 已读回执、未读角标、`contact_removed`、`error` |

消息推送在 **事务提交后** 执行（`ImMessageEventPublisher` + `TransactionAfterCommit`）。

### WebSocket 消息体 `ImWebSocketMessageDTO`

| `type` | 含义 |
|--------|------|
| `text` / `rich` | 收到的新消息 |
| `self_text` / `self_rich` | 发送方其他端同步 |
| `read_receipt` | 已读回执 |
| `unread_count` | 未读角标变化（可触发 HTTP 拉取最新 count） |
| `contact_removed` | 联系人关系已解除 |
| `error` | 发送失败 |
| `pong` | 心跳响应 |

前端参考：`tg-vue/src/utils/im/im-stomp-client.ts`、`tg-vue/src/views/im/imMessageCenter/index.vue`。

## 消息数据格式

### 发送 `ImMessageSendDTO`

```json
{
  "toUserCode": "对方 userCode",
  "typeCode": "text",
  "content": "你好"
}
```

| 字段 | 说明 |
|------|------|
| `typeCode` | `text` 或 `rich` |
| `content` | `text` 必填；`rich` 可选正文 |
| `title` / `imageUrl` | `rich` 必填 |
| `linkUrl` | `rich` 可选跳转 |

### 消息 `ImMessageDTO`（HTTP 响应）

主要字段：`messageCode`、`conversationCode`、`fromUserCode`、`toUserCode`、`typeCode`、`content`、`title`、`imageUrl`、`linkUrl`、`readStatusCode`（`0`/`1`）、`sendStatusCode`（`0`/`1`）、`createTime`。

### 会话 `ImConversationDTO`

`conversationCode`、`peerUserCode`、`peerNickName`、`peerAvatar`、`lastMessageContent`、`lastMessageTime`、`lastActiveTime`、`unreadCount`。

### 已读 `ImMessageReadDTO`

```json
{
  "messageCodes": ["消息业务编码"],
  "fromUserCode": "对方 userCode（WebSocket 已读回执用）",
  "conversationCode": "会话编码"
}
```

### 自定义业务消息（`typeCode=text`，`content` 为 JSON）

在 `content` 中约定 `type` 字段，前后端需保持一致：

| `type` | 说明 |
|--------|------|
| `wx_exchange_request` | 交换微信请求 |
| `wx_exchange_accept` | 接受交换微信 |
| `chat_image` | 图片消息（`imageUrl`） |
| `rich_link` | 图文链接 |
| `contact_removed` | 联系人已解除（也可由 WS `type=contact_removed` 推送） |

解析参考：后端 `ApiImMessageServiceImpl.formatLastMessagePreview`；前端 `tg-vue/src/utils/im/im-message.ts`。

## 枚举与状态码

| 枚举 | 取值 |
|------|------|
| `ImMessageTypeCodeEnum` | `text` / `rich` |
| `ImMessageReadStatusCodeEnum` | `0` 未读 / `1` 已读 |
| `ImFriendStatusCodeEnum` | `0` 删除 / `1` 正常 |
| `ImNoticePublishStateCodeEnum` | `0` 草稿 / `1` 已发送 |
| `ImNoticeTargetTypeCodeEnum` | 当前仅 `all` 全员 |

**注意**：库表与 API 使用字符串 `"0"` / `"1"`，勿使用 `YES` / `NO`。

## 典型对接流程

1. **登录后初始化**：`POST /cus/im/initImUser` → 建立 `im_user`，同步 `sys_user` IM 同步状态。
2. **建立实时连接**：WebSocket 连接 `/ws/im-native`，订阅 `/user/queue/messages` 与 `/user/queue/notifications`。
3. **发消息**：优先 `SEND /app/im/send`；失败或未连接时用 `POST /cus/im/message/send`。
4. **拉历史**：`GET /cus/im/message/list?conversationCode=...`。
5. **会话列表**：`GET /cus/im/message/conversation/list`。
6. **业务加好友**：交友域同意申请后调用 `ApiImService.addFriend`（勿直接写 `im_friend`）。

```text
交友域 dt_contact（业务联系人）  ←→  IM im_friend（聊天好友）
        需通过 ApiImService 显式同步，两层数据独立维护
```

## 扩展与维护

- **新增消息类型**：在 `ImMessageTypeCodeEnum` 与 `ApiImMessageServiceImpl.sendMessage` 增加校验；或沿用 `text` + JSON `content` 约定。
- **新增跨模块能力**：在 `-api` 增加 DTO 与 `Api**Service` 方法，在 `-biz` 实现；**禁止**在 `-api` 引用 `crud.entity`。
- **架构测试**：`mvn -pl spring-boot-starter-architecture-tests -am test`（模块边界与 BaseEntity 约定）。
- **管理端 UI**：`tg-vue/src/views/im/`（用户、消息中心、全员通知）。
