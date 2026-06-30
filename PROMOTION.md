# TG-boot 系统推广简介

> 模块化单体架构开发平台，面向 AI 协作与快速业务交付。本文档供对外介绍与方案宣讲使用；技术细节见 [README.md](README.md)、[AGENTS.md](AGENTS.md)。

---

## 平台概览

TG-boot 基于 **Spring Boot 3** 与 **Vue 3**，以 Maven 多模块划分 **common / components / business / runner**。默认将多个能力模块打进同一 `runner` 进程，模块间通过 **`*-api` 契约** 与 **MQ 消息** 协作，模块内多厂商实现通过 **SPI 插件** 切换——兼顾开发效率、边界清晰与后续按需拆服。

**技术栈摘要**：Spring Security、MyBatis-Plus、Redis、RabbitMQ（可嵌入式开发）、多数据库（MySQL / PostgreSQL / Oracle / 达梦等）、管理端 Vue 3 + Element Plus。

---

## 核心能力模块

### 1. AI 模块（spring-boot-starter-ai）

OpenAI 兼容协议对接，提供智能体配置、对话编排、会话与消息记录、Token 消耗明细等完整链路。

| 能力 | 说明 |
|------|------|
| 接口配置 | 管理 Base URL、API Key、计费单价 |
| 智能体 | 人设、提示词、关联接口，可按业务编码调用 |
| 对话服务 | 跨模块 `ApiAiChatService`，统一入参/出参 DTO |
| 运营可视 | 管理端查看会话、消息详情与消耗明细 |

> **贴图位置**：AI 接口配置 / 智能体管理 / 对话演示

![](https://pubpicture.oss-cn-shenzhen.aliyuncs.com/pub/20260630024506275.png)

---

### 2. 即时通讯模块（spring-boot-starter-im）

自研本地 IM：**WebSocket + STOMP** 实时推送，**MySQL** 持久化会话与消息，提供 C2C 单聊、好友关系、未读角标、全员通知及管理端私信等能力（已从第三方云 IM 迁移为平台内建实现，最显著的效果是更快的消息反应速度，另可微服务化）。

| 能力 | 说明 |
|------|------|
| 实时通道 | STOMP over WebSocket；浏览器 SockJS（`/ws/im`），小程序/管理端原生 WebSocket（`/ws/im-native`） |
| 消息持久化 | MyBatis-Plus 落库；文本/图文消息，支持 `content` 内嵌 JSON 扩展业务消息 |
| 认证与安全 | 握手携带 JWT（URL 参数或 `Authorization`）；与 `sys_user` 统一以 `userCode` 标识 |
| 可靠推送 | 写库后通过 `TransactionAfterCommit` 再推送，避免事务未提交即下发 |
| 跨模块 API | `ApiImService` / `ApiImMessageService` 等；交友域联系人变更时同步好友与清空会话 |

**技术栈**：Spring WebSocket + STOMP、Spring Security、JWT、MyBatis-Plus、MySQL；管理端前端基于 Vue 3 STOMP 客户端（`tg-vue` 消息中心）。

> **贴图位置**：消息中心 / WebSocket 会话列表 / 全员通知

![image-20260630024546767](C:\Users\tenda\AppData\Roaming\Typora\typora-user-images\image-20260630024546767.png)

![](https://pubpicture.oss-cn-shenzhen.aliyuncs.com/pub/20260630024805994.png)

---

### 3. 核验模块（spring-boot-starter-verification）

统一核验与合规能力中心：手机号二要素、资产认证、**内容合法校验**（文字 / 图片链接 / 视频链接），当前已接入微信公众平台的内容自动审核API插件。

| 能力 | 说明 |
|------|------|
| 内容合法校验 | 插件化检测，支持同步/异步；每次检测落库审核记录，可人工复审 |
| 手机号二要素 | SPI 渠道（如阿里云），核验结果可追溯 |
| 资产认证 | 多步流程状态机，管理端审核闭环 |
| 跨模块 API | 业务域仅依赖 `verification-api`，如 dating 编辑资料送审 |

> **贴图位置**：内容审核列表 / 人工审核 / 二要素或资产认证流程

![](https://pubpicture.oss-cn-shenzhen.aliyuncs.com/pub/20260630025006588.png)

---

### 4. 微信模块（spring-boot-starter-wechat）

微信生态集成：网页授权、登录回调、小程序内容安全等，与核验模块协同完成异步媒体检测回调。

| 能力 | 说明 |
|------|------|
| 登录与授权 | `WxLoginController` 等，衔接平台账号体系 |
| 小程序内容安全 | 文本同步检测、媒体异步检测，回调更新核验记录 |
| 模块边界 | 支付与商户订单在 trade 模块；本模块侧重身份与开放平台能力 |

> **贴图位置**：微信登录流程 / 小程序内容安全配置 / 回调说明

![image-20260630025229442](C:\Users\tenda\AppData\Roaming\Typora\typora-user-images\image-20260630025229442.png)

---

### 5. 系统模块（spring-boot-starter-system）

平台底座：**组织、用户、角色、权限、验证码、登录会话**，为多端（管理端 / 用户端）提供统一账号与授权，支持**部门角色绑定，在线切换部门切换角色**。

| 能力 | 说明 |
|------|------|
| 组织与权限 | 用户—角色—部门绑定，支持借调、兼任等多部门场景 |
| 登录与会话 | JWT + 缓存快照，权限变更需重新登录，降低越权风险 |
| 事件驱动 | 登录/注册/资料变更通过 MQ 通知 customer、distribution 等域 |

> **贴图位置**：用户组织角色管理 / 权限配置 / 登录与会话架构示意

![image-20260630025313818](C:\Users\tenda\AppData\Roaming\Typora\typora-user-images\image-20260630025313818.png)

---

### 6. 短信模块（spring-boot-starter-sms）

统一短信发送门面，**SPI 插件化适配不同厂商**（如创蓝、腾讯云等），换平台只需改配置或切换实现，业务代码零改动。

| 能力 | 说明 |
|------|------|
| 统一 API | `ApiSmsSendService` / `BizSmsService`，上游无感知厂商差异 |
| 多厂商 SPI | `Spi*SmsServiceImpl` + 注册表路由，可按场景扩展新渠道 |
| 模板与日志 | 模板管理、发送记录，便于运营与排错 |

> **贴图位置**：短信模板 / 厂商配置 / 发送记录

![image-20260630025430633](C:\Users\tenda\AppData\Roaming\Typora\typora-user-images\image-20260630025430633.png)

---

## 其他模块（简要）

| 模块 | 功能摘要 |
|------|----------|
| **customer** | 客户档案、跟进、推广任务、员工看板 |
| **dating** | 婚恋/约会：匹配、意向、红娘门店、统计；对接核验与交易 |
| **trade** | 商品订单、微信支付；支付成功 MQ 驱动下游履约 |
| **distribution** | 分销分佣、钱包；消费支付事件、推广关系 |
| **file** | 分片/秒传上传；本地、MinIO、OSS 等存储 SPI |
| **excel** | 模板化导入导出，业务与渲染解耦 |
| **cms** | 内容节点，公开与管理端接口 |
| **ocr** | 银行卡等 OCR 识别 HTTP 能力 |
| **job** | XXL-Job 定时任务集成 |

---

## 架构优势

### 模块化单体，演进友好

- 源码按 Maven 模块严格切分（`*-api` 契约 + `*-biz` 实现），默认 **单 runner 单 JVM** 部署，运维简单、启动快（冷启动约数秒级，视环境而定）。
- 负载升高时，可将 **高流量模块单独打进独立 runner** 扩容，或继续整包部署；对外由 **网关/路由** 统一分流，**无需重写业务粘连代码**。

### 边界清晰，AI 协作友好

- 跨模块 **只依赖 `*-api`**，禁止 `*-biz` 互引；契约、DTO、MQ 消息类型集中在 api 层，便于 AI 与人工按规范生成代码。
- 仓库提供 [AGENTS.md](AGENTS.md) 硬性约定（实体继承 `BaseEntity`、双主键、`getByCode` 等）及 **架构测试门禁**，减少「能跑但不符合边界」的代码进入主干。

### SPI 插件化，换厂商不改业务

- 短信、内容校验、存储、OCR 等能力均采用 **SPI + 注册表** 模式：新增厂商/插件只需实现接口并注册 Bean，业务侧调用统一门面。
- 配置驱动切换，降低供应商锁定与迁移成本。

### 异步解耦，事务可靠

- 跨模块副作用走 **MQ**（契约在 `*-api/messaging`）；写库与发消息通过 **`publishAfterCommit`** 保证事务提交后再投递，避免脏消息。
- 开发环境可启用嵌入式 Rabbit，生产切换真实 RabbitMQ 即可。

### 多端与安全一体化

- HTTP 分层约定：`cus` 用户端 / `mgt` 管理端 / `pub` 公开接口，路径与权限模型一致。
- Spring Security + JWT + 会话缓存；用户—角色—部门联动，适配复杂组织场景。

### 配套前端与开箱即用

- 管理端 **tg-vue**（Vue 3 + Element Plus + Vite）与后端模块对齐；H5/小程序壳 **tg-matchmaker-h5** 已对接后端。
- 仓库自带 **`.tools` 内置 JDK/Maven** 与 `scripts/run-runner` 一键启动，降低环境差异。

---

## 进一步了解

| 文档 | 内容 |
|------|------|
| [README.md](README.md) | 架构、模块树、MQ 一览、命名约定 |
| [CROSS_MODULE_COLLABORATION.md](CROSS_MODULE_COLLABORATION.md) | 跨模块协作规范 |
| [OPS.md](OPS.md) | 部署、Actuator、生产运维 |
| [AGENTS.md](AGENTS.md) | AI 编码规范与检查清单 |

# 仓库地址及演示环境

- **Gitee 仓库**：https://gitee.com/pub_module/tg-boot.git
