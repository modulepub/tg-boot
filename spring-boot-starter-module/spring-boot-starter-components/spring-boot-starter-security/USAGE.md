# Spring Security 配置使用说明

## 功能概述

本模块实现了基于 Spring Security 的权限验证功能，支持：

1. **接口权限验证** - 验证用户是否有访问特定接口的权限
2. **模块权限验证** - 验证用户是否有访问某个模块的权限
3. **角色验证** - 验证用户是否拥有特定角色

## 核心组件

### 1. CustomUserDetails
自定义用户详情类，扩展了 Spring Security 的 UserDetails，包含：
- 用户基本信息（ID、用户名、密码等）
- 权限集合（接口权限）
- 角色集合

### 2. CustomUserDetailsService
用户详情服务，负责从数据库加载用户信息和权限：
- 通过用户编码查询用户
- 加载用户角色
- 加载用户权限（通过角色权限关联表）

### 3. CustomPermissionEvaluator
自定义权限评估器，支持：
- 精确权限匹配
- 通配符权限匹配（如 `sys:user:*`）
- 模块权限匹配（如 `sys:user` 匹配 `sys:user:list`、`sys:user:add` 等）

### 4. SecurityConfig
Spring Security 主配置类，配置了：
- 密码编码器（BCrypt）
- 认证管理器
- 安全过滤器链
- 方法安全表达式处理器（配置了 CustomPermissionEvaluator）

**注意**：本模块使用 Spring Security 自带的方法安全机制，通过 `@EnableMethodSecurity` 启用。
不需要自定义 AOP 切面，Spring Security 会自动处理 `@PreAuthorize` 和 `@Secured` 注解。

## 使用方法

### 1. 接口权限验证

使用 Spring Security 自带的 `@PreAuthorize` 注解进行接口权限验证：

**注意**：请使用 `org.springframework.security.access.prepost.PreAuthorize`，而不是自定义的注解。

```java
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    /**
     * 验证是否有 sys:user:list 权限
     */
    @PreAuthorize("hasPermission('sys:user:list')")
    @GetMapping("/list")
    public Result<List<User>> listUsers() {
        // 业务逻辑
        return Result.ok();
    }
    
    /**
     * 验证是否有 sys:user 模块的权限（模块权限验证）
     * 这会匹配 sys:user:list、sys:user:add、sys:user:edit 等所有 sys:user 下的权限
     */
    @PreAuthorize("hasPermission('sys:user')")
    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable String id) {
        // 业务逻辑
        return Result.ok();
    }
    
    /**
     * 组合条件：同时验证角色和权限
     */
    @PreAuthorize("hasRole('ADMIN') and hasPermission('sys:user:delete')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable String id) {
        // 业务逻辑
        return Result.ok();
    }
    
    /**
     * 多个权限之一即可（hasAnyPermission）
     */
    @PreAuthorize("hasAnyPermission('sys:user:add', 'sys:user:edit')")
    @PostMapping("/save")
    public Result<Void> saveUser(@RequestBody User user) {
        // 业务逻辑
        return Result.ok();
    }
}
```

### 2. 角色验证

使用 Spring Security 自带的 `@Secured` 注解进行角色验证：

**注意**：请使用 `org.springframework.security.access.annotation.Secured`，而不是自定义的注解。

```java
import org.springframework.security.access.annotation.Secured;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    /**
     * 验证是否有 ADMIN 角色
     */
    @Secured({"ADMIN"})
    @GetMapping("/dashboard")
    public Result<Dashboard> getDashboard() {
        // 业务逻辑
        return Result.ok();
    }
    
    /**
     * 多个角色之一即可
     */
    @Secured({"ADMIN", "MANAGER"})
    @GetMapping("/reports")
    public Result<List<Report>> getReports() {
        // 业务逻辑
        return Result.ok();
    }
}
```

### 3. 获取当前登录用户信息

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    /**
     * 使用 @AuthenticationPrincipal 获取当前用户
     */
    @GetMapping("/profile")
    public Result<UserProfile> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // userDetails 包含当前登录用户的完整信息
        UserProfile profile = new UserProfile();
        profile.setUsername(userDetails.getUsername());
        profile.setUserCode(userDetails.getUserCode());
        profile.setRoles(userDetails.getRoles());
        profile.setPermissions(userDetails.getPermissions());
        return Result.ok(profile);
    }
    
    /**
     * 或者从 SecurityContext 获取
     */
    @GetMapping("/current")
    public Result<CustomUserDetails> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return Result.ok(userDetails);
        }
        return Result.error("用户未登录");
    }
}
```

### 4. 权限表达式说明

`@PreAuthorize` 注解支持以下表达式：

- `hasPermission('permission')` - 检查是否有指定权限
- `hasRole('role')` - 检查是否有指定角色
- `hasAnyPermission('perm1', 'perm2')` - 检查是否有任一权限
- `hasAnyRole('role1', 'role2')` - 检查是否有任一角色
- `and` - 逻辑与
- `or` - 逻辑或
- `!` - 逻辑非

示例：
```java
// 同时满足多个条件
@PreAuthorize("hasRole('ADMIN') and hasPermission('sys:user:delete')")

// 满足任一条件
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")

// 取反
@PreAuthorize("!hasRole('GUEST')")
```

### 5. 模块权限验证

模块权限验证允许您验证用户是否有访问某个模块的权限，而不需要指定具体的操作权限。

例如，如果用户有 `sys:user:list` 权限，那么验证 `sys:user` 模块权限时会返回 true。

```java
@PreAuthorize("hasPermission('sys:user')")
@GetMapping("/module")
public Result<Void> accessUserModule() {
    // 用户只要有 sys:user 模块下的任何权限都可以访问
    return Result.ok();
}
```

## 配置说明

### 公开接口配置

在 `SecurityConfig` 中配置了以下公开接口（不需要认证）：

- `/pub/**` - 公开接口
- `/actuator/**` - 监控端点
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - API 文档
- `/doc.html` - 文档页面
- `/webjars/**` - WebJars 资源

如需添加更多公开接口，请修改 `SecurityConfig.securityFilterChain()` 方法。

### 数据库表结构要求

本模块依赖以下数据库表：

1. `sys_user` - 用户表
2. `sys_user_role` - 用户角色关联表
3. `sys_role_permission` - 角色权限关联表
4. `sys_permission` - 权限表（菜单表）

权限编码存储在 `sys_permission.perms` 字段中，格式为：`sys:user:list,sys:user:add`（多个权限用逗号分隔）

## 注意事项

1. **用户服务依赖**：确保项目中已引入 `spring-boot-starter-system-api` 和 `spring-boot-starter-system-biz` 模块
2. **权限加载**：用户权限通过角色权限关联表加载，确保角色和权限的关联关系正确配置
3. **密码加密**：默认使用 BCrypt 密码编码器，确保用户密码已正确加密
4. **会话管理**：当前配置为无状态会话（STATELESS），适用于 JWT 认证。如果使用 Session 认证，需要修改配置

## 扩展开发

### 自定义权限验证逻辑

如果需要自定义权限验证逻辑，可以实现 `PermissionEvaluator` 接口或继承 `CustomPermissionEvaluator`：

```java
@Component
public class CustomPermissionEvaluator extends CustomPermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // 自定义验证逻辑
        return super.hasPermission(authentication, targetDomainObject, permission);
    }
}
```

### 添加 JWT 认证过滤器

如果需要 JWT 认证，可以添加 JWT 过滤器：

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        // JWT 认证逻辑
        filterChain.doFilter(request, response);
    }
}
```

然后在 `SecurityConfig` 中添加过滤器：

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    // ... 其他配置
}
```

