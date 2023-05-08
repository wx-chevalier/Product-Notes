# 基本模板

在最简单的情况下，一个 WordPress 主题由两个文件构成：

- `style.css`
- `index.php`

这些文件都位于主题目录. 这`index.php` [模板](https://codex.wordpress.org/Stepping_Into_Templates) 是非常灵活的.他可以用来包含所有的引用 header， sidebar， footer， content， categories， archives， search， error， 和其它在 WordPress 产生的文件.

或者，他也可以模块化，使用单独的文件分担工作.如果你没有提供其它的模板文件，WordPress 会使用默认文件.比如说，如果你没有提供`comments.php` 文件， WordPress 会自动使用 `wp-comments.php` 模板文件 [Template Hierarchy](https://codex.wordpress.org/Template_Hierarchy). (注意: 自 3.0 起，那些默认的文件已经不能保证都存在或者跟以前的一样。提供你自己的模板文件会更安全。)

典型的模板文件包括：

- `comments.php`
- `footer.php`
- `header.php`
- `sidebar.php`

使用这些模板文件，你可以把这些文件嵌入到`index.php` 中，最后生成的文件里.

- 包含 header， 使用`get_header()`.
- 包含 sidebar， 使用 `get_sidebar()`.
- 包含 footer， 使用 `get_footer()`.
- 包含 search form， 使用 `get_search_form()`.

_include_ 用法:

```
<?php get_sidebar(); ?>

<?php get_footer(); ?>
```
