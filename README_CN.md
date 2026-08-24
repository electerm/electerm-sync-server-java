# Java Electerm 同步服务器

[![Build Status](https://github.com/electerm/electerm-sync-server-java/actions/workflows/linux.yml/badge.svg)](https://github.com/electerm/electerm-sync-server-java/actions)

[English](README.md) | [中文](README_CN.md)

开源终端/ssh/telnet/serialport/RDP/VNC/Spice/sftp/ftp客户端(Linux, Mac, Windows, Android, HarmonyOS)。

除了主流的 Windows/macOS/Linux/Android，electerm 还支持鸿蒙(HarmonyOS)，以及老旧系统——如 Ubuntu 18、Windows 7、macOS 10+，以及国产特殊 Linux 发行版如 UOS、麒麟(Kylin)、龙芯(LoongArch，含旧世界与新世界)。

<p>
  <a href="https://electerm.org">主页 / 下载</a> ·
  <a href="https://theme.electerm.org">主题</a> ·
  <a href="https://github.com/electerm/electerm-web-docker">Docker</a> ·
  <a href="https://demo.electerm.org">在线演示</a> ·
  <a href="https://github.com/electerm/electerm-android">Android</a> ·
  <a href="https://github.com/electerm/electerm-harmony">鸿蒙</a> ·
  <a href="https://appgallery.huawei.com/app/detail?id=org.electerm.electerm">华为应用市场</a> ·
  <a href="https://www.microsoft.com/store/apps/9NCN7272GTFF">微软商店</a> ·
  <a href="https://snapcraft.io/electerm">Snap 商店</a> ·
  <a href="https://repos.electerm.org/deb">deb 仓库</a> ·
  <a href="https://repos.electerm.org/rpm">rpm 仓库</a>
</p>

<div>🌐 <strong><a href="https://cloud.electerm.org">electerm 在线版</a></strong> — 公共免费在线 electerm 应用</div>
<div>🤖 <strong><a href="https://ai.electerm.org">electerm AI</a></strong> — 免费为 electerm 用户提供 AI</div>
<div>💻 <strong><a href="https://github.com/electerm/electerm-web">electerm-web</a></strong> — 运行于浏览器(支持移动设备)的 web app 版本</div>

一个简单、轻量的 [Electerm](https://github.com/electerm/electerm) 数据同步服务器，使用 Java 和 Spark 构建。它提供 REST API 用于跨 Electerm 实例同步书签、历史记录和其他数据。

## 先决条件

- **Java 17**：构建和运行服务器所需。从 [Adoptium](https://adoptium.net/) 或您首选的 JDK 提供商下载。
- **Gradle**：通过 Gradle Wrapper（`gradlew`）包含，因此无需单独安装。
- **Git**：用于克隆仓库。

## 安装

1. **克隆仓库**：

   ```bash
   git clone https://github.com/electerm/electerm-sync-server-java.git
   cd electerm-sync-server-java
   ```

2. **设置环境文件**：

   ```bash
   cp sample.env .env
   ```

   使用您的首选设置编辑 `.env`（请参阅配置部分）。

## 配置

服务器使用 `.env` 文件中的环境变量进行配置。关键设置包括：

- `JWT_SECRET`：JWT 令牌签名的密钥（生成一个强随机字符串）。
- `JWT_USER_NAME`：用于认证的用户名。
- `JWT_USER_PASSWORD`：用于认证的密码。
- `PORT`：服务器端口（默认：7837）。
- `DB_URL`：H2 数据库 URL（默认：嵌入式数据库）。

示例 `.env`：

```properties
JWT_SECRET=your-super-secret-key-here
JWT_USER_NAME=admin
JWT_USER_PASSWORD=securepassword
PORT=7837
DB_URL=jdbc:h2:./electerm_sync_db
```

**安全注意**：切勿将 `.env` 提交到版本控制。使用强且唯一的密钥。

## 运行服务器

### 开发模式

用于快速测试或开发：

```bash
./gradlew run
```

- 在 `http://127.0.0.1:7837` 上启动服务器。
- 输出将显示：`server running at http://127.0.0.1:7837`。
- 按 `Ctrl+C` 停止。

### 生产模式

1. **构建应用程序**：

   ```bash
   ./gradlew build
   ```

2. **提取分发包**：

   ```bash
   tar -xvf build/distributions/electerm-sync-server-java.tar
   cd electerm-sync-server-java
   ```

3. **运行服务器**：

   ```bash
   ./bin/electerm-sync-server-java
   ```

   - 在前台运行。使用 `&` 在后台运行：`./bin/electerm-sync-server-java &`。
   - 在 `http://127.0.0.1:7837`（或您的配置端口）访问。

## 与 Electerm 一起使用

1. 在 Electerm 中，转到 **设置 > 同步**。
2. 选择 **自定义同步服务器**。
3. 设置：
   - **服务器 URL**：`http://127.0.0.1:7837`（或您的服务器 URL）。
   - **JWT 密钥**：来自您的 `.env` 的 `JWT_SECRET`。
   - **用户名**：来自您的 `.env` 的 `JWT_USER_NAME`。
4. 保存并同步数据。

API 端点是 `http://127.0.0.1:7837/api/sync`。

## 测试

运行测试套件：

```bash
./gradlew test
```

- 使用 JUnit 进行单元测试。
- 报告位于 `build/reports/tests/test/index.html`。

## 自定义

### 编写您自己的数据存储

服务器默认使用 H2 数据库，但您可以实现自定义存储。

1. 通过扩展或引用 `DataStore.java` 实现数据存储接口。
2. 更新 `App.java` 以使用您的自定义存储。
3. 重新构建并运行。

示例：以 [src/main/java/ElectermSync/DataStore.java](src/main/java/ElectermSync/DataStore.java) 为参考来实现读/写方法。

## Docker

对于容器化部署，请参阅 [electerm-sync-server-java-docker](https://github.com/Aliang-code/electerm-sync-server-java-docker)。

## 其他语言的同步服务器

探索替代方案：[自定义同步服务器 Wiki](https://github.com/electerm/electerm/wiki/Custom-sync-server)。

## 许可证

MIT