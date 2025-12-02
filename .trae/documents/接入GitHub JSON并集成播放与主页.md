## 数据来源

* 使用 Retrofit + Moshi 从 `https://dony-xb.github.io/LookLook_data/Videos.json` 拉取视频列表。

* BaseURL：`BuildConfig.VIDEOS_BASE_URL = https://dony-xb.github.io/LookLook_data/`，接口路径：`Videos.json`。

## 数据结构与映射

* 响应 DTO：`VideosResponse(videos: List<VideoDto>)`。

* `VideoDto` 字段：`id/title/description/videoUrl/coverUrl/user{username,avatarUrl}/tags/stats/createdAt`。

* 映射到现有领域模型 `core.model.Video`：

  * `id → id`，`title → title`，`tags → tags`，`coverUrl → coverUrl`，`videoUrl → streamUrl`，`user.username → authorName`，`user.avatarUrl → authorAvatar`。

## 网络层

* 新建 `RemoteVideoApi`：`@GET("Videos.json") suspend fun fetch(): VideosResponse`。

* Hilt 模块：

  * 提供 `@Named("videosRetrofit") Retrofit`（baseUrl 为 BuildConfig.VIDEOS\_BASE\_URL）；

  * 提供 `RemoteVideoApi`（由 `videosRetrofit.create()`）。

## 仓库与视图模型

* 更新 `VideoRepository` 构造注入 `RemoteVideoApi`，新增 `getRemoteVideos(): Flow<List<Video>>`（失败时回退到本地/示例数据）。

* 将 `HomeViewModel` 与 `VideoFeedViewModel` 的数据源改为 `repo.getRemoteVideos()`。

## UI集成

* 主页瀑布流：展示从远端解析出的封面、标题、头像、昵称；点击卡片进入竖屏视频 Feed（传索引）。

* 播放页：竖屏 Feed 已实现封面缓冲与抖音式控件（右侧按钮列、底部描述、单击暂停、长按 2x）。

## 验证

* 构建并运行：主页显示五条远端视频卡；点击进入竖屏 Feed 按索引播放。

* 网络异常时使用本地 `demo.mp4` 或示例源作为回退。

## 文档

* 记录的文档不要改成新的文档，还是在之前要求过的的两个文档中做改动

