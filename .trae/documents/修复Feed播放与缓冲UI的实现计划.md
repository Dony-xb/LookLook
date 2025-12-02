## 问题分析
- 白屏原因可能有两类：
  1) URL不规范：JSON中的链接含空格或反引号（`）等包裹符导致解析/请求失败；
  2) Pager初始化为0页：进入Feed时数据尚未加载，`rememberPagerState(pageCount=list.size)`初始为0，页面为空；
- 缓冲UI未显示：READY状态判断未触发或UI在空列表时未构建；同时右侧按钮与底部文案仅在有item时渲染。

## 修复方案
1) 规范化URL
- 在 DTO→领域模型映射时，统一对 `videoUrl/coverUrl/user.avatarUrl` 做清理：`trim()` 并去掉反引号与包裹空格，保证是纯净的HTTPS URL。
- 针对可能的特殊字符再做 `Uri.encode` 或 `Uri.parse` 处理，避免ExoPlayer因不合法URI失败。

2) Pager稳态与占位
- 将 `VerticalPager` 的 `pageCount` 改为至少为1（当列表为空时显示占位页），待数据到达后自动更新。
- 在空数据时显示全屏占位封面/占位背景与右侧控件列（禁用点击），避免纯白。

3) 播放器与缓冲UI
- 初始化ExoPlayer后，先设置封面Overlay可见；监听`Player.Listener.onPlaybackStateChanged`：
  - `STATE_BUFFERING`：显示封面Overlay与右侧控件；
  - `STATE_READY`：隐藏封面Overlay并自动播放；
  - `onPlayerError`：显示错误提示与重试按钮（重置并重新`setMediaItem`）。
- 单击暂停/播放，长按2x速度；长按结束后恢复1x（抬起后再次轻触恢复）。

4) 网络播放健壮性
- 使用 `Uri.parse(cleanUrl)` 创建 `MediaItem`，避免`fromUri`对异常字符串的不友好处理。
- 可选：自定义 `HttpDataSource.Factory` 设置User-Agent与合理超时，提升对OBS的兼容性。

5) 文档细化
- 在 README与DevelopmentProcess中记录：
  - URL清理策略与示例；
  - Pager稳态与占位页逻辑；
  - 缓冲UI状态与错误重试；
  - 长按加速与单击暂停的交互说明；

## 变更点（获批后实施）
- DTO映射添加 `sanitizeUrl()` 清理函数（去反引号/空格，必要时编码）。
- `VideoFeedScreen`：
  - `pageCount = max(1, videos.size)`；
  - 空数据占位页；
  - Overlay与错误重试逻辑；
  - `Uri.parse(cleanUrl)`。
- `HomeScreen`与导航保持`feed?start=index`逻辑；
- 文档更新两处。

## 验证
- 主页展示五条远端视频；
- 进入Feed：未就绪显示封面与控件，READY后播放；错误时提示并可重试；
- 链接含反引号/空格时仍能正常播放或提示错误。