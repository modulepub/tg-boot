# spring-boot-starter-module

TG-boot 的 **Maven 模块聚合根**：按通用能力与垂直业务拆分为可复用的 `spring-boot-starter-*` 子工程。

## 子聚合说明

| 目录 | 说明 | 详细文档 |
|------|------|----------|
| `spring-boot-starter-components` | 通用组件（系统、交易、文件、插件底座等） | [spring-boot-starter-components/README.md](spring-boot-starter-components/README.md) |
| `spring-boot-starter-business` | 垂直业务（客户、婚恋等） | [spring-boot-starter-business/README.md](spring-boot-starter-business/README.md) |
| `spring-boot-starter-runner` | 默认单体启动器（装配多数 biz 与示例插件） | [spring-boot-starter-runner/README.md](spring-boot-starter-runner/README.md) |

## 给 AI / 开发者的阅读顺序

1. 仓库根目录 [README.md](../README.md) 了解整体与运行方式。
2. 进入 **components** 或 **business** 下与各聚合 `pom.xml` 同级的 **README.md**；契约与实现分别在 `-api` / `-biz`，文档集中在「大模块」目录。
3. 插件与扩展约定见 [spring-boot-starter-common/README.md](spring-boot-starter-common/README.md)。
