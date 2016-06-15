[TOC]

# SpringMVC Boilerplate
Implementation of Reactive Abstract Resource Flow Architecture Style In Java With Spring

## Goals

- 能够应对复杂多变的业务逻辑需求，以最快的速度进行开发
- 能够构建长期运行的应用程序

## Features

- Grade:笔者最早是习惯于使用Maven作为项目构建工具，目前已经全部迁移到了Gradle上，用脚本语言来进行配置与构建会比标记语言有很大的灵活性。在Gradle的基础上，笔者进行了模块划分与一些辅助性的Task的构造。
- Code Generator:笔者在自己的实践中将一些常用的重复代码
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

为了方便更好地接口阅读性，建议是将资源的ID放置到URL中，然后将其他查询参数放置到统一的requestData中，譬如我们需要获取编号为1的书籍的信息：

```
[GET] http://api.com/book/1
```

笔者在早期是习惯性使用譬如getBookByID，对于单个接口而言，可读性貌似上去了，但是，这最终又会导致接口整体命名的混乱性。确实可以通过统一的规划定义来解决，但是笔者个人认为这并没有从接口风格本身来解决这个问题，还是依赖于项目经理或者程序猿的能力。

这里有一个小的注意点，譬如当前用户要获取自己的收藏夹信息，应该是：

```
[GET] http://api.com/favorite
```

而当前用户要查看其它人的收藏夹信息，应该是：

```
[GET] http://api.com/user/{user_id}/favorite
```

这里要注意，不能够在URL中滥用user这个资源，如果都是要获取自己相关的信息，都不需要显性的将user资源列举出来。

### Method

### RequestData:请求数据

笔者建议是采用扁平化的请求方式,其优势在于:
- 在Controller中可以以PathVariable以及RequestParam的方式显示地表明每个Controller的必填参数与选填参数,这样也方便进行单元测试。

- 与下面描述的弃用的JSON方式封装的参数相比,优势在于一个是将取值与默认值这一步交付给了SpringMVC去做,另一个是返回值上可以直接返回一个对象。

请求规范:
[GET] /api?key1=value1&key2=valu2
[POST] 
/api
Content-Type=application/x-www-form-urlencoded
key1=value1&key2=valu2

#### 原JSON封装式请求方案:Deprecated

我们会将除路径参数/动作参数外的请求数据放置到requestData中，以JSON形式发起请求。注意，如果我们将requestData放到URL中作为查询参数进行请求时，在部分客户端中需要进行手动的编码：

- JavaScript fetch 手动进行URL编码

```
        //将requestData序列化为JSON
        var requestDataString = encodeURIComponent(JSON.stringify(requestData).replace(/%22/g, "\""));
        //将字符串链接
        const packagedRequestURL = `${Model.BASE_URL}${path}?requestData=${requestDataString}&action=${action}`;
```

也可以将RequestData放置到POST/PUT请求的Body中进行，此时Body形式为：

```
requestData={}
```

而对应地我们需要在SpringMVC的Filter中添加对于Body的读取：

```java
//从requestBody中获取请求数据，放置到attribute中
            String requestBody = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (requestBody != null && requestBody.contains("=")) {
                requestBody = requestBody.replace("&", "");
                requestBody = requestBody.split("=").length == 2 ? requestBody.split("=")[1] : "";
                //判断requestBody是否为JSONObject
                if (request.getAttribute("requestData") == null && HJSONObject.getJsonType(requestBody) == 0) {
                    JSONObject jsonObject = wx.rarf.utils.HJSONObject.parseObject(requestBody);
                    if (jsonObject != null) {
                        //判读请求体中是否含有action
                        if (jsonObject.containsKey("action")) {
                            request.setAttribute("action", jsonObject.get("action"));
                        }
                        request.setAttribute("requestData", jsonObject.toJSONString());
                    }
                }
            }
```

## Authentic
验证数据体放在user_token键内,

## Data Format

### 时间日期

所有时间格式以Unix时间戳进行交换,为Long型整数,切记不可有小数点。

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
### JSONP Support

我们有大量的请求来自于WebAPP，这些WebAPP可能来自于不同的域名下，因此我们要保证服务端支持跨域请求的处理。注意，为了封装方便，每个Controller的返回值建议都设置为String，即我们手工的将对象转化为JSONString。

- 对于返回的数据进行封装

```java
StringBuffer buf = new StringBuffer();
buf.append(request.getParameter("callback"));
buf.append("(");
buf.append(rtn.toJSONString());
buf.append(");");
```

- 修改返回头

```java
response.setHeader("Access-Control-Allow-Origin","*");
```



### Session

Spring MVC本身提供给了我们一个内建的Session，不过笔者建议在无状态

## Service

### Naming

Service层命名时以`*Service`方式命名,具体的方法还是包含五类:

- get*:获取数据
  - get*ById:根据ID获取某些数据
  - get*ByName:根据名称获取某些数据
- post*:创建某个数据
- put*:更新某个数据
- delete*:删除某个数据
- do*:执行某个复杂操作

## Model

### Naming

Model层建议尽量用Mybatis Generator自动生成,命名方式主体还是脱胎于SQL语句:

- select*:查询数据
  - select*WhereIdEqual
  - select*whereNameIn
- insert*:插入数据
- update*:更新数据
- delete*:删除数据



# More

- [RARF：基于响应式抽象资源流的深度 RESTful 实践](https://segmentfault.com/a/1190000004600730)