# 系统改进设计文档

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 提升系统安全性和可靠性，解决当前存在的安全漏洞和配置问题。

**Architecture:** 基于现有Spring Boot + Vue 3架构，进行安全增强和配置优化。

**Tech Stack:** Spring Boot 2.x, Vue 3, MySQL, Spring Security, JWT

---

## 1. 项目概述

### 1.1 系统架构

- **后端:** Spring Boot 2.x + JPA + MySQL
- **前端:** Vue 3 + Element Plus + Vue Router
- **角色:** 管理员、店家、顾客

### 1.2 核心功能模块

- **用户管理:** 注册、登录、个人信息管理
- **店铺管理:** 店铺信息维护、服务管理
- **订单管理:** 订单创建、查询、状态更新
- **评论管理:** 评论发布、查看、管理
- **服务管理:** 服务类型、价格管理

## 2. 现有系统分析

### 2.1 安全问题

1. **安全配置缺失:**
   - 禁用了CSRF保护
   - 所有请求无需认证
   - 没有实现权限控制

2. **密码处理不安全:**
   - 明文存储密码
   - 登录时明文比较密码

3. **前端存储不安全:**
   - 使用localStorage存储用户信息
   - 没有实现前端路由权限控制

### 2.2 配置问题

1. **数据库配置:**
   - JPA配置为`create`，每次启动都会重新创建表结构
   - 可能导致数据丢失

2. **错误处理:**
   - 异常处理不规范
   - 没有统一的错误响应格式

3. **日志记录:**
   - 日志记录不完整
   - 没有结构化的日志格式

## 3. 改进方案设计

### 3.1 安全增强

#### 3.1.1 后端安全

1. **Spring Security配置:**
   - 启用CSRF保护
   - 实现基于角色的权限控制
   - 配置安全的HTTP头

2. **密码加密:**
   - 使用BCrypt加密存储密码
   - 实现安全的密码策略

3. **认证机制:**
   - 实现JWT认证
   - 添加token刷新机制
   - 实现token失效处理

#### 3.1.2 前端安全

1. **存储优化:**
   - 使用SessionStorage存储敏感信息
   - 实现前端token管理

2. **路由权限控制:**
   - 实现基于角色的路由守卫
   - 防止未授权访问

3. **输入验证:**
   - 增强前端表单验证
   - 防止XSS攻击

### 3.2 配置优化

1. **数据库配置:**
   - 修改JPA配置为`update`
   - 添加数据库连接池配置

2. **错误处理:**
   - 实现统一的异常处理
   - 定义标准的错误响应格式

3. **日志配置:**
   - 配置结构化日志
   - 添加业务操作日志

### 3.3 性能优化

1. **数据库优化:**
   - 添加适当的索引
   - 优化查询语句

2. **缓存机制:**
   - 实现Redis缓存
   - 缓存热点数据

3. **代码优化:**
   - 优化服务层逻辑
   - 减少不必要的数据库查询

## 4. 技术实现细节

### 4.1 后端实现

1. **Spring Security配置:**
   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig extends WebSecurityConfigurerAdapter {
       @Override
       protected void configure(HttpSecurity http) throws Exception {
           http
               .csrf().enable()
               .authorizeRequests()
               .antMatchers("/api/login", "/api/register").permitAll()
               .anyRequest().authenticated()
               .and()
               .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
       }
   }
   ```

2. **JWT工具类:**
   ```java
   @Component
   public class JwtUtils {
       private String secret = "your-secret-key";
       private long expiration = 86400000; // 24小时

       public String generateToken(User user) {
           Map<String, Object> claims = new HashMap<>();
           claims.put("id", user.getId());
           claims.put("username", user.getUsername());
           claims.put("roleId", user.getRoleId());
           return Jwts.builder()
               .setClaims(claims)
               .setExpiration(new Date(System.currentTimeMillis() + expiration))
               .signWith(SignatureAlgorithm.HS512, secret)
               .compact();
       }

       public Claims parseToken(String token) {
           return Jwts.parser()
               .setSigningKey(secret)
               .parseClaimsJws(token)
               .getBody();
       }
   }
   ```

3. **密码加密:**
   ```java
   @Service
   public class UserServiceImpl implements UserService {
       @Autowired
       private BCryptPasswordEncoder passwordEncoder;

       @Override
       public User save(User user) {
           user.setPassword(passwordEncoder.encode(user.getPassword()));
           return userRepository.save(user);
       }

       @Override
       public User findByUsernameAndPassword(String username, String password) {
           User user = userRepository.findByUsername(username);
           if (user != null && passwordEncoder.matches(password, user.getPassword())) {
               return user;
           }
           return null;
       }
   }
   ```

### 4.2 前端实现

1. **路由守卫:**
   ```javascript
   router.beforeEach((to, from, next) => {
       const token = sessionStorage.getItem('token');
       const user = JSON.parse(sessionStorage.getItem('user'));
       
       if ((to.path !== '/login' && to.path !== '/register') && !token) {
           next('/login');
       } else if (to.path.startsWith('/admin') && user && user.roleId !== 3) {
           next('/customer/home');
       } else if (to.path.startsWith('/shopkeeper') && user && user.roleId !== 1) {
           next('/customer/home');
       } else {
           next();
       }
   });
   ```

2. **API请求拦截器:**
   ```javascript
   axios.interceptors.request.use(config => {
       const token = sessionStorage.getItem('token');
       if (token) {
           config.headers.Authorization = `Bearer ${token}`;
       }
       return config;
   });
   ```

## 5. 测试计划

### 5.1 安全测试

1. **认证测试:**
   - 测试JWT token生成和验证
   - 测试过期token处理
   - 测试无效token处理

2. **授权测试:**
   - 测试基于角色的权限控制
   - 测试未授权访问拦截

3. **密码测试:**
   - 测试密码加密存储
   - 测试密码验证

### 5.2 功能测试

1. **用户管理:**
   - 测试注册功能
   - 测试登录功能
   - 测试个人信息修改

2. **订单管理:**
   - 测试订单创建
   - 测试订单查询
   - 测试订单状态更新

3. **店铺管理:**
   - 测试店铺信息维护
   - 测试服务管理

### 5.3 性能测试

1. **响应时间测试:**
   - 测试API响应时间
   - 测试页面加载时间

2. **并发测试:**
   - 测试多用户并发访问
   - 测试系统稳定性

## 6. 实施步骤

### 6.1 后端实施

1. **添加依赖:**
   - Spring Security
   - JWT
   - BCrypt

2. **配置Spring Security:**
   - 实现安全配置
   - 实现JWT过滤器

3. **修改用户服务:**
   - 实现密码加密
   - 修改登录逻辑

4. **添加异常处理:**
   - 实现统一异常处理
   - 定义错误响应格式

### 6.2 前端实施

1. **修改存储方式:**
   - 从localStorage改为SessionStorage
   - 实现token管理

2. **增强路由守卫:**
   - 实现基于角色的权限控制
   - 防止未授权访问

3. **添加API拦截器:**
   - 实现请求拦截器
   - 实现响应拦截器

### 6.3 数据库配置

1. **修改JPA配置:**
   - 从`create`改为`update`
   - 添加连接池配置

2. **添加索引:**
   - 为常用查询字段添加索引
   - 优化数据库性能

## 7. 风险评估

### 7.1 潜在风险

1. **兼容性风险:**
   - 现有系统可能依赖当前的配置
   - 前端存储方式变更可能影响现有功能

2. **性能风险:**
   - 安全增强可能增加系统开销
   - 加密处理可能影响响应速度

3. **测试风险:**
   - 测试覆盖可能不够全面
   - 可能存在未发现的安全漏洞

### 7.2 风险缓解措施

1. **兼容性措施:**
   - 渐进式实施
   - 保留备份方案

2. **性能措施:**
   - 优化代码实现
   - 添加缓存机制

3. **测试措施:**
   - 增加测试覆盖
   - 进行安全审计

## 8. 结论

本设计文档提出了一系列系统改进方案，重点关注安全性和可靠性。通过实施这些改进，可以显著提升系统的安全性，防止常见的安全漏洞，同时优化系统性能和用户体验。

建议按照实施步骤逐步进行，确保每个改进都经过充分测试，以避免影响现有系统的正常运行。