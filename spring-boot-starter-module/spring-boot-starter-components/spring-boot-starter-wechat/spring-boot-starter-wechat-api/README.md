微信开发平台SDK
===============

当前最新版本： 3.0.0（发布日期：20240402）
实现了微信公众号、微信小程序、登录消息订阅功能，相关方法封装成WxUtil。


## 技术架构
- 基础框架：Spring Boot

- 持久层框架：Mybatis-plus 3.4.1

- 数据库连接池：阿里巴巴Druid 1.1.22

- 日志打印：logback

- 其他：fastjson，poi，Swagger-ui，quartz, lombok（简化代码）等。



## 技术文档

#### 一、微信小程序登录和公众号登录：WxUtil.miniLogin，WxUtil.mpLogin

```
通过前端获取用户授权code，获取用户openId和unionId.
```

#### 一、微信关注消息事件SubscribeEvent

```
通过发布关注事件，事件中包含用户openId和unionId
```