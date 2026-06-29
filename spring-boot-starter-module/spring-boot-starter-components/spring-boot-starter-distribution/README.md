# spring-boot-starter-distribution

**分销账单与结算**：用户账单汇总、结算记录、结算批次、分佣计算等。

## Maven 结构

- **`spring-boot-starter-distribution-api`**：`ApiDistUserBillSummaryService`、`ApiDistSettleBatchService` 等。
- **`spring-boot-starter-distribution-biz`**：业务实现、MQ 消费（`biz/messaging` 包）。

## 跨模块协作（MQ）

| Destination | 角色 | 说明 |
|-------------|------|------|
| `trade.order-goods.paid` | 消费（group=`distribution`） | 支付成功后更新账单汇总与结算记录 |
| `system.user.registered` | 消费（group=`distribution`） | 注册成功后初始化用户账单汇总 |

管理端菜单：分销系统 → 用户账单汇总 / 结算记录 / 结算批次。

绑定见 `application-distribution-messaging.yml`。

## 状态字段约定

本模块 `*StatusCode` 字段（如 `distSettledStatusCode`、`distSettleBatchStatusCode`）遵循全站 **`StatusCodeEnum`：`"0"`=否、`"1"`=是**。

- 后端请使用 `StatusCodeEnum.YES.getCode()` / `NO.getCode()` 读写。
- 勿在注释或前端使用 `YES`/`NO` 作为接口取值；早期 SQL 注释中的 `YES-是 NO-否` 为误导性笔误。
- 管理端：`tg-vue/src/views/distribution/*`；H5：`matchmaker-web/src/utils/statusCode.ts`。
