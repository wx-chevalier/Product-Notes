<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [SpringMVC Boilerplate](#springmvc-boilerplate)
  - [Features](#features)
- [Quick Start](#quick-start)
  - [Run](#run)
  - [Project Structure](#project-structure)
  - [Mybatis Generator:自动生成Model](#mybatis-generator%E8%87%AA%E5%8A%A8%E7%94%9F%E6%88%90model)
  - [Flyway:DataBase Migration](#flywaydatabase-migration)
- [Data Format](#data-format)
- [Coding Style](#coding-style)
  - [Controller](#controller)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# SpringMVC Boilerplate
Implementation of Reactive Abstract Resource Flow Architecture Style In Java With Spring

## Features
- Hessian WebService Example

# Quick Start
## Run

- gradle bootRun :运行开发环境

- gradle prod bootRun :运行生产环境

- gradle deploy bootRun :运行部署环境

- gradle bootRepackage :打包成Jar包可以独立运行/放置在Jetty下运行

## Project Structure
在一个正常的MVC项目中,我们会包含Controller,Service与Model这几个部分.传统而言我们会在一个Java项目中的不同的包内进行代码分割，而在SpringMVC-Boilerplate中，我们是利用Gradle的Multiple Projects机制来在项目级别进行代码分割的，这样的好处有：
（1）在团队协作中，能够更好地分割任务，同时避免了因为其他模块错误而带来的本部分的开发进度受阻。同时在微服务概念盛行的今天，能够尽可能地减少CodeBase的大小，避免大量冗余的重复代码。

（2）在单元测试中，可以以更清晰的粒度进行单元测试。

（3）发布或者持续集成的时候，如果某个子模块发生错误主模块可以选择使用该子模块的最近一次编译成功的Jar包作为依赖，尽可能地减少局部错误对整体发布带来的影响。



```
--rootProject：根项目
	-- api：存放接口，即传统的Controller层
	-- service：存放服务于模型层
	-- toolkits：公共的类库
	-- external：公共的外部服务库，譬如微信公众号、七牛等
	-- configuration：公共的配置文件库，考虑到api与service在进行单元测试的时候都需要用到这个配置文件
	-- doc：文档以及接口等使用说明
```



## Mybatis Generator:自动生成Model
## Router Generator:自动生成路由
## Flyway:DataBase Migration

# Request Style
## Restful
## Authentic
验证数据体放在user_token键内,

## Data Format


(1)数据格式
时间格式:所有时间格式以Unix时间戳进行交换,为Long型整数,切记不可有小数点.
(2)用户认证信息

# Coding Style
## Controller
```java
@RestController
public class CustomController extends BasicController{

    @RequestMapping("/{pathVariable}")
    public String handler(
        String user_id, //在切面中完成用户认证与替换,不一定存在
        @PathVariable("pathVariable") String pathVariable,
        String action,
        JSONObject requestData, //请求数据,在切面中完成注入,必有数据
        HttpServletResponse response //由Spring自动完成注入
    ){

    }
}
```
## Service

Service层命名时以`*Service`方式命名,具体的方法还是包含五类:

- get*:获取数据
    - get*ById:根据ID获取某些数据
    - get*ByName:根据名称获取某些数据
- post*:创建某个数据
- put*:更新某个数据
- delete*:删除某个数据
- do*:执行某个复杂操作

## Model
Model层建议尽量用Mybatis Generator自动生成,命名方式主体还是脱胎于SQL语句:

- select*:查询数据
    - select*WhereIdEqual
    - select*whereNameIn
- insert*:插入数据
- update*:更新数据
- delete*:删除数据