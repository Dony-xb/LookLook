## 改动点

1) 覆盖层可见性
- 当前封面始终绘制在最上层，READY 后仍遮挡视频；改为仅在 `!ready` 时绘制封面覆盖层。

2) 右侧控件与头像
- 去除重复头像（删除额外的 AsyncImage）；右侧按钮列中的头像与主页作者行头像均改为圆形 `clip(CircleShape)`。
- 评论按钮使用 `ChatBubble` 图标，避免与点赞重复。

3) 描述显示
- `Video` 模型增加 `description:String?` 字段；Feed底部两行：第一行用户名（白色加粗），第二行视频描述（白色，两行省略）。主页卡片仍显示标题。

4) 起始索引与滚动
- 在 `videos` 加载完成后，通过 `pagerState.scrollToPage(startIndex.coerceIn(...))` 定位到点击的那条视频。

5) 背景与文字颜色
- Feed容器背景设置为黑色 `Color.Black`；底部文案 Text 设为白色，提高对比度。

## 文件改动摘要
- `core/model/Video.kt`：新增 `description:String?`
- `core/repository/VideoRepository.kt`：`VideoDto.toDomain()` 映射 `description`；`sanitizeUrl()` 保留
- `feature/video/ui/VideoFeedScreen.kt`：
  - 仅在 `!ready` 时绘制封面；背景黑色；底部用户名与描述（白色）
  - 右侧头像圆形，评论按钮使用 `ChatBubble`，删除重复头像
  - 使用 `collectAsState()` 获取列表，`LaunchedEffect(videos)` 滚动到 `startIndex`

## 验证
- 主页点击任意卡进入对应视频；READY前显示封面+按钮，READY后播放且封面不遮挡。
- 右侧按钮排列整齐；底部用户名与描述显示；整体背景黑色、文字白色。

## 文档
- 在 README 与 DevelopmentProcess 增加“Feed覆盖层与起始索引修复”说明，不创建新文档。