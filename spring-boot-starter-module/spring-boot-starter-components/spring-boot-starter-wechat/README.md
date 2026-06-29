# spring-boot-starter-wechat

**微信集成**：网页授权、登录回调等与微信平台对接的能力（具体 scope 以代码与配置为准）。

## HTTP 入口（检索用）

- **`WxController`**：微信侧回调或通用微信接口。
- **`WxLoginController`**：登录流程相关接口。

路径与参数见 Swagger 与各方法注解。

## Maven 结构

- **`spring-boot-starter-wechat-api`**：DTO、配置常量、与微信交互所需的契约类型。
- **`spring-boot-starter-wechat-biz`**：`BizWxAutoConfiguration`、上述控制器与服务实现。

**支付与商户订单**主要在 **`spring-boot-starter-trade`**（微信支付配置、支付回调）；本模块侧重身份与微信开放平台能力衔接。

## 使用说明

引入 **`spring-boot-starter-wechat-biz`**，在配置文件中填写微信 AppId、密钥等；与其他模块协作时仅依赖 `-api` 即可引用类型。

---

## 微信小程序内容安全插件

供 **verification 模块**内容合法校验插件 `wechat_media_check` 调用（业务域不直接依赖本能力，走 `ApiContentModerationService`）。

### 对外 API（`*-api`）

**接口**：`pub.module.wx.api.service.ApiWxMaContentSecurityService`

| 方法 | 微信 OpenAPI | 说明 |
|------|--------------|------|
| `msgSecCheck` | `POST /wxa/msg_sec_check` | 文本同步检测；`suggest=risky` 视为未通过 |
| `mediaCheckAsync` | `POST /wxa/media_check_async` | 图片/音视频 URL **异步**检测；返回 `trace_id` |
| `resolveDefaultAppId` | — | 首个启用的 `wx_mini_config.appId` |

**实现类**：`pub.module.wx.biz.service.impl.ApiWxMaContentSecurityServiceImpl`（使用 `WxMaService` + Hutool HTTP）。

**请求上下文**：

- `openId`：须为小程序用户 openId（verification 插件通过 `ApiSysUserService#getUserByUserCode` 取 `userWxOpenId`）。
- `scene`：默认 `1`（资料）；可选 2 评论 / 3 论坛 / 4 社交日志。
- `media_type`：图片 `2`；视频链接在插件侧按 `1`（音频通道）提交。

### 异步回调

| 项 | 值 |
|----|-----|
| 路径 | `POST /pub/wx/ma/sec-check/notify`（`/pub/**` 免登录） |
| 控制器 | `WxMaSecCheckCallbackController` |
| 事件 | `Event=wxa_media_check` |
| 落库 | 调用 `ApiContentModerationService#completeAsyncByTraceId`（依赖 `verification-api`） |

**运维**：小程序后台「消息推送」URL 需指向上述回调；媒体 URL 须公网可下载。

### 模块依赖

- `wechat-biz` 依赖 `spring-boot-starter-verification-api`（仅回调更新审核记录）。
- `verification-biz` 依赖 `spring-boot-starter-wechat-api`（插件调微信 API）。
- **禁止** verification-biz ↔ wechat-biz 互依赖。

详细字段与流程见 [spring-boot-starter-verification/README.md](../spring-boot-starter-verification/README.md#内容合法校验content-moderation)。
