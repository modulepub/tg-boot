# spring-boot-starter-runner

**默认单体应用启动模块**：`StartApplication` 为 `@SpringBootApplication` 入口，聚合引入多个 `*-biz` 与示例 **`spring-boot-starter-dating-trade-plugin`**，用于开箱演示完整链路。

## 职责

- 打包可执行 Spring Boot 应用（胖 JAR）。
- 启动时合并 **外置插件** 主源：`ExternalPluginBootstrap.resolveExtraPrimarySources`，与 `common` 中外置 JAR 插件目录配置配合。
- 控制台打印代码生成页与 Swagger 地址（见 `StartApplication#getString`）。

## 依赖关系说明

`pom.xml` 中显式引入的各 `*-biz` 即本仓库默认启用的业务能力；裁剪应用时可自建另一 runner 模块，仅保留需要的 biz 依赖。

## 相关文档

- 模块总览：[spring-boot-starter-module/README.md](../README.md)
- 插件机制：[spring-boot-starter-common/README.md](../spring-boot-starter-common/README.md)
