# RARF-Java
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

- gradle rePackage