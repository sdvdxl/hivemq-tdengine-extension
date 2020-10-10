# HiveMQ 4 TDengine Extension

[中文说明](README_zh.md)

## Function
  
- [x] Support TDengine jdbc
- [x] Support TDengine http
- [x] Support the encoding of the payload stored in the configuration (original string, base64 string, HEX string, can be extended)
- [x] Support custom database name and table name, and automatically create database and table
- [x] Support extension hot load and stop
- [x] Support asynchronous storage, does not affect the original mqtt message processing
- [x] Support custom queue size and thread size
- [x] Support super table, table name strategy (client_id, username)

## Support version

- HiveMQ v4
- TDengine 2.0

The current test version of this extension:

- HiveMQ 4.4.1
- TDengine Server 2.0.4.0
- TDengine JDBC driver 2.0.7

## Instructions for use

1. This extension depends on JDK version 11+, minimum 11 (HiveMQ dependency)
1. This extension depends on Maven
1. [HiveMQ install and start](https://www.hivemq.com/docs/hivemq/4.3/user-guide/install-hivemq.html) , support Linux, Mac, Windows and Docker
1. [TDengine install and start](https://www.taosdata.com/cn/getting-started/) , support Linux, Windows and Docker, recommend Linux or Linux Docker version
1. Enter the project root directory, execute `mvn package -Dmaven.test.skip=true`, when you see `BUILD SUCCESS`, it means that the compilation and packaging is successful, and the packaged file directory `Building zip:xxx' will be printed on it `
1. Unzip the file, copy the file `config-example.yml` and rename it to `config.yml`, select the way to connect to TDengine (http or JDBC) according to your needs, and select the encoding according to the actual MQTT transfer encoding (RAW , BASE64 or HEX), modify and configure the corresponding database address and other information
1. Copy the folder to the ʻextensions` directory of the HiveMQ installation directory. Under normal circumstances, you should see the output ʻExtension "TD Engine Data Transfer" version 1.0.0 started successfully.` in the HiveMQ log, indicating the startup success
1. If you want to stop the plugin, create an empty file named `DISABLED` in the plugin folder; if you need to enable the plugin, delete the `DISABLED` folder

## Configuration

The configuration file is `config.yml`, the basic format:

```yaml
# Message storage thread pool configuration
threadPool:
  core: 4
  max: 64
  queue: 5000
# mode: JDBC, HTTP
# JDBC uses jdbc to write, temporarily only supports Linux and Windows (depending on the official JAVA driver), you need to pay attention to the local library file version used, fill in "jdbc:TAOS://host:6030/" in the url
# HTTP Use http to write, cross-platform, fill in url http://host:6041/rest/sql
mode: JDBC
url: "jdbc:TAOS://localhost:6030/"
# username
username: root
# Password
password: taosdata
# Database
database: mqtt
# Table information
table:
  # Super table name
  name:'mqtt_payload'
  # if table mode is SUPER_TABLE, tableNameMode is required
  # The available modes are: USERNAME, CLIENT_ID
  # USERNAME use username as the table name
  # CLIENT_ID use client_id as the table name
  use: USERNAME
  # Table name formatting
  # FIXED Replace non-letters or numbers in username or client_id with'_'
  # MD5 uses username or client_id md5 as the table name
  # Note that the final table name is super table name +'_' + formatted table name
  format: FIXED

# mqtt Message body storage code
# RAW directly toString
# BASE64 base64 encoding
# HEX hex code
payloadCoder: BASE64
# Maximum connection/read timeout time (milliseconds)
timeout: 10000
# Maximum number of connections in the connection pool
maxConnections: 10
```

## Table Structure

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

## Architecture

### Overall architecture diagram

![Architecture diagram](https://public-links.todu.top/1602317333.png?imageMogr2/thumbnail/!100p)

### Sequence diagram

![Sequence diagram](https://public-links.todu.top/1602317262.png?imageMogr2/thumbnail/!100p)
)

## Testing

**2 cores 4G; storage core 4, maximum 16, queue 1000.**

### JDBC

#### 20 publishers, each with 100 messages, a total of 2000 messages

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

#### 20 publishers, each with 500 messages, a total of 10,000 messages

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

### HTTP

#### 20 publishers, each with 100 messages, a total of 2000 messages

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

#### 20 publishers, each with 500 messages, a total of 10,000 messages

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

**Increasing the number of optimized threads and queue size will improve performance**

## Precautions

1. The jdbc mode does not support hivemq to reload the plug-in (duplication of dynamic library). If the jdbc mode is enabled and the configuration or plug-in version is changed, the hivemq server needs to be restarted
1. Users need to determine whether to use username or client_id for the table name, because the design of each platform is different, such as [Tencent Cloud](https://cloud.tencent.com/document/product/634/32546#mqtt-.E5.8D.8F.E8.AE.AE.E8.AF.B4.E6.98.8E) Each device uses a fixed client_id, so you should use CLIENT_ID; [Aliyun](https://help.aliyun.com/document_detail/73742.html?spm=a2c4g.11186623.6.599.5c131424m9mbpK#title-s5l-k39-qti) The username of each device is fixed, so USERNAME should be used.