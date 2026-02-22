# TG-Excel | Excel 导入导出模块

TG-Excel 模块提供了强大的 Excel 导入导出功能，采用模板驱动的方式，实现业务代码与 Excel 处理逻辑的完全解耦。

## 核心优势

### 1. 对业务代码零侵入
- **业务模块专注数据提供**：业务模块只需要提供标准的数据接口，无需关心 Excel 的格式、样式、合并单元格等复杂逻辑
- **Excel 模块专注渲染**：Excel 模块负责根据模板将数据渲染成 Excel 文件，支持所有 Excel 特性（样式、函数、图表等）
- **完全解耦**：导入导出功能与业务代码完全分离，业务代码中不需要引入任何 Excel 相关的依赖

### 2. 基于模板的灵活配置
- 使用 Excel 模板定义导出格式，支持复杂的表格样式、合并单元格、公式等
- 模板可以随时调整，无需修改代码
- 支持多语言、多场景的模板切换

### 3. 完整的导入流程管理
- 支持模板下载，确保导入数据格式正确
- 支持历史导入记录查询，可查看导入状态和异常情况
- 支持导入结果下载，包含错误信息明细
- 异步导入处理，不阻塞用户操作

## 前端组件使用说明

### tg-excel-import 导入组件

**组件位置**：`tg-manage-vue/src/components/tg-excel-import/index.vue`

**Props 参数**：
- `push`：导入接口地址（必填）
- `template`：模板下载链接（必填）

**使用示例**：
```vue
<template>
  <tg-excel-import
    push="/mgt/customer/customer/add"
    template="https://matchlove.oss-cn-beijing.aliyuncs.com/%E5%AE%A2%E6%88%B7%E5%AF%BC%E5%85%A5.xlsx"
  >
    导入
  </tg-excel-import>
</template>
```

**功能特性**：
- 支持拖拽上传和点击上传
- 自动下载导入模板
- 显示历史导入记录
- 支持查看导入状态（已完成/未完成）
- 支持查看异常情况（正常/异常）
- 支持下载导入结果文件
- 文件大小限制：最大 100MB

**导入流程**：
1. 用户点击"导入"按钮打开对话框
2. 可选：点击"下载模板"获取标准导入模板
3. 上传填写好的 Excel 文件
4. 系统异步处理导入任务
5. 可在"历史导入记录"中查看导入状态
6. 导入完成后可下载结果文件（包含错误信息）

### tg-excel-export 导出组件

**组件位置**：`tg-manage-vue/src/components/tg-excel-export/index.vue`

**Props 参数**：
- `data`：数据接口地址（必填）
- `template`：导出模板路径（必填）

**使用示例**：
```vue
<template>
  <tg-excel-export
    :data="`${state.dataListUrl}?pageNo=${state.pageNo}&pageSize=${state.pageSize}`"
    template="https://matchlove.oss-cn-beijing.aliyuncs.com/exportTemplate.xlsx"
  >
    导出
  </tg-excel-export>
</template>
```

**功能特性**：
- 一键导出当前查询条件下的数据
- 自动生成带时间戳的文件名（格式：年月日时分秒.xlsx）
- 支持大数据量导出
- 支持自定义查询参数

## 模板配置说明

### 导入模板配置

导入模板需要包含以下要素：
- 第一列表头：定义数据字段名称，批注中加入导入的字段名称，后端自动读取每一列并封装成对象以json的形式post业务数据接收接口。

**模板配置步骤**：
1. 在 Excel 中设计导入模板
2. 上传模板到文件服务器（如阿里云 OSS）
3. 在前端组件中配置模板下载链接
4. 后端根据模板解析数据并执行导入

**模板配置示例**：
```
模板字段说明：
- 客户姓名：必填，文本类型
- 手机号：必填，11位数字
- 身份证号：选填，18位数字
- 客户来源：选填，下拉选择
- ...
```

### 导出模板配置

导出模板
自定义任何形式的内容，只需要填值的地方与导入的数据源路径一致即可，路径格式为${xx/xx},如果是数组对象，只需要把数组对象的访问路径配置到第一列的批注里，后续列取值为该迭代对象的字段。
## 后端接口说明

### 导入接口

**接口路径**：`POST /cus/excel/import`

**请求参数**：
- 自动传入当前的主要Header到pushUrl,不侵入接口鉴权模块
- `file`：上传的 Excel 文件
- `pushUrl`：业务数据接口地址

**响应结果**：
```json
{
  "code": 200,
  "msg": "success",
  "data": "batchId"
}
```

### 导出接口

**接口路径**：`GET /cus/excel/export`

**请求参数**：
- 自动传入当前的主要Header到dataUrl,不侵入接口鉴权模块
- `dataUrl`：业务数据接口地址
- `templatePath`：导出模板路径

**响应结果**：Excel 文件流

### 导入状态查询接口

**接口路径**：`GET /cus/excel/getImportStatus`

**请求参数**：
- `keys`：批量ID（多个用逗号分隔）

**响应结果**：
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "batchId": "xxx",
      "excelName": "客户导入.xlsx",
      "completed": true,
      "hasError": false,
      "beginImportTime": "2026-02-22 10:00:00",
      "completeImportTime": "2026-02-22 10:05:00"
    }
  ]
}
```

### 导入结果下载接口
- 自动新增一列包含每一行数据的导入结果。

**接口路径**：`GET /cus/excel/downloadImportResult`

**请求参数**：
- `batchId`：批量ID

**响应结果**：Excel 文件流

## 业务代码无侵入的优势详解

### 传统方式的痛点

传统 Excel 导入导出通常存在以下问题：

1. **业务代码耦合严重**
   - 业务代码中充斥着 Excel 相关的代码
   - 需要引入 POI、EasyExcel 等依赖
   - 代码可读性差，维护困难

2. **格式调整困难**
   - 修改 Excel 格式需要修改代码
   - 需要重新编译部署
   - 无法快速响应业务需求变化

3. **灵活性差**
   - 难以支持复杂的 Excel 特性
   - 样式、公式等需要硬编码
   - 多场景需要多套代码

4. **职责不清**
   - 业务代码既要处理业务逻辑，又要处理 Excel 格式
   - 违反单一职责原则

### TG-Excel 的解决方案

1. **完全解耦**
   - 业务模块只提供数据接口，不关心 Excel 格式
   - Excel 模块独立处理模板渲染和数据填充
   - 通过接口地址进行松耦合通信

2. **模板驱动**
   - 所有格式、样式、公式都在模板中定义
   - 修改格式只需更新模板，无需改代码
   - 支持非技术人员调整格式

3. **职责清晰**
   - 业务模块：专注业务逻辑和数据提供
   - Excel 模块：专注模板解析和数据渲染
   - 前端组件：专注用户交互和流程管理

4. **易于扩展**
   - 新增导入导出场景只需添加模板
   - 支持多语言、多场景模板切换
   - 无需修改业务代码

5. **提升开发效率**
   - 减少重复代码
   - 降低维护成本
   - 加快开发速度

## 实战案例

### 客户管理模块导入导出

**场景**：客户管理模块需要支持批量导入客户信息和导出客户列表

**实现步骤**：

1. **准备导入模板**
   - 设计 Excel 模板，包含客户姓名、手机号、身份证号等字段
   - 上传到文件服务器：`https://matchlove.oss-cn-beijing.aliyuncs.com/%E5%AE%A2%E6%88%B7%E5%AF%BC%E5%85%A5.xlsx`

2. **准备导出模板**
   - 设计 Excel 模板，包含表头、样式、统计信息
   - 上传到文件服务器：`https://matchlove.oss-cn-beijing.aliyuncs.com/exportTemplate.xlsx`

3. **前端集成**
```vue
<template>
  <el-space>
    <tg-excel-import
      push="/mgt/customer/customer/add"
      template="https://matchlove.oss-cn-beijing.aliyuncs.com/%E5%AE%A2%E6%88%B7%E5%AF%BC%E5%85%A5.xlsx"
    >
      导入
    </tg-excel-import>
    <tg-excel-export
      :data="`${state.dataListUrl}?pageNo=${state.pageNo}&pageSize=${state.pageSize}`"
      template="https://matchlove.oss-cn-beijing.aliyuncs.com/exportTemplate.xlsx"
    >
      导出
    </tg-excel-export>
  </el-space>
</template>
```

4. **后端接口**
   - 客户数据接口：`/mgt/customer/customer/list`
   - 客户新增接口：`/mgt/customer/customer/add`
   - Excel 模块自动处理导入导出逻辑

**优势体现**：
- 客户模块代码无需任何修改
- 导入导出功能独立于业务逻辑
- 模板可随时调整，无需重新部署

## 框架环境

- springboot & mybatis plus

## 开发环境

- IDE(JAVA)： Eclipse安装lombok插件 或者 IDEA
- 依赖管理：Maven
- 数据库：MySQL5.7+ & Oracle 11g

## 版本信息

当前最新版本：取决于所在项目的springboot框架版本，请查看pom.xml文件中依赖的springboot版本。
