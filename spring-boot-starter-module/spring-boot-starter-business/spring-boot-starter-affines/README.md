# spring-boot-starter-affines | 家长相亲业务

家长用户为孩子建立资料卡（可多个），包含孩子基础/物质条件、意向对象基础/物质条件及简要描述；并支持资料卡浏览记录与家长关注。

## 模块结构

- **`spring-boot-starter-affines-api`**：对外契约（`Api**Service`、DTO）
- **`spring-boot-starter-affines-biz`**：实现层、`BizAffinesConfiguration`、REST、SQL

生成代码前请先读 [AGENTS.md](../../../AGENTS.md)。

## 数据表（`af` 前缀）

| 表名 | 说明 |
|------|------|
| `af_child_profile` | 孩子资料卡（基础 + 物质 + 简要描述） |
| `af_child_intention` | 意向对象条件（参照 dating `dt_intention`） |
| `af_child_profile_view` | 资料卡浏览记录 |
| `af_parent_follow` | 家长关注某资料卡 |

首版 DDL 见 `spring-boot-starter-affines-biz/src/main/sql/create_*.sql`，需在目标库依次执行。

## 用户端 API

| 路径 | 说明 |
|------|------|
| `GET /cus/affines/afChildProfile/myList` | 我的孩子的资料卡列表 |
| `GET /cus/affines/afChildProfile/detail` | 资料卡详情（含意向） |
| `POST /cus/affines/afChildProfile/add` | 新增资料卡 |
| `POST /cus/affines/afChildProfile/edit` | 编辑资料卡 |
| `POST /cus/affines/afChildProfile/delete` | 删除资料卡 |
| `POST /cus/affines/afChildProfileView/record` | 记录浏览 |
| `GET /cus/affines/afChildProfileView/myList` | 我的浏览记录 |
| `POST /cus/affines/afParentFollow/follow` | 关注资料卡 |
| `POST /cus/affines/afParentFollow/unfollow` | 取消关注 |
| `GET /cus/affines/afParentFollow/followStatus` | 是否已关注 |
| `GET /cus/affines/afParentFollow/myList` | 我的关注列表 |

## 管理端 API

路径前缀 `/mgt/affines/{资源}/list|edit|delete`，资源：`afChildProfile`、`afChildIntention`、`afChildProfileView`、`afParentFollow`。

## 跨模块调用

其他模块仅依赖 `spring-boot-starter-affines-api`，注入 `ApiAfChildProfileService` 等接口。
