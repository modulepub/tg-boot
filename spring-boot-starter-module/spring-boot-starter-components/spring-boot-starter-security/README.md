# Spring Boot Starter Security

## 功能说明

该模块基于Spring Security实现了接口鉴权和微服务鉴权功能，支持：

- JWT令牌认证
- 微服务间安全通信
- 接口级权限控制
- 自定义认证注解

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>pub.module</groupId>
    <artifactId>spring-boot-starter-security-biz</artifactId>
    <version>1.1.8.RELEASE</version>
</dependency>
```

### 2. 配置参数

在application.yml中添加以下配置：

```yaml
security:
  jwt:
    secret: your-secret-key # 密钥，建议使用环境变量配置
    expiration: 3600000 # 过期时间（毫秒）
    issuer: your-issuer # 发行者
```

### 3. 使用示例

#### 接口鉴权

```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public UserProfile getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // 处理请求
    }
}
```

#### 微服务鉴权

```java
@Service
public class ServiceClient {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RestTemplate restTemplate;

    public void callOtherService() {
        String serviceToken = jwtTokenProvider.generateServiceToken("service-a");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(serviceToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // 调用其他微服务
        restTemplate.exchange("http://service-b/api/resource", HttpMethod.GET, entity, String.class);
    }
}
```

## 核心组件

### JwtTokenProvider

用于生成和验证JWT令牌的工具类：

- `generateToken(Authentication)`: 生成用户认证令牌
- `generateServiceToken(String)`: 生成微服务通信令牌
- `validateToken(String)`: 验证令牌有效性
- `getUsernameFromToken(String)`: 从令牌获取用户名/服务名

### JwtAuthenticationFilter

JWT认证过滤器，自动拦截请求并验证令牌。

### 自定义注解

- `@PreAuthorize`: 方法级权限控制
- `@Secured`: 角色控制
- `@AuthenticationPrincipal`: 获取当前认证对象

## 扩展配置

### 自定义UserDetailsService

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 实现用户查询逻辑
    }
}
```

### 自定义JWT过滤器

```java
@Component
public class CustomJwtAuthenticationFilter extends JwtAuthenticationFilter {
    // 扩展自定义逻辑
}
```
