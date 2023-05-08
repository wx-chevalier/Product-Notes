# WordPress 自定义页面

WordPress 主题由一系列文件和 CSS 样式表构成，构成了一个美丽的 WordPress 网站。每个主题都是不同的， 这样 WordPress 用户就可以随时更改 WordPress 网站的外观。

# 主题的剖析

WordPress 主题目录位于 wp-content/themes/。主题的子目录拥有所有样式文件、模板文件、可选的函数文件 (functions.php)、JavaScript 文件、图片等。比如说一个叫做 "test" 的主题就会放在 wp-content/themes/test/目录里。请避免使用数字名字，这会导致无法在主题列表中正常显示出来。

WordPress 每一个发行版都会有一个默认的主题。请认真查看默认的主题，这样可能会对制作你自己的主题有帮助。WordPress 主题除了图片和 JavaScript，经常由三种文件构成。

- 样式表文件 style.css， 控制着页面的外观
- 函数文件 (functions.php)。
- 模板文件，它控制着从数据库中调出的数据所呈现的外观。

## 子主题

最简单的主题可能是子主题，其仅仅包含一个 style.css 文件，也可以加上一些图片。之所以它能工作是因为它是以另一个主题为基础进行工作的。

# Links

- https://www.wpzhiku.com/document/template-tags/
