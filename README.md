# peanotes 豌豆笔记

## 开发动机(Motivation)
看了网络上这个小伙子做的叫做[blossom](https://www.wangyunf.com/blossom/)笔记和博客，非常漂亮，由于自己平时也经常喜欢写blog，
就想自己开发一个属于自己的笔记，博客的软件。

有几点原因导致想自己开发。
1. blossom是基于spring进行开发，spring开发到现在，越来越庞大，学习起来越来越困难。
2. 没有采用前后端分离的设计。
3. 缺少单元测试。

本着java开发如果离开了spring就不行了么这个理念，就想着试着参考/抄袭blossom的代码和设计，自己用一些轻量级的框架来重新实现，也算是对java生态的一个再学习的过程。

## 重新开发的一些理念
1. 所有模块均可以单独进行测试
2. 设计上尽量简单，满足设计的功能，不需要过度设计

## FrontEnd框架
这里开发最好是以移动平台优先的原则，决定采用flutter统一前端的的开发。
- [fultter](https://docs.flutter.dev/)
- [flutter github](https://github.com/flutter/flutter)

## Backend框架以及平台选择
- [JAVA运行环境 升级为最新的JDK21](https://openjdk.org/projects/jdk/21/)
- [数据库 PostgreSQL](https://www.postgresql.org/)
- [日志框架logback](https://logback.qos.ch/manual/introduction.html)
- [基于netty的轻量级Http Server MuServer](https://muserver.io/)
- [本地缓存 caffeine](https://github.com/ben-manes/caffeine)
- [程序配置Typesafe config](https://github.com/lightbend/config)
- [使用自动JDK11自带的HttpClient](https://openjdk.org/groups/net/httpclient/intro.html)
- [WEB后端 mu server 基于netty的轻量级的Http Server](https://muserver.io/)
- [增加swagger UI](https://swagger.io/tools/swagger-ui/)
- [数据库连接池HikariCP](https://github.com/brettwooldridge/HikariCP)
- [数据库版本管理flyway](https://flywaydb.org/)
- [数据库访问 Jdbi](https://jdbi.org/)
- [json序列化FastXML jackson](https://github.com/FasterXML/jackson)
- [依赖注入 DI Guice ](https://github.com/google/guice)
- [增加Junit 5单元测试](https://junit.org/junit5/)
- [非常轻量级网关 mu-cranker]()

## Backend与安全相关的依赖
- [JWT 库 jjwt](https://github.com/jwtk/jjwt)
- [轻量级权限控制模块 Sa-Token](https://github.com/dromara/Sa-Token)

## 自动化测试
- [karate](https://karatelabs.github.io/karate/karate-core/)

## Reference Page for examples
- [typesafe config examples](https://github.com/lightbend/config/tree/main/examples)
- [junit5 samples](https://github.com/junit-team/junit5-samples/blob/fd1688afcd92d3b42b2dd45b19b1e51dfaf86d5a/junit5-jupiter-starter-maven/pom.xml)
- [assertJ quick start](https://assertj.github.io/doc/#assertj-core-quick-start)
- [logback examples](https://github.com/qos-ch/logback/tree/master/logback-examples)
- [jdbi getting started](https://jdbi.org/#_getting_started)
- [guice demos](https://github.com/google/guice/tree/master/examples/guice-demo)
- [picocli](https://picocli.info/)

## JWT
- [java-json-web-tokens-jjwt](https://www.baeldung.com/java-json-web-tokens-jjwt)

## JAVA17 
- [what's new in java 17](https://mkyong.com/java/what-is-new-in-java-17/)

## Base64
- [Java Base64 Encoding and Decoding](https://www.baeldung.com/java-base64-encode-and-decode)

## Jackson 接口反序列化
- [Interface Deserialising](https://andrewtarry.com/posts/deserialising-an-interface-with-jackson/)
