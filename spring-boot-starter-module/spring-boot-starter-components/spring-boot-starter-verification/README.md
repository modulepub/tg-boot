# spring-boot-starter-verification

**核验组件**：手机号二要素、资产认证、**内容合法校验（文字 / 图片链接 / 视频链接）** 等能力。跨模块调用只依赖 **`spring-boot-starter-verification-api`**；实现与落库在 **`-biz`**。

> **AI / 开发者**：修改本模块 `crud/entity` 前必读仓库根目录 [AGENTS.md](../../../AGENTS.md)（须 `extends BaseEntity`；业务主键 `xxxCode` 不得标 `@TableId`）。  
> 内容合法校验的完整约定以**本文「内容合法校验」章节**为准，勿按旧版「结果字段 2/E」实现。

## Maven 结构

| 子模块 | 职责 |
|--------|------|
| `spring-boot-starter-verification-api` | `Api**Service`、DTO、枚举、常量；**禁止**依赖 `-biz` |
| `spring-boot-starter-verification-biz` | 实体/Mapper/Service、SPI 插件、`BizVerificationAutoConfiguration`、HTTP 控制器 |

Runner 聚合：`spring-boot-starter-runner` 依赖 `spring-boot-starter-verification-biz`。

---

## 内容合法校验（Content Moderation）

### 能力概述

- 支持检测类型：**TEXT**（文字）、**IMAGE**（图片 URL）、**VIDEO**（视频 URL）。
- **插件化**：请求参数 `cmRecordPluginCode` 指定实现；**传空 / 不传则仅落库，走人工审核**，不调用第三方。
- 每次检测写入审核表 **`vt_cm_record`**，含：审核内容、是否通过、是否异步、流程状态、插件编码、备注（第三方原始 JSON）。
- 已接入业务：**dating 用户端编辑客户资料**（`editCurrCusInfo`）；其他域依赖 `ApiContentModerationService` 即可。

### 跨模块 API（`*-api`）

**接口**：`pub.module.verification.api.service.ApiContentModerationService`

| 方法 | 说明 |
|------|------|
| `moderate(ContentModerationRequest)` | 批量检测并落库；返回 `ContentModerationBatchResult`（`passed` 是否允许业务继续） |
| `completeAsyncByTraceId(ContentModerationAsyncCallbackDTO)` | 异步回调更新（微信 `wxa_media_check`）；一般由 wechat-biz 回调控制器调用 |

**请求** `ContentModerationRequest` 要点：

| 字段 | 必填 | 说明 |
|------|------|------|
| `cmRecordPluginCode` | 否 | 插件编码，如 `wechat_media_check`；**空 = 纯人工审核** |
| `cmRecordSourceModuleCode` | 是 | 发起模块，如 `dating` |
| `cmRecordBizCode` | 否 | 业务主体，如 `cusCode` |
| `cmRecordUserCode` | 否 | 用户编码（微信插件需据此取 `userWxOpenId`） |
| `wxMaAppId` / `wxSecCheckScene` | 否 | 微信插件上下文；appId 缺省取首个启用的 `wx_mini_config` |
| `items` | 是 | `ContentModerationItemDTO` 列表：`cmRecordContentTypeCode` + `cmRecordContent` |

**HTTP（可选）**：`POST /verification/content-moderation`（`ContentModerationController`）

### 插件 SPI（仅 `-biz` 内）

| 类 | 说明 |
|----|------|
| `SpiContentModerationPlugin` | 插件契约：`pluginCode()` + `check(context, typeCode, content)` |
| `ContentModerationPluginRegistry` | 按 `pluginCode` 路由（参考 `SmsProviderRegistry`） |
| `WechatSpiContentModerationPlugin` | 编码 **`wechat_media_check`**；依赖 **`wechat-api`** 的 `ApiWxMaContentSecurityService` |

**新增插件步骤**：

1. 在 `verification-api/constants` 增加 `ContentModerationPluginCodeEnum` 常量（若需对外暴露）。
2. 在 `-biz` 实现 `SpiContentModerationPlugin` 并注册为 Spring Bean。
3. 返回 `ContentModerationPluginOutcome`（见下表字段映射）。

微信插件实现细节见 [spring-boot-starter-wechat/README.md](../spring-boot-starter-wechat/README.md#微信小程序内容安全插件)。

### 审核记录表 `vt_cm_record`

**实体**：`pub.module.verification.crud.entity.CmRecord`（`extends BaseEntity`）

| 字段 | 类型/约定 | 说明 |
|------|-----------|------|
| `cmRecordCode` | 业务编码 | 落库时雪花生成 |
| `cmRecordSourceModuleCode` | string | 来源模块 |
| `cmRecordBizCode` | string | 业务主体编码 |
| `cmRecordUserCode` | string | 发起用户 |
| `cmRecordContentTypeCode` | TEXT / IMAGE / VIDEO | 内容类型 |
| `cmRecordContent` | text | 文本或媒体 URL |
| `cmRecordPluginCode` | string，可空 | 插件编码；空表示人工审核通道 |
| `cmRecordPassedStatusCode` | **StatusCode `0`/`1`** | **是否通过**；流程未结束时为 `null` |
| `cmRecordNotPassedReason` | string(512) | **未通过原因**；`passed=0` 或人工驳回时填写；通过时为空 |
| `cmRecordAsyncStatusCode` | **StatusCode `0`/`1`** | **是否异步**（0 同步，1 异步） |
| `cmRecordProcessCode` | enum | **流程**：见 `CmRecordProcessCodeEnum` |
| `cmRecordRemark` | text | **备注**：第三方原始 JSON；人工驳回原因追加在此 |
| `cmRecordVendorTraceId` | string | 异步 trace_id（回调匹配） |
| `cmRecordAuditBy` / `cmRecordAuditAt` | | 人工审核人与时间 |

**流程枚举** `CmRecordProcessCodeEnum`（`*ProcessCode`，非 StatusCode）：

| code | 含义 |
|------|------|
| `0` | 待审核（人工队列 / 插件失败转人工） |
| `1` | 审核中（已提交微信 mediaCheckAsync，等回调） |
| `2` | 审核结束（同步终态或异步回调完成或人工审结） |

**禁止**再使用已废弃字段：`cmRecordSuggestCode`、`cmRecordLabelCode`、`cmRecordVendorMessage`、`cmRecordVendorRaw`；第三方详情统一进 `cmRecordRemark`。

### 业务阻断规则（`moderate` 返回值）

| 场景 | process | async | passed | 业务 `passed` |
|------|---------|-------|--------|----------------|
| 插件为空 | 0 待审核 | 0 | null | **true**（不阻断，等人审） |
| 插件 API 失败 | 0 待审核 | 0 | null | **true**（转人工） |
| 微信文本 sync 通过 | 2 结束 | 0 | 1 | true |
| 微信文本 sync risky | 2 结束 | 0 | 0 | **false**（阻断） |
| 微信媒体 async 已提交 | 1 审核中 | 1 | null | **true**（等回调） |
| 微信 async 回调 pass | 2 结束 | 1 | 1 | — |
| 微信 async 回调 risky | 2 结束 | 1 | 0 | — |

同步文本未通过时 `ContentModerationBatchResult.passed=false`，`items[].cmRecordNotPassedReason` 与 `blockedMessage` 均含原因；调用方（如 dating）当前仍仅用 `blockedMessage` 抛异常，业务侧可后续读取 `items` 细粒度原因。

### 管理端人工审核

**API 接口**：`pub.module.verification.api.service.ApiContentModerationMgtService`

| HTTP | 说明 |
|------|------|
| `GET /mgt/verification/cmRecord/list` | 分页列表 |
| `GET /mgt/verification/cmRecord/queryById` | 详情 |
| `POST /mgt/verification/cmRecord/approve?id=` | 人工通过（仅 **process=0 待审核**） |
| `POST /mgt/verification/cmRecord/reject` | 人工驳回（body: `id`, `rejectReason`） |
| `POST /mgt/verification/cmRecord/delete` | 按 `cmRecordCode` 批量删 |

**前端**：`tg-vue/src/views/verification/cmRecord/index.vue`  
**菜单 SQL**：`spring-boot-starter-verification-biz/src/main/sql/insert_vt_cm_record_menu.sql`（父级 `verificationSystem`）

权限码：`cmRecordList` / `cmRecordApprove` / `cmRecordReject`（`verification:cmRecord:*`）

### SQL 脚本

| 文件 | 用途 |
|------|------|
| `create_vt_cm_record.sql` | 新环境建表 |
| `alter_vt_cm_record_refactor.sql` | 旧表迁移（字段重构） |
| `alter_vt_cm_record_add_not_passed_reason.sql` | 增量：未通过原因字段 |
| `insert_vt_cm_record_menu.sql` | 管理端菜单与权限 |

### 工具类（api）

- `pub.module.verification.api.util.MediaUrlClassifier`：按 URL 后缀推断 IMAGE / VIDEO（dating 组装 items 时使用）。

### 依赖边界

- `verification-biz` 可依赖 **`spring-boot-starter-wechat-api`**（微信插件）。
- `verification-biz` **禁止**依赖 `wechat-biz`。
- 业务域（如 dating-biz）只依赖 **`verification-api`**，通过 `ApiContentModerationService` 调用。

---

## 手机号二要素

- **API**：`ApiPhoneTwoFactorVerifyService#verify`
- **HTTP**：`POST /verification/phone-two-factor`
- **记录表**：`vt_np_record`（`NpRecord`）
- **SPI**：`SpiPhoneTwoFactorChannel`（默认 `AliyunSpiPhoneTwoFactorChannel`）
- **MQ**：事务提交后 `verification.phone-two-factor.verified`

---

## 资产认证（爱与诚）

- **API**：`ApiVtAssetCertService` / `ApiVtAssetCertMgtService`
- **记录表**：`vt_asset_cert_record`（`VtAssetCertRecord`）
- **流程枚举**：`VtAssetCertProcessCodeEnum`
- **管理端**：`/mgt/verification/vtAssetCert/*`；前端 `tg-vue/src/views/verification/assetCert/`

---

## AI 扩展检查清单（内容合法校验）

- [ ] `ApiContentModerationService` 入参/返回值均为 `*-api` DTO，无 `crud.entity`
- [ ] `CmRecord extends BaseEntity`；`cmRecordCode` 非 `@TableId`
- [ ] `cmRecordPassedStatusCode` / `cmRecordAsyncStatusCode` 仅 `"0"` / `"1"`；流程用 `cmRecordProcessCode`
- [ ] 新插件实现 `SpiContentModerationPlugin` + 注册表，不写在 `-api`
- [ ] 跨模块写库后若有 MQ，使用 `publishAfterCommit`
- [ ] 增量 DDL 用新 `alter_*.sql`，勿改已执行过的 `create_*.sql` 内容
- [ ] 管理端 `*StatusCode` 筛选项 value 为 `"0"` / `"1"`，展示用 `isStatusYes(code)`
