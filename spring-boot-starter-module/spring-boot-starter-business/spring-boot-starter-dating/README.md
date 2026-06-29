# spring-boot-starter-dating

婚恋 / 约会垂直业务：匹配与推荐、意向与联系方式申请、偏好、红娘与婚介机构、客户侧与管理端运营能力，以及与统计相关的公开接口。

## Maven 结构

- **`spring-boot-starter-dating-api`**：`pub.module.dating.api.service.*`、常量（含 `DatingTradeGoodsCategoryEnum`）、DTO，供其他模块依赖。
- **`spring-boot-starter-dating-biz`**：业务实现、持久化、`BizDatingAutoConfiguration`、REST 控制器、**MQ 消费端**（`biz/messaging` 包）。

## HTTP 能力（便于检索）

- **客户侧 `cus`**：匹配、推荐、意向、偏好、联系人、红娘关系等。
- **管理侧 `mgt`**：意向审核、匹配规则运营、红娘/门店、推荐配置等。
- **公开 `pub`**：机构/红娘展示、统计等（见 `Pub*`、`Statistic*` 控制器）。

具体路径与参数以 Swagger（`/swagger-ui/index.html`）与各 Controller 注解为准。

## 跨模块协作（MQ）

| Destination | 角色 | Function Bean | 说明 |
|-------------|------|---------------|------|
| `trade.order-goods.paid` | 消费者（group=`dating`） | `datingTradeOrderGoodsPaid` | 支付履约，`BizDatingTradeOrderPaidService` |
| `customer.profile.updated` | 消费者 | `DatingCustomerProfileUpdatedConsumer`（dating-api） | 同步客户冗余快照 |
| `distribution.promoter-role.query` | 应答方（group=`dating`） | `datingDistPromoterRoleQuery` | request-reply 解析推广人角色 |

Stream 绑定见 **`application-dating-messaging.yml`**（本模块 resources，由 runner `spring.config.import` 引入）。

## 与交易模块的协作

交易订单支付完成后，trade 模块发布 `trade.order-goods.paid`；本模块按 **`DatingTradeGoodsCategoryEnum`** 过滤并路由履约逻辑（权益、合约类等），无需再引入独立 trade 插件。

## 内容合法校验（编辑客户资料）

用户端 **`POST /cus/customer/customer/editCurrCusInfo`** 在落库前调用 verification 模块审核（仅真实用户编辑，`updateMockCustomerPartial` 跳过）。

| 项 | 说明 |
|----|------|
| 封装类 | `pub.module.dating.biz.support.DtCustomerContentModerationSupport` |
| 调用点 | `ApiDtCustomerServiceImpl#persistCustomerPatch`（`publishProfileEvent=true` 时） |
| 依赖 | `spring-boot-starter-verification-api` → `ApiContentModerationService` |
| 来源模块 | `cmRecordSourceModuleCode=dating` |
| 默认插件 | `wechat_media_check`（可改为传空走纯人工，见 verification README） |

**送审规则**（仅 patch DTO 中非空字段）：

1. **文字**：`cusNickName`、`cusName`、`cusOccupationalDescription`、`cusDesc`、`cusRemark`、`cusDemand`、`cusMoment`、`cusWechatId`、`cusWxIdNo`、`cusEducationName`、`cusCityResidenceName` 用英文逗号拼接为 **一条 TEXT**。
2. **媒体**：`cusAvatar`、`cusTeenagePhoto`、`cusLifePhoto`（逗号分隔多 URL）；类型由 `MediaUrlClassifier`（verification-api）按后缀区分 IMAGE / VIDEO。

**阻断**：微信文本同步检测 `risky` 时 `IllegalArgumentException`；异步媒体已提交、纯人工待审、插件失败转人工时 **不阻断** 保存。

管理端审核记录在 verification 模块「内容审核」菜单，见 [spring-boot-starter-verification/README.md](../../spring-boot-starter-components/spring-boot-starter-verification/README.md#内容合法校验content-moderation)。
