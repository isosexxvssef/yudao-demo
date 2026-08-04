# 门店管理模块（yudao-module-store）

基于芋道（yudao）框架开发的门店管理示例模块，演示多租户隔离、CRUD、Excel 导入导出、定时数据清洗等完整功能。

---

## 一、项目结构

```
yudao-store-demo/
├── yudao-module-store/              # 门店业务模块
│   └── src/main/java/.../store/
│       ├── controller/admin/
│       │   ├── store/                # 门店 CRUD + Excel 导入导出
│       │   └── clean/                # 数据清洗手动触发 + 日志查询
│       ├── service/
│       │   ├── store/                # 门店业务逻辑（含多租户校验）
│       │   └── clean/                # 清洗逻辑（跨租户）
│       ├── dal/                      # 数据访问层（DO / Mapper）
│       ├── job/                      # 定时任务（@Scheduled）
│       └── enums/                    # 字典常量、错误码
├── yudao-server/                     # 启动模块，端口 48080
├── yudao-framework/                  # 框架层（多租户、安全、Web 等）
└── sql/postgresql/                   # 数据库脚本
    ├── ruoyi-vue-pro.sql             # 芋道基础表
    ├── quartz.sql                    # Quartz 表
    └── store_demo_setup.sql          # 门店功能初始化脚本
```

---

## 二、环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 1.8+ | 后端运行环境 |
| Maven | 3.6+ | 后端构建 |
| Node.js | 16+ | 前端构建 |
| PostgreSQL | 12+ | 数据库 |
| Redis | 5+ | 缓存（权限、字典等） |

---

## 三、PostgreSQL 配置

### 1. 创建数据库

使用 `psql` 或 pgAdmin 创建数据库 `yudao_store`：

```sql
CREATE DATABASE yudao_store WITH ENCODING 'UTF8';
```

### 2. 执行初始化脚本

按顺序执行以下脚本（使用 `psql`）：

```powershell
# 设置密码环境变量
$env:PGPASSWORD="123456"

# PostgreSQL 安装路径下的 psql.exe
$psql = "D:\PostgreSQL\18\bin\psql.exe"

# 1. 芋道基础表
& $psql -h 127.0.0.1 -U postgres -d yudao_store -f "sql\postgresql\ruoyi-vue-pro.sql"

# 2. Quartz 定时任务表
& $psql -h 127.0.0.1 -U postgres -d yudao_store -f "sql\postgresql\quartz.sql"

# 3. 门店功能初始化脚本（门店表、字典、菜单、租户、权限）
& $psql -h 127.0.0.1 -U postgres -d yudao_store -f "sql\postgresql\store_demo_setup.sql"
```

### 3. 后端数据源配置

数据源配置位于 [application-local.yaml](yudao-server/src/main/resources/application-local.yaml)：

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          url: jdbc:postgresql://127.0.0.1:5432/yudao_store?useSSL=false&serverTimezone=Asia/Shanghai
          username: postgres
          password: 123456   # 请按实际密码修改
```

### 4. Redis 配置

默认连接本地 Redis（无密码），如需修改请编辑 [application-local.yaml](yudao-server/src/main/resources/application-local.yaml) 中的 `spring.redis` 配置。

---

## 四、项目启动

### 1. 启动后端服务

#### 方式 A：IDEA 启动（推荐开发）
1. 使用 IDEA 打开 `yudao-store-demo` 根目录
2. 等待 Maven 自动下载依赖
3. 运行启动类 `cn.iocoder.yudao.server.YudaoServerApplication`
4. 启动成功后访问 `http://localhost:48080`

#### 方式 B：命令行启动
```powershell
cd yudao-store-demo
mvn clean install -DskipTests
java -jar yudao-server\target\yudao-server.jar --spring.profiles.active=local
```

### 2. 启动前端服务

```powershell
cd yudao-ui\yudao-ui-admin-vue3
npm install
npm run dev
```

启动成功后访问 `http://localhost:80`

---

## 五、测试账号

### 默认超级管理员（租户 1）

| 项 | 值 |
|----|----|
| 租户名称 | `芋道源码` |
| 账号 | `admin` |
| 密码 | `admin123` |

### 测试租户（用于多租户隔离测试）

| 租户 | 租户名称（登录时填这个） | 租户ID | 账号 | 密码 |
|------|------------------------|--------|------|------|
| 租户A | `租户A` | 123 | `admin` | `admin123` |
| 租户B | `租户B` | 124 | `admin` | `admin123` |

> **登录页说明**：登录页的「租户」输入框填写的是**租户名称**（中文名），不是数字 ID。系统会自动按名称查找租户 ID。

### 多租户隔离验证
- 租户A 只能看到自己创建的门店数据
- 租户B 只能看到自己创建的门店数据
- 同一门店编码可在不同租户内重复，但同一租户内唯一
- 前端无法通过传入 `tenantId` 访问其他租户数据（`StoreSaveReqVO` 无该字段，由 `TenantBaseDO` 自动注入）

---

## 六、Excel 导入

### 1. 下载导入模板
1. 登录系统，进入「门店管理」→「门店列表」
2. 点击「导入」按钮旁的「下载模板」
3. 获得模板文件 `门店导入模板.xls`

### 2. Excel 字段

| 门店编码 | 门店名称 | 平台 | 城市 | 负责人 | 联系电话 | 营业状态 |
|---------|---------|------|------|--------|---------|---------|
| A101 | 门店A101 | 美团 | 上海 | 李四 | 13800000001 | 营业 |

> 营业状态可选值：`营业` / `停业`（或使用字典值 `1` / `2`）

### 3. 执行导入
1. 在门店列表页点击「导入」按钮
2. 选择准备好的 Excel 文件
3. 「是否更新已经存在的门店数据」默认为否
4. 点击「确定」上传
5. 查看导入结果：成功数量、失败原因

### 4. 导入校验规则
- 门店编码不能为空
- 门店名称不能为空
- 同一租户内门店编码不能重复
- 导入数据自动归属当前登录租户

---

## 七、Excel 导出

1. 在门店列表页点击「导出」按钮
2. 浏览器下载 `门店.xls`
3. 导出内容仅包含当前租户数据，中文不乱码

---

## 八、定时数据清洗

### 1. 清洗规则

| 字段 | 规则 |
|------|------|
| 门店名称 | 去掉前后空格 |
| 负责人 | 去掉前后空格 |
| 联系电话 | 去掉空格和短横线 |
| 平台 | `meituan` / `美 团` → `美团`<br>`eleme` / `饿了么平台` → `饿了么`<br>`jd` / `京东到家` → `京东` |

### 2. 定时触发（自动）

定时任务配置在 [StoreCleanJob.java](yudao-module-store/src/main/java/cn/iocoder/yudao/module/store/job/StoreCleanJob.java)：

```java
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨 2 点执行
public void cleanStoreData() { ... }
```

无需手动干预，到点自动运行并写入清洗日志。

### 3. 手动触发

- **前端入口**：进入「门店管理」→「数据清洗日志」，点击「触发清洗」按钮
- **API 接口**：`POST /admin-api/store/clean/trigger?triggerType=2`

### 4. 清洗日志字段

每次清洗后写入一条日志，记录：

| 字段 | 说明 |
|------|------|
| 开始时间 | 清洗开始时间 |
| 结束时间 | 清洗结束时间 |
| 扫描数量 | 扫描的门店总数 |
| 修改数量 | 实际修改的门店数 |
| 执行状态 | 0=成功，1=失败 |
| 错误信息 | 失败时的异常信息 |
| 触发方式 | 1=定时任务，2=手动触发 |

---

## 九、已完成功能清单

### 多租户隔离
- [x] 基于 `TenantBaseDO` 实现门店数据自动按租户隔离
- [x] 新增/查询/修改/删除/导入/导出全部按租户隔离
- [x] 门店编码租户内唯一约束
- [x] 前端无法传入任意 `tenantId` 越权访问
- [x] 准备两个测试租户（租户A / 租户B）

### 门店 CRUD
- [x] 门店列表（分页查询）
- [x] 按门店名称搜索
- [x] 新增门店
- [x] 编辑门店
- [x] 删除门店（单条 + 批量）

### Excel 导入
- [x] 导入模板下载
- [x] 文件上传与解析
- [x] 校验：编码非空、名称非空、租户内编码唯一
- [x] 返回成功/失败明细
- [x] 导入数据归属当前租户

### Excel 导出
- [x] 导出当前租户门店数据
- [x] Excel 可正常打开，中文不乱码

### 定时数据清洗
- [x] `@Scheduled` 定时任务（每天凌晨 2 点）
- [x] 跨租户清洗（使用 `TenantUtils.execute` 遍历所有租户）
- [x] 手动触发接口
- [x] 清洗日志记录（开始/结束时间、扫描数、修改数、状态、错误信息）

### 前端页面
- [x] 门店列表页（含搜索、分页、状态字典显示）
- [x] 门店新增/编辑表单
- [x] 门店删除（单条 + 批量）
- [x] Excel 导入弹窗
- [x] Excel 导出按钮
- [x] 数据清洗日志页

### 权限配置
- [x] 菜单与按钮权限（`store:store:query/create/update/delete/import/export`）
- [x] 清洗日志权限（`store:clean:trigger`、`store:clean:log:query`）
- [x] 租户套餐已分配门店菜单
- [x] 测试租户角色已分配门店菜单

---

## 十、测试说明

详细的测试方案、测试用例、测试数据准备、测试结果登记模板，请参见独立文档：

👉 **[TEST.md](yudao-store-demo/TEST.md)**



## 十一、AI 工具使用情况

本项目采用 Kimi 与 Trae协同开发：
Kimi 负责整体流程梳理与方案确认，Trae 提供代码辅助与工程实现。

| 环节 | AI 辅助内容 |
|------|------------|
| 代码生成 | 生成门店模块的后端 Controller / Service / Mapper / DO / VO 全套代码 |
| 代码生成 | 生成前端 API 接口、Vue3 页面（列表、表单、导入弹窗） |
| SQL 编写 | 生成 PostgreSQL 建表脚本、字典数据、菜单权限、租户初始化脚本 |
| 问题排查 | 定位 API 404 问题（控制器路径前缀重复） |
| 问题排查 | 定位权限 403 问题（租户套餐菜单缺失、role_menu 租户隔离） |
| 问题排查 | 定位 PostgreSQL 序列缺失导致的数据插入失败 |
| 测试验证 | 通过 PowerShell 调用 API 验证多租户隔离、Excel 导入导出、清洗功能 |
| 文档编写 | 生成本 README 文档 |

**说明**：AI 生成的代码均经过人工验证与调整，所有功能均通过实际接口测试和前端操作验证。
