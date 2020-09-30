# HiveMQ 4 TDengine Extension
  

## 功能
  
- 支持使用jdbc方式将 mqtt 消息写入 TDengine
- 支持使用http RESTful 方式将 mqtt 消息写入 TDengine
- 支持配置存储的payload的编码（原始字符串、base64字符串、HEX 字符串，可以扩展）
- 支持自定义数据库名字和表名字，并且自动创建库和表
- 支持hivemq热加载、停止
- 支持同时启用http和jdbc方式写入不同的表并且配置不通编码或者其中之一

## 支持版本

- HiveMQ v4
- TDengine 2.0

本插件当前测试版本：

- HiveMQ 4.4.1
- TDengine Server 2.0.0.0 (docker)
- TDengine JDBC 驱动 2.0.0

## 使用说明

1. 本插件依赖JDK版本11+，最低11（HiveMQ依赖），国内可以[从这里下载](https://www.injdk.cn/)
1. 本插件依赖Maven，国内可以[从这里下载](https://mirrors.huaweicloud.com/apache/maven/maven-3/3.6.3/binaries/)
1. [HiveMQ安装并启动](https://www.hivemq.com/docs/hivemq/4.3/user-guide/install-hivemq.html)，支持Linux，Mac、Windows和Docker
1. [TDengine安装并启动](https://www.taosdata.com/cn/getting-started/)，支持Linux、Windows和Docker，推荐Linux或者Linux Docker版本 
1. 进入项目根目录，执行 `mvn package -Dmaven.test.skip=true`，当看到 `BUILD SUCCESS`，说明编译并打包成功， 在其上面会打印出打包的文件目录 `Building zip:xxx`
1. 解压该文件，拷贝 文件 `config-example.yml` 并重命名为 `config.yml`，根据需要选择要是用的连接TDengine的方式（http或者JDBC），并根据MQTT实际传输编码选择编码（RAW、BASE64或者HEX），修改并配置相应的数据库地址等信息
1. 将该文件夹拷到到 HiveMQ安装目录的 `extensions` 目录，正常的情况下应该看到HiveMQ的日志中有输出 `Extension "TD Engine Data Transfer" version 1.0.0 started successfully.` ，说明启动成功
1. 如果要停止插件运行，在该插件文件夹创建一个名字为 `DISABLED` 的空文件即可；如果需要启用插件，删除 `DISABLED` 文件夹即可

## TODO

- [ ] 支持热加载配置文件
- [ ] 优化配置文件参数校验
  
## 不足

1. jdbc方式不支持hivemq 重新热加载插件（动态库重复问题），如果启用了jdbc方式并且更改了配置或者插件版本，需要重启hivemq server
1. 根据测试，jdbc驱动版本 2.0.4 操作 server 2.0.0.0 会报错，所以驱动需要根据server版本选择