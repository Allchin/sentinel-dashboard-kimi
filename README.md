# Sentinel 控制台

## 0. 概述

Sentinel 控制台是流量控制、熔断降级规则统一配置和管理的入口，它为用户提供了机器自发现、簇点链路自发现、监控、规则配置等功能。在 Sentinel 控制台上，我们可以配置规则并实时查看流量控制效果。

## 1. 编译和启动

### 1.1 如何编译

使用如下命令将代码打包成一个 fat jar:

```bash
mvn clean package
```

### 1.2 如何启动

使用如下命令启动编译后的控制台：

```bash
java -Dserver.port=8080 \
-Dcsp.sentinel.dashboard.server=localhost:8080 \
-Dproject.name=sentinel-dashboard \
-jar target/sentinel-dashboard.jar
```

上述命令中我们指定几个 JVM 参数，其中 `-Dserver.port=8080` 是 Spring Boot 的参数，
用于指定 Spring Boot 服务端启动端口为 `8080`。其余几个是 Sentinel 客户端的参数。

为便于演示，我们对控制台本身加入了流量控制功能，具体做法是引入 Sentinel 提供的 `CommonFilter` 这个 Servlet Filter。
上述 JVM 参数的含义是：

| 参数 | 作用 |
|--------|--------|
|`Dcsp.sentinel.dashboard.server=localhost:8080`|向 Sentinel 接入端指定控制台的地址|
|`-Dproject.name=sentinel-dashboard`|向 Sentinel 指定应用名称，比如上面对应的应用名称就为 `sentinel-dashboard`|

全部的配置项可以参考 [启动配置项文档](https://github.com/alibaba/Sentinel/wiki/%E5%90%AF%E5%8A%A8%E9%85%8D%E7%BD%AE%E9%A1%B9)。

经过上述配置，控制台启动后会自动向自己发送心跳。程序启动后浏览器访问 `localhost:8080` 即可访问 Sentinel 控制台。

从 Sentinel 1.6.0 开始，Sentinel 控制台支持简单的**登录**功能，默认用户名和密码都是 `sentinel`。用户可以通过如下参数进行配置：

- `-Dsentinel.dashboard.auth.username=sentinel` 用于指定控制台的登录用户名为 `sentinel`；
- `-Dsentinel.dashboard.auth.password=123456` 用于指定控制台的登录密码为 `123456`；如果省略这两个参数，默认用户和密码均为 `sentinel`；
- `-Dserver.servlet.session.timeout=7200` 用于指定 Spring Boot 服务端 session 的过期时间，如 `7200` 表示 7200 秒；`60m` 表示 60 分钟，默认为 30 分钟；

## 2. 客户端接入

选择合适的方式接入 Sentinel，然后在应用启动时加入 JVM 参数 `-Dcsp.sentinel.dashboard.server=consoleIp:port` 指定控制台地址和端口。
确保客户端有访问量，**Sentinel 会在客户端首次调用的时候进行初始化，开始向控制台发送心跳包**，将客户端纳入到控制台的管辖之下。

客户端接入的详细步骤请参考 [Wiki 文档](https://github.com/alibaba/Sentinel/wiki/%E6%8E%A7%E5%88%B6%E5%8F%B0#3-%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8E%A5%E5%85%A5%E6%8E%A7%E5%88%B6%E5%8F%B0)。

## 3. 验证是否接入成功

客户端正确配置并启动后，会**在初次调用后**主动向控制台发送心跳包，汇报自己的存在； 控制台收到客户端心跳包之后，会在左侧导航栏中显示该客户端信息。如果控制台能够看到客户端的机器信息，则表明客户端接入成功了。

更多：[控制台功能介绍](./Sentinel_Dashboard_Feature.md)。

## 4. 持久化到Redis(二开变更记录)

```
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/datasource/entity/rule/AuthorityRuleCorrectEntity.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/datasource/entity/rule/ParamFlowRuleCorrectEntity.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/RedisUtil.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/RuleConsts.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/authority/AuthorityRuleRedisProvider.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/authority/AuthorityRuleRedisPublisher.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/cluster/ClusterMapDataRedisPublisher.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/degrade/DegradeRuleRedisProvider.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/degrade/DegradeRuleRedisPublisher.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/flow/FlowRuleRedisProvider.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/flow/FlowRuleRedisPublisher.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/param/ParamFlowRuleRedisProvider.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/param/ParamFlowRuleRedisPublisher.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/system/SystemRuleRedisProvider.java
new file:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/system/SystemRuleRedisPublisher.java
modified:   sentinel-dashboard/pom.xml
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/controller/AuthorityRuleController.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/controller/DegradeController.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/controller/FlowControllerV1.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/controller/ParamFlowRuleController.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/controller/SystemController.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/datasource/entity/rule/AuthorityRuleCorrectEntity.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/datasource/entity/rule/ParamFlowRuleCorrectEntity.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/datasource/entity/rule/ParamFlowRuleEntity.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/RedisUtil.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/RuleConsts.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/authority/AuthorityRuleRedisProvider.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/authority/AuthorityRuleRedisPublisher.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/cluster/ClusterMapDataRedisPublisher.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/degrade/DegradeRuleRedisProvider.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/degrade/DegradeRuleRedisPublisher.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/flow/FlowRuleRedisProvider.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/flow/FlowRuleRedisPublisher.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/param/ParamFlowRuleRedisProvider.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/param/ParamFlowRuleRedisPublisher.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/system/SystemRuleRedisProvider.java
modified:   sentinel-dashboard/src/main/java/com/alibaba/csp/sentinel/dashboard/rule/redis/system/SystemRuleRedisPublisher.java
modified:   sentinel-dashboard/src/main/resources/application.properties
```

## 5. 多环境启动（对接 kimigoshop-tms）

本项目已与 kimigoshop-tms 的 Redis 规则源约定对齐：

- ruleKey：`sentinel:rules:{app}:{group}:{flow|degrade|param-flow}`
- channel：`sentinel:channel:{app}:{group}:{flow|degrade|param-flow}`（SET 后 PUBLISH 任意消息触发客户端刷新）
- 分组由 `sentinel.rule.group` 控制（默认 default，可用环境变量 `SENTINEL_RULE_GROUP` 覆盖），
  与 TMS 的 `tms.sentinel.rule.group` 保持一致。

### 环境一览（application-{env}.properties）

| 环境   | Redis                          | db | dashboard 端口 |
|--------|--------------------------------|----|----------------|
| dev    | 172.20.1.174:6379              | 1  | 8858           |
| zrxdev | 172.20.1.174:6379              | 1  | 8859           |
| test   | 81.68.223.190:32379（测试 Redis） | 1  | 8860           |
| wms    | 192.168.110.254:6379（无密码）  | 1  | 8861           |
| export | 192.168.110.254:6379（无密码）  | 1  | 8862           |
| prod   | 仅环境变量注入，不设默认值      | 1  | 8863           |

所有值均可用 `SENTINEL_RULE_REDIS_HOST / PORT / PASSWORD / DATABASE` 覆盖；
端口用 `SENTINEL_DASHBOARD_PORT` 覆盖。多个环境可同时运行（端口互不相同）。

### 启动

```bash
mvn clean package -DskipTests        # 先构建 target/sentinel-dashboard.jar

./start.sh zrxdev                    # 启动 zrxdev 环境（端口 8859）
./start.sh test                      # 启动 test 环境（端口 8860，连测试 Redis）
SENTINEL_RULE_REDIS_HOST=81.68.223.190 SENTINEL_RULE_REDIS_PORT=32379 ./start.sh dev
```

账号/密码：sentinel / sentinel（`SENTINEL_DASHBOARD_USER/PASS` 可覆盖）。

### 注意

- 仅 flow / degrade / param-flow 三类规则写入 TMS 约定的 key；
  system / authority / gateway / cluster 仍用本 fork 旧的 key（TMS 不消费）。
- Dashboard 发布的 JSON 带 dashboard 内部字段（id/app/ip/port 等），
  TMS 侧 SentinelRuleDataSourceConfig 的 ObjectMapper 已配置忽略未知字段。
- 若 TMS 启动时用环境变量把规则 Redis 指向了测试 Redis，
  dashboard 需同步用 `SENTINEL_RULE_REDIS_*` 指向同一实例（或直接用 test profile）。