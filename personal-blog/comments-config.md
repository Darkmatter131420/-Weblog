# Giscus 配置预留

评论系统暂不硬编码仓库 ID / category ID。

启用 Giscus 前，需要在 GitHub 仓库开启 Discussions，并在 giscus.app 生成对应的 repo/category 配置。

建议最终由构建器读取：

- `GISCUS_REPO`
- `GISCUS_REPO_ID`
- `GISCUS_CATEGORY`
- `GISCUS_CATEGORY_ID`

这样不会把错误或占位配置发布到正式博客。
