# robots.txt

robots.txt 是一个协议,我们可以把它理解为一个网站的"管家",它会告诉搜索引擎哪些页面可以访问,哪些页面不能访问。也可以规定哪些搜索引擎可以访问我们的网站而哪些搜索引擎不能爬取我们网站的信息等等,是网站管理者指定的"君子协议"。当一个搜索机器人（有的叫搜索蜘蛛）访问一个站点时，它会首先检查该站点根目录下是否存在 robots.txt，如果存在，搜索机器人就会按照该文件中的内容来确定访问的范围；如果该文件不存在，那么搜索机器人就沿着链接抓取。另外，robots.txt 必须放置在一个站点的根目录下，而且文件名必须全部小写。

百度的 robots.txt 如下所示：

```
User-agent: Baiduspider
Disallow: /baidu
Disallow: /s?
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Googlebot
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: MSNBot
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Baiduspider-image
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: YoudaoBot
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sogou web spider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sogou inst spider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sogou spider2
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sogou blog
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sogou News Spider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sogou Orion spider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: ChinasoSpider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: Sosospider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh


User-agent: yisouspider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: EasouSpider
Disallow: /baidu
Disallow: /s?
Disallow: /shifen/
Disallow: /homepage/
Disallow: /cpro
Disallow: /ulink?
Disallow: /link?
Disallow: /home/news/data/
Disallow: /bh

User-agent: *
Disallow: /
```

# robots.txt 的内容

- User-agent: 搜索机器人的名称
- Disallow: 不允许搜索机器人访问的地址
- Allow: 允许搜索机器人访问的地址

若 User-agent 是 `*`，则表示允许所有的搜索机器人访问该站点下的所有文件。在"robots.txt"文件中，"User-agent: `*`"这样的记录只能有一条。

Disallow 和 Allow 后面跟的是地址,这个 URL 可以是一条完整的路径，也可以是部分的，地址的描述格式符合正则表达式(regex)的规则。因此可以在 python 中使用正则表达式来筛选出可以访问的地址。需要特别注意的是 Disallow 与 Allow 行的顺序是有意义的，robot 会根据第一个匹配成功的 Allow 或 Disallow 行确定是否访问某个 URL。

下列内容代表 禁止所有搜索引擎访问网站的任何部分

```
User-agent: *
Disallow: /
```

## 如何创建 robots 文件？

用任意文本编辑器(就是写代码的软件)创建 robots.txt 文件。

**格式和位置规则**：

- 文件必须命名为 robots.txt。
- 网站只能有 1 个 robots.txt 文件。
- robots.txt 文件必须位于其要应用到的网站主机的根目录下。例如，若要控制对 `https://www.example.com/` 下所有网址的抓取，就必须将 robots.txt 文件放在 `https://www.example.com/robots.txt` 下，一定不能将其放在子目录中（例如 `https://example.com/pages/robots.txt` 下）。
- robots.txt 文件可应用到子网域（例如 `https://**website**.example.com/robots.txt`）或非标准端口（例如 `http://example.com:**8181**/robots.txt`）。
- robots.txt 文件必须是采用 UTF-8 编码（包括 ASCII）的文本文件。Google 可能会忽略不属于 UTF-8 范围的字符，从而可能会导致 robots.txt 规则无效。

## robots 文件的书写规则

- robots.txt 文件包含一个或多个组。
- 每个组由多条规则或指令（命令）组成，每条指令各占一行。每个组都以 `User-agent` 行开头，该行指定了组适用的目标。
- 每个组包含以下信息：
  - 组的适用对象（用户代理）
  - 代理可以访问的目录或文件。
  - 代理无法访问的目录或文件。
- 抓取工具会按从上到下的顺序处理组。一个用户代理只能匹配 1 个规则集（即与相应用户代理匹配的首个最具体组）。
- 系统的默认假设是：用户代理可以抓取所有未被 `disallow` 规则屏蔽的网页或目录。
- 规则区分大小写。例如，`disallow: /file.asp` 适用于 `https://www.example.com/file.asp`，但不适用于 `https://www.example.com/FILE.asp`。
- `#` 字符表示注释的开始处。

对着着示例说明：

```cobol
# Example 1: Block only Googlebot
User-agent: Googlebot
Disallow: /

# Example 2: Block Googlebot and Adsbot
User-agent: Googlebot
User-agent: AdsBot-Google
Disallow: /

# Example 3: Block all but AdsBot crawlers
User-agent: *
Disallow: /

Sitemap: http://www.example.com/sitemap.xml
```

- `user-agent:` [必需，每个组需含一个或多个 User-agent 条目] 该指令指定了规则适用的自动客户端（即搜索引擎抓取工具）的名称。这是每个规则组的首行内容。示例里是谷歌蜘蛛的名称，每个搜索引擎的蜘蛛名称不同。
- `disallow:` [每条规则需含至少一个或多个 `disallow` 或 `allow` 条目] 您不希望用户代理抓取的目录或网页（相对于根网域而言）。如果规则引用了某个网页，则必须提供浏览器中显示的完整网页名称。它必须以 `/` 字符开头；如果它引用了某个目录，则必须以 `/` 标记结尾。
- `allow:` [每条规则需含至少一个或多个 `disallow` 或 `allow` 条目] 上文中提到的用户代理可以抓取的目录或网页（相对于根网域而言）。此指令用于替换 `disallow` 指令，从而允许抓取已禁止访问的目录中的子目录或网页。对于单个网页，请指定浏览器中显示的完整网页名称。对于目录，请用 `/` 标记结束规则。
- `sitemap:` [可选，每个文件可含零个或多个 sitemap 条目] 相应网站的站点地图的位置。站点地图网址必须是完全限定的网址；Google 不会假定存在或检查是否存在 http、https、www、非 www 网址变体。

## 上传 robots 文件

加到网站的根目录(取决于网站和服务器架构)。

## 测试 robots.txt 标记

要测试新上传的 robots.txt 文件是否可公开访问，请在浏览器中打开无痕浏览窗口（或等效窗口），然后转到 robots.txt 文件的位置。例如：https://example.com/robots.txt。如果您看到 robots.txt 文件的内容，就可准备测试标记了。

测试工具：https://www.google.com/webmasters/tools/robots-testing-tool

## 常用的 robots 规则

```cobol
# 禁止所有搜索引擎抓取整个网站
User-agent: *
Disallow: /


# 禁止所有搜索引擎抓取某一目录及其内容（禁止抓取的目录字符串可以出现在路径中的任何位置，因此 Disallow: /junk/ 与 https://example.com/junk/ 和 https://example.com/for-sale/other/junk/ 均匹配。）

User-agent: *
Disallow: /calendar/
Disallow: /junk/
Disallow: /books/fiction/contemporary/


# 只有 googlebot-news 可以抓取整个网站。
User-agent: Googlebot-news
Allow: /

User-agent: *
Disallow: /


# Unnecessarybot 不能抓取相应网站，所有其他漫游器都可以。
User-agent: Unnecessarybot
Disallow: /

User-agent: *
Allow: /


# 禁止所有搜索引擎抓取 useless_file.html 网页。

User-agent: *
Disallow: /useless_file.html


# 禁止访问 dogs.jpg 图片。
User-agent: Googlebot-Image
Disallow: /images/dogs.jpg


# 禁止 Google 图片访问您网站上的所有图片(如果无法抓取图片和视频，则 Google 无法将其编入索引。)
User-agent: Googlebot-Image
Disallow: /


# 禁止谷歌抓取所有 .gif 文件。

User-agent: Googlebot
Disallow: /*.gif$


# 禁止抓取整个网站，但允许 Mediapartners-Google 访问内容
User-agent: *
Disallow: /

User-agent: Mediapartners-Google
Allow: /


# 禁止谷歌抓取所有 .xls 文件。
User-agent: Googlebot

```

## 如何更新 robots 文件？
