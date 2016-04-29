- [RARF-Java](#rarf-java)
  - [Features](#features)
- [Run](#run)
  - [Maven](#maven)
  - [Gradle](#gradle)

# SpringMVC Boilerplate
Implementation of Reactive Abstract Resource Flow Architecture Style In Java With Spring

## Features
- Hessian WebService Example

# Run
## Maven
- mvn install : 安装基本的Maven依赖项

- mvn spring-boot:run : 从wx.application.Application开始运行服务器

- mvn package -Ppro : 按照开发环境打包成jar文件

- mvn package -Pdeploy : 按照部署环境打包成war文件

## Gradle
- gradle bootRun:运行开发环境

- gradle prod bootRun:运行生产环境

- gradle deploy bootRun:运行部署环境

- gradle bootRepackage:打包成Jar包可以独立运行/放置在Jetty下运行

# 前后端数据交换原则
(1)数据格式
时间格式:所有时间格式以Unix时间戳进行交换,为Long型整数,切记不可有小数点.
(2)用户认证信息