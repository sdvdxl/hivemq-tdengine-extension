# HiveMQ 4 TDengine Extension

## 功能
  
- [x] 支持使用jdbc方式将 mqtt 消息写入 TDengine
- [x] 支持使用http RESTful 方式将 mqtt 消息写入 TDengine
- [x] 支持配置存储的payload的编码（原始字符串、base64字符串、HEX 字符串，可以扩展）
- [x] 支持自定义数据库名字和表名字，并且自动创建库和表
- [x] 支持hivemq热加载、停止
- [x] 支持同时启用http和jdbc方式写入不同的表
- [x] 支持异步入库
- [x] 支持自定义入库队列大小，线程大小
- [x] 支持超级表，表名策略（client_id、username）

## 支持版本

- HiveMQ v4
- TDengine 2.0

本插件当前测试版本：

- HiveMQ 4.4.1
- TDengine Server 2.0.4.0
- TDengine JDBC 驱动 2.0.7

## 使用说明

1. 本插件依赖JDK版本11+，最低11（HiveMQ依赖），国内可以[从这里下载](https://www.injdk.cn/)
1. 本插件依赖Maven，国内可以[从这里下载](https://mirrors.huaweicloud.com/apache/maven/maven-3/3.6.3/binaries/)
1. [HiveMQ安装并启动](https://www.hivemq.com/docs/hivemq/4.3/user-guide/install-hivemq.html)，支持Linux，Mac、Windows和Docker
1. [TDengine安装并启动](https://www.taosdata.com/cn/getting-started/)，支持Linux、Windows和Docker，推荐Linux或者Linux Docker版本 
1. 进入项目根目录，执行 `mvn package -Dmaven.test.skip=true`，当看到 `BUILD SUCCESS`，说明编译并打包成功， 在其上面会打印出打包的文件目录 `Building zip:xxx`
1. 解压该文件，拷贝 文件 `config-example.yml` 并重命名为 `config.yml`，根据需要选择要是用的连接TDengine的方式（http或者JDBC），并根据MQTT实际传输编码选择编码（RAW、BASE64或者HEX），修改并配置相应的数据库地址等信息
1. 将该文件夹拷到到 HiveMQ安装目录的 `extensions` 目录，正常的情况下应该看到HiveMQ的日志中有输出 `Extension "TD Engine Data Transfer" version 1.0.0 started successfully.` ，说明启动成功
1. 如果要停止插件运行，在该插件文件夹创建一个名字为 `DISABLED` 的空文件即可；如果需要启用插件，删除 `DISABLED` 文件夹即可

## 配置

配置文件为 `config.yml`，基本格式：

```yaml
# 消息存储线程池配置
threadPool:
  core: 4
  max: 64
  queue: 5000
# mode: JDBC, HTTP
# JDBC 使用 jdbc 方式写入，暂时仅支持Linux和Windows（依赖于官方JAVA driver）需要注意使用的本地库文件版本，url填写 "jdbc:TAOS://host:6030/"
# HTTP 使用 http 方式写入，跨平台，url 填写 http://host:6041/rest/sql
mode: JDBC
url: "jdbc:TAOS://localhost:6030/"
# 用户名
username: root
# 密码
password: taosdata
# 数据库
database: mqtt
# 表信息
table:
  # 超级表名字
  name: 'mqtt_payload'
  # if table mode is SUPER_TABLE, tableNameMode is required
  # The available modes are: USERNAME, CLIENT_ID
  # USERNAME 使用username作为表名
  # CLIENT_ID 使用 client_id 作为表名
  use: USERNAME
  # 表名格式化方式
  # FIXED 替换所使用的username或者client_id中非字母或者数字为'_'
  # MD5 将 username或者client_id md5后作为表名
  # 注意最终表名是 超级表名字+'_'+格式化表名
  format: FIXED

# mqtt 消息体存储编码
# RAW 直接toString
# BASE64 base64 编码
# HEX hex 编码
payloadCoder: BASE64
# 最大连接/读取超时时间（毫秒）
timeout: 10000
# 连接池最大连接数
maxConnections: 10
```

## 表结构

```
taos> describe mqtt_payload ;
             Field              |        Type        |   Length    |    Note    |
=================================================================================
 ts                             | TIMESTAMP          |           8 |            |
 payload                        | NCHAR              |        1024 |            |
 client_id                      | NCHAR              |        1024 | TAG        |
 topic                          | NCHAR              |        1024 | TAG        |
 qos                            | TINYINT            |           1 | TAG        |
 ip                             | NCHAR              |         512 | TAG        |
```

## 架构

### 整体架构图 

![架构图](https://public-links.todu.top/hivemq-tdengine-extension.jpg?imageMogr2/thumbnail/!100p)

### 时序图

![时序图](https://public-links.todu.top/hivemq-tdengine-extension-seq.jpg?imageMogr2/thumbnail/!100p)

## 测试

**2核4G；存储核心现程4，最大16，队列1000。**

### JDBC

#### 20个publisher，每个100个消息，一共2000个消息

`bin/mqttloader -b tcp://127.0.0.1:1883 -p 20 -m 100`

```
Measurement started: 2020-10-02 13:50:09.701 CST
Measurement ended: 2020-10-02 13:50:19.942 CST

-----Publisher-----
Maximum throughput[msg/s]: 2000
Average throughput[msg/s]: 2000.00
Number of published messages: 2000
Per second throughput[msg/s]: 2000

-----Subscriber-----
Maximum throughput[msg/s]: 1070
Average throughput[msg/s]: 500.00
Number of received messages: 2000
Per second throughput[msg/s]: 505, 410, 1070, 15
Maximum latency[ms]: 2847
Average latency[ms]: 1612.60
```

#### 20个publisher，每个500个消息，一共10000个消息

`bin/mqttloader -b tcp://127.0.0.1:1883 -p 20 -m 500`

```
Measurement started: 2020-10-02 13:52:34.530 CST
Measurement ended: 2020-10-02 13:52:49.761 CST

-----Publisher-----
Maximum throughput[msg/s]: 9889
Average throughput[msg/s]: 5000.00
Number of published messages: 10000
Per second throughput[msg/s]: 9889, 111

-----Subscriber-----
Maximum throughput[msg/s]: 2498
Average throughput[msg/s]: 1111.11
Number of received messages: 10000
Per second throughput[msg/s]: 111, 1750, 2145, 2259, 2498, 1156, 48, 0, 33
Maximum latency[ms]: 7136
Average latency[ms]: 2811.72
```

### REST

#### 20个publisher，每个100个消息，一共2000个消息

`bin/mqttloader -b tcp://127.0.0.1:1883 -p 20 -m 100`

```
Measurement started: 2020-10-02 14:10:14.697 CST
Measurement ended: 2020-10-02 14:10:23.978 CST

-----Publisher-----
Maximum throughput[msg/s]: 2000
Average throughput[msg/s]: 2000.00
Number of published messages: 2000
Per second throughput[msg/s]: 2000

-----Subscriber-----
Maximum throughput[msg/s]: 1213
Average throughput[msg/s]: 666.67
Number of received messages: 2000
Per second throughput[msg/s]: 1213, 782, 5
Maximum latency[ms]: 1767
Average latency[ms]: 611.88
```

#### 20个publisher，每个500个消息，一共10000个消息

```
Measurement started: 2020-10-02 14:17:27.436 CST
Measurement ended: 2020-10-02 14:17:40.148 CST

-----Publisher-----
Maximum throughput[msg/s]: 9475
Average throughput[msg/s]: 5000.00
Number of published messages: 10000
Per second throughput[msg/s]: 9475, 525

-----Subscriber-----
Maximum throughput[msg/s]: 4234
Average throughput[msg/s]: 1666.67
Number of received messages: 10000
Per second throughput[msg/s]: 632, 3264, 1210, 4234, 597, 63
Maximum latency[ms]: 4676
Average latency[ms]: 1900.11
```

**增加优化线程数量和队列大小会提升性能**

## 注意事项

1. jdbc方式不支持hivemq 重新热加载插件（动态库重复问题），如果启用了jdbc方式并且更改了配置或者插件版本，需要重启hivemq server
1. 用户更具需要确定表名使用username还是client_id，因为每个平台的设计不一样，比如 [腾讯云](https://cloud.tencent.com/document/product/634/32546#mqtt-.E5.8D.8F.E8.AE.AE.E8.AF.B4.E6.98.8E) 每个设备使用固定client_id，所以应该使用 use应该使用CLIENT_ID; [阿里云](https://help.aliyun.com/document_detail/73742.html?spm=a2c4g.11186623.6.599.5c131424m9mbpK#title-s5l-k39-qti) 每个设备的username是固定的，应该使用USERNAME。
