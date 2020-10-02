# HiveMQ 4 TDengine Extension
  

## 功能
  
- [x] 支持使用jdbc方式将 mqtt 消息写入 TDengine
- [x] 支持使用http RESTful 方式将 mqtt 消息写入 TDengine
- [x] 支持配置存储的payload的编码（原始字符串、base64字符串、HEX 字符串，可以扩展）
- [x] 支持自定义数据库名字和表名字，并且自动创建库和表
- [x] 支持hivemq热加载、停止
- [x] 支持同时启用http和jdbc方式写入不同的表并且配置不通编码或者其中之一
- [ ] 支持同步入库
- [ ] 支持自定义入库队列大小，线程大小
- [ ] 支持热加载配置文件
- [ ] 支持批量入库
- [ ] 支持批量入库大小
- [ ] 支持批量入库最大聚合等待时间
- [ ] 优化配置文件参数校验


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

**全部有效写入库中。**

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

有效写入库中： 8867 条。

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

有效写入库中： 1262 条。

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

有效写入库中： 5981 条。

有效入库：成功执行插入操作，没有抛出异常。没有写入库中是因为写入速率达不到，线程拒绝执行导致。

**增加优化线程数量和队列大小会提升性能，即有效写入数量**

## 注意事项

1. jdbc方式不支持hivemq 重新热加载插件（动态库重复问题），如果启用了jdbc方式并且更改了配置或者插件版本，需要重启hivemq server
1. 本插件是一个通用处理方案，所以使用了一张表来存储数据。因为 TDengine 设计的是时间戳一样，就会丢失后面的时间戳一样的消息，所以该插件使用的时候需要按照自己的mqtt的clientId 或者username 重新改造一下，尽量做到每个设备一个表。