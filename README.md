# TikTok简版瀑布流视频应用

## 项目概述

一个仿TikTok风格的短视频瀑布流应用，基于Android平台开发，提供流畅的视频浏览体验和完整的用户系统。

## 功能特性

### 核心功能

* 📱 **瀑布流视频主页**

  * 上下滑动浏览视频
  * 视频封面展示
  * 标签、长标题、用户头像和昵称显示
  * 小红书风格UI设计

* ▶️ **视频播放**

  * 点击跳转播放页面
  * 支持横竖屏播放
  * 网络视频流播放

* 👤 **个人中心**

  * 用户信息展示
  * 发布的视频列表
  * 抖音风格个人页面UI

### 技术特性

* 🎨 自定义转场动画
* ⚡ 性能分析与优化
* 🏗️ 清晰的架构设计
* 📊 网络请求与数据存储

## 技术栈

### 主要技术

* **语言**: Kotlin
* **UI框架**: Jetpack Compose
* **架构**: MVVM + Repository

### 开发工具

* Android Studio 2024
* Android SDK 24+
* Git + GitHub
* 华为云OBS云储存
* 
### 第三方库

* **网络**: Retrofit2 + OkHttp3
* **图片加载**: Coil
* **视频播放**: ExoPlayer
* **数据库**: Room
* **依赖注入**: Hilt
* **导航**: Navigation Compose + Accompanist Navigation Animation

## 项目结构
 
## 迭代更新记录

* 2025-12-01（Feed滑动卡住修复）
  * 修复竖屏视频 Feed 在滑动到下一个页面完全露出后卡住不播放的问题
  * 原因：同一个 `ExoPlayer` 同时绑定到多个 `PlayerView`（两个页面半可见时存在两个视图绑定），在页面 settle 时发生 Surface 竞争导致渲染/播放停止
  * 方案：仅在当前页绑定 `player`，其他页 `player=null`，非当前页强制展示封面与加载指示，避免多 Surface 争抢
  * 代码参考：`app/src/main/java/com/looklook/feature/video/ui/VideoFeedScreen.kt:126-134`
  * 验证：本地 `assembleDebug` 通过；中间任意视频上下滑动均可正常继续播放；首尾视频行为一致

* 2025-11-26
  * 完成Kotlin + Compose项目初始化与Gradle配置，启用 Hilt、Room、DataStore、Retrofit、Media3、Paging、Coil、Accompanist 动画
  * 建立MVVM + Repository架构与依赖注入，创建主页瀑布流、播放页、登录页占位、个人中心占位与路由
  * 修复构建问题：AndroidX/Jetifier、BuildConfig 生成、DataStore写入、ExoPlayer绑定、Room schema警告、AGP版本警告
  * 添加测试依赖（JUnit、AndroidX Test），通过 assembleDebug 与基础测试编译
  * 后续计划：接入真实后端与分页、迁移官方导航动画、完善UI样式与共享元素过渡
* 2025-11-26（补充）
  * 将导航动画从 Accompanist 迁移至 AndroidX Navigation Compose 的 `NavHost`，保留淡入淡出转场以消除弃用警告
  * 处理环境问题：建议清理本机 Gradle 缓存（`E:\Android\.gradle\caches\transforms-4\...`）并重新同步，避免 `metadata.bin` 读取异常
  * 将 Android Gradle Plugin 版本从 `8.6.1` 降级为 `8.5.1`，与当前开发环境兼容
  * 修复点击播放崩溃：添加 `INTERNET` 权限、导航参数进行 URI 编码/解码、替换为可播放的示例视频 URL
  * 登录策略调整：手机号登录先完成 UI，不接入后端；优先完善视频播放功能与应用逻辑，后续再接入注册/登录后端
* 2025-11-26（UI迭代）
  * 深色主题（黑灰主色）并应用全局
  * 启动页与应用图标设置，启动页展示Logo
  * 主页卡片UI调整为小红书风格：圆角、标题与昵称信息
  * 新增竖屏视频Feed（上下滑动切换），覆盖账号名、描述、右侧头像等基础信息
  * 新增注册页UI与登录页UI占位，后端暂缓
  * 用户信息页样式增强
* 2025-11-27（白色主题与OSS方案）
  * 主题改为白色，统一浅色配色；启动页仅保留一个，白底居中Logo
  * 主页顶部居中“推荐”，取消左侧标识；卡片维持小红书风格
  * 预留 OSS 接口（列表与播放），引入占位仓库；新增 `BuildConfig.OSS_BASE_URL` 与 `DEBUG_LOCAL_VIDEO_PATH`
  * 本地调试支持：可使用 `file://` 或 `android.resource://` 方案；在 OSS 可用后切换为网络接口
  * 已配置 `DEBUG_LOCAL_VIDEO_PATH=android.resource://com.looklook/raw/demo`，将 `app/src/main/res/raw/demo.mp4` 作为默认演示视频
* 2025-11-27（顶部栏与主题修复）
  * 问题：系统黑色导航栏未移除；XML主题父类引用不可用导致 AAPT 资源链接失败
  * 解决：引入 `com.google.android.material:material`，主题父类改为 `Theme.MaterialComponents.DayNight.NoActionBar`，在 Manifest 应用 `@style/Theme.LookLook`，禁用 ActionBar 与标题；状态栏与背景设为白色
  * 主页顶部采用自定义居中“推荐”+短下划线，不再使用 TopAppBar；卡片尺寸改为小红书纵向比例
  * 编译问题：补齐 `Row/width/size/fillMaxWidth/aspectRatio` 导入，消除未解析错误
* 2025-11-28（远端视频接入与Feed修复）
  * 从 GitHub Pages 解析 `Videos.json`，映射到领域模型；增加 `VIDEOS_BASE_URL`
  * DTO映射清理URL（去空格与反引号）以确保OBS视频/图片链接可播放与可加载
  * Feed页：空数据也有占位页；READY前显示封面缓冲，错误可重试；导航统一为 `feed?start=index`
* 2025-11-28（播放性能优化）
  * 新增 PlayerViewModel（Hilt），配置 LoadControl（快速起播）与 OkHttp+CacheDataSource、repeatMode；日志采集首帧与状态
  * 预加载策略与切换：READY前只显示封面与Spinner；首帧后移除封面；避免黑屏
  * 播放页背景黑色、文字白色；头像圆形；描述展示
* 2025-12-01（下一条视频预加载优化）
  * 在竖屏视频 Feed 切换时为当前页设置 `MediaItems(current,next)` 并准备
  * 新增预取逻辑：使用 `CacheDataSource` 在后台读取下一条视频前 512KB 以热缓存与连接
  * 仅当前页 `PlayerView` 绑定 `player`，非当前页显示封面与加载指示，避免 Surface 竞争
  * 代码参考：`PlayerViewModel.kt` 的 `prefetch(url)` 与 `VideoFeedScreen.kt` 的 `LaunchedEffect(currentPage,videos)`
* 2025-12-01（主页卡片可视化可调）
  * 将主页卡片样式的关键参数抽取为资源：`res/values/dimens.xml`、`res/values/integers.xml`、`res/values/colors.xml`
  * 支持通过 Android Studio Resource Manager 可视化修改：卡片圆角、阴影高度、网格间距、封面宽高比、标签颜色与内边距等
  * 修改入口：`app/src/main/java/com/looklook/feature/home/ui/HomeScreen.kt` 使用了上述资源；无需改代码即可在资源编辑器里调整样式
  * 新增可视化属性：封面标签最小宽度与固定高度、主页顶部栏上下内边距
* 2025-12-01（播放页操作组可视化可调）
  * 播放页右侧“头像、点赞、收藏、评论、分享”合并为垂直操作组，位置可通过资源调整（顶部/居中/底部对齐，边距）
  * 图标间距与每个图标尺寸抽取到 `dimens.xml`，直接在 Resource Manager 中修改
  * 代码参考：`app/src/main/java/com/looklook/feature/video/ui/VideoFeedScreen.kt` 中对资源的读取与位置映射逻辑
* 2025-12-01（播放页交互与显示增强）
  * 点击暂停显示暂停标识；长按切换 2x 并显示 2x 标识；再次长按取消 2x
  * 底部增加极细进度条，紧贴屏幕底部；颜色与高度可通过资源调整
  * 底部文案分层：上方为 `@用户名`，下方为视频描述；两者字体大小抽取到 `integers.xml`（sp）
  * 主页封面标签：新增 `home_tag_text_size_sp` 以抽取字体大小；标签边距使用 `home_tag_padding_*`
* 2025-12-01（主页与播放页样式细化）
  * 主页卡片底部作者头像改为圆形；封面标签新增圆角并抽取 `home_tag_radius`
  * 播放页右侧头像增加细白边（保持圆形），分享按钮替换为自定义矢量 `ic_share_douyin` 且为白色
  * 二倍速标识字体增大；暂停态覆盖图标改为播放三角形 `PlayArrow`

## 播放性能诊断与优化（阶段性记录）

- 指标采集
  - 首帧耗时（prepare→onRenderedFirstFrame）、重缓冲次数/时长、丢帧、带宽估计（Timber 打印）
- Player 配置
  - LoadControl：minBufferMs=15000、maxBufferMs=30000、bufferForPlaybackMs=800、bufferForPlaybackAfterRebufferMs=1500
  - repeatMode=REPEAT_MODE_ONE；裁剪缩放全屏；PlayerView 保持内容
- 数据源与缓存
  - OkHttpDataSource + SimpleCache(200MB) → CacheDataSource；UA=LookLook/1.0
- 预加载与切换
  - 当前页先起播，下一页预缓冲；未就绪显示封面+Spinner；首帧后移除封面
- UI 与交互
  - 背景黑色；文案白色；右侧按钮列与圆形头像；底部显示用户名与描述
- 退出与错误
  - 离开页面暂停播放器，避免残留；错误时提供重试入口（后续补充数值统计输出）
