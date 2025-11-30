## UI调整
- 主题：切换为浅色主题（白色背景、深色文字），Material3 Light 配色；仅在徽章/按钮使用强调色。
- 启动页：保留一个 SplashActivity，白色背景+居中 Logo，Manifest 仅一个 LAUNCHER。
- 主页导航：顶部使用 CenterAlignedTopAppBar，居中“推荐”并处于选中态；移除左上角标识。
- 卡片样式：小红书风格圆角卡片，左上角“LIVE”徽章+人数，封面下标题两行省略，头像+昵称行，整体白色主题优化对比与间距。
- 视频页：竖屏全屏播放，底部进度条、账号名、视频描述，右侧中部作者头像；上下滑切换。

## OSS视频接入方案
- 架构：后端使用 OSS 作为视频存储与来源，App 通过 HTTPS 接口获取“视频列表（分页）+ 播放地址”。
- 接口占位（App 侧）：
  - 列表 `GET /videos?cursor=&size=` → `[{id,title,desc,coverUrl,author{name,avatar},objectKey|playUrl}], hasMore, nextCursor`
  - 播放 `GET /videos/{id}/play` → `{playUrl, headers?, drm?}`（如需要鉴权/重定向由后端处理）
- App 端实现：
  - Retrofit 定义 `OssVideoApi` 与 Repository；Paging3+RemoteMediator 打通分页与本地缓存（Room `VideoEntity` 与 `RemoteKeys`）。
  - 点击卡片获取 `playUrl` 并在竖屏 Feed 中播放；支持错误提示与重试。
- 安全与合规：所有通信 HTTPS；不在客户端硬编码 OSS 密钥；必要时通过后端签名 URL；速率限制与防盗链由后端侧处理。

## 本地调试替代
- 说明：Android 设备无法直接访问 Windows 路径 `E:\...`；提供以下替代方式：
  1) 将测试视频通过 `adb push` 放入设备 `/sdcard/Movies/sonder_plan.mp4`，App 支持 `file://`/`content://` 播放；
  2) 暂存到 `res/raw` 并通过 `android.resource://` 播放；
  3) 启动本地 HTTP 服务器（如 `http://localhost:8000/video.mp4`）并在设备网络可达环境下访问。
- 实施：我将在 Repository 中预留针对 `file://` 与 `android.resource://` 的占位路径支持，待你提供 OSS 后端后切换为网络接口。

## 数据与分页
- 使用 Paging3 + RemoteMediator：支持连续瀑布流滑动获取，Room 缓存封面与基础信息、离线浏览封面。
- DTO 映射：后端返回 `objectKey` 时由 App 侧拼接 `playUrl = OSS_BASE_URL + objectKey` 或通过 `play` 接口拿到签名 URL；在 App 端保留可配置的 `BuildConfig.OSS_BASE_URL` 与可选 `DEBUG_LOCAL_VIDEO_PATH` 用于本地调试。

## 文档更新
- README：记录白色主题、单一启动页、主页“推荐”居中、小红书卡片、竖屏视频页；明确“视频后端优先，手机号注册/登录后端暂缓”。
- DevelopmentProcess：补充 OSS 接入架构、接口契约、分页与缓存策略、错误处理与预加载、调试替代方式。

## 执行顺序（获批后）
1) 切主题为白色并精简启动页；
2) 顶部栏“推荐”居中与卡片样式；
3) 添加 `OssVideoApi`、Repository 占位、BuildConfig 字段；
4) 支持 `file://`/`android.resource://` 作为临时播放源；
5) 更新两份文档并准备对接后端域名与契约。

## 需要你的确认
- 采用 OSS 后端代理方案并通过接口返回 `coverUrl/objectKey/playUrl` 可行；请稍后提供域名与接口契约或沿用上述约定。
- 本地替代路径使用 `adb push` 到设备存储或 `res/raw` 资源是否可以？我将同时保留两种入口，确保你在租用 OSS 之前可以运行演示。