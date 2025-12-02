# 项目迭代过程记录

## 当前进展（2025-11-26）
- 完成 Kotlin + Jetpack Compose 初始化与依赖配置（Hilt、Room、DataStore、Retrofit、Media3、Paging、Coil、Accompanist 动画）
- 建立 MVVM + Repository 架构与依赖注入，导航与转场可用
- 首页瀑布流（静态演示数据）、视频播放页、登录页占位、个人中心占位已实现
- 白色主题应用；启动页与应用图标完成；竖屏视频Feed支持上下滑动切换
- 构建修复：AndroidX/Jetifier 开启、BuildConfig 生成、DataStore 写入修正、ExoPlayer 绑定修复、Room exportSchema=false、AGP 警告处理、测试依赖补齐

## 技术栈与版本
- Kotlin 1.9.24；Compose Compiler 1.5.14；Compose BOM 2024.10.00
- AGP 8.5.1；minSdk 24；targetSdk 35
- Hilt 2.51.1；Room 2.6.1；Retrofit/OkHttp；Media3 1.4.1；Paging3；Coil 2.6.0

## 模块结构
- feature/auth、feature/home、feature/video、feature/profile
- core/network、core/database、core/datastore、core/model、core/repository、core/common
- navigation、app（Application/Activity）

## 关键实现
- AnimatedNavHost 路由与淡入淡出转场；后续拟迁移到 AndroidX 官方动画
- ExoPlayer 在 Compose 中通过 AndroidView 嵌入，生命周期释放
- DataStore 管理会话，Room 建库与占位实体/DAO

## 已解决问题
- BuildConfig 未生成 → 启用 buildFeatures.buildConfig
- DataStore 写入类型错误 → 使用 MutablePreferences
- ExoPlayer 变量与作用域冲突 → 重命名并在 factory/update 绑定
- Accompanist 动画实验性 API 报错 → @OptIn + 导入 fadeIn/fadeOut
- 导航弃用警告 → 迁移到 AndroidX Navigation Compose 的 NavHost（保留转场参数）
- Room KSP 导出警告 → exportSchema=false
- AndroidX 未启用 → 开启 useAndroidX/enableJetifier
- D8 OOM → 增大 org.gradle.jvmargs
- Gradle 缓存读取异常（metadata.bin FileNotFound）→ 建议清理 `E:\Android\.gradle\caches\transforms-4` 并重新同步
- Hilt 依赖循环 → 删除自引用 Module，使用构造注入
- 测试编译失败 → 添加 JUnit/AndroidX Test 依赖
- 顶部栏未移除 → 使用应用主题禁用系统 ActionBar/标题；Manifest 应用自定义主题；主页顶部改为自定义居中“推荐”+短下划线
- 主题父类资源不存在导致 AAPT 失败 → 引入 Material 组件库、主题父类改为 `Theme.MaterialComponents.DayNight.NoActionBar`
- HomeScreen 编译错误（未解析/Composable 调用环境）→ 补齐 `Row/width/size/fillMaxWidth/aspectRatio` 导入

## 待办与下一步
- 与后端对接：视频播放相关接口与应用逻辑优先；手机号登录/注册后端暂缓，先完成UI；后续替换 baseUrl 与接口契约
- 瀑布流分页：Paging3 + RemoteMediator，网络与本地缓存融合
- 导航动画迁移至 AndroidX 官方实现，探索共享元素过渡
- UI 风格细化：卡片信息、圆角阴影、占位骨架与加载策略
- 播放优化：缓冲策略、错误重试、横竖屏 UI 与手势
  - 交互增强（2025-12-01）：点击暂停显示标识、长按 2x/再次长按取消、底部极细进度条；底部文案分层（@用户名 + 描述），字体大小资源化
  - 样式细化（2025-12-01）：主页作者头像圆形、封面标签圆角与半径资源化；播放页头像细白边与分享图标替换为自定义矢量并设白色；二倍速标识增大；暂停态覆盖图标改为播放三角形
- 安全与合规：输入校验、权限最小化、HTTPS、日志隐私
 
## OSS接入方案与调试策略（2025-11-27）
- App 预留 `OssVideoApi` 与 Repository，占位接口：`/videos`（分页）与 `/videos/{id}/play`（播放地址获取）
- 安全：所有通信 HTTPS，播放地址由后端签名/代理；客户端不保存密钥
- 调试替代：使用 `BuildConfig.DEBUG_LOCAL_VIDEO_PATH` 播放 `file://` 或 `android.resource://`；待 OSS 可用后仅替换 BaseUrl 和映射
- 已设置 `DEBUG_LOCAL_VIDEO_PATH=android.resource://com.looklook/raw/demo`，演示视频位于 `res/raw/demo.mp4`
 
## 记录与规范
- 每次迭代后同步更新 README 与 DevelopmentProcess，记录 UI 调整、依赖与主题变更、错误与修复、后续计划与接口契约占位

## 多次交互重点问题记录（汇报用，细化版）
- 顶部黑色导航栏未移除
  - 症状：页面仍显示系统 ActionBar 与“LookLook”字样
  - 原因：应用主题父类不可用、Manifest 未正确应用主题
  - 解决：引入 `material` 依赖，主题父类改为 `Theme.MaterialComponents.DayNight.NoActionBar`，在 Manifest 应用 `@style/Theme.LookLook` 并禁用 ActionBar/标题；状态栏与背景统一白色
  - 验证：重新构建后系统栏移除，页面仅显示居中“推荐”与短下划线
  - 参考代码：`app/src/main/res/values/themes.xml`、`app/src/main/AndroidManifest.xml`
  - 影响范围：所有 Activity 顶部栏呈现；上线前检查主题一致性
  - 复盘建议：优先选用稳定父主题（Material Components），避免不可用资源名
- 主题父类资源缺失导致 AAPT 失败
  - 症状：`style/Theme.* not found` 资源链接失败
  - 原因：未引入提供该主题的依赖或主题名不匹配
  - 解决：添加 `com.google.android.material:material`，使用 `Theme.MaterialComponents.DayNight.NoActionBar`
  - 验证：资源链接通过，应用可编译
  - 参考代码：`app/build.gradle.kts`（依赖）、`app/src/main/res/values/themes.xml`
  - 影响范围：资源打包阶段；CI 构建需覆盖该检查
  - 复盘建议：主题父类与依赖版本建立映射清单，变更前先校验
- 双启动页问题（系统 Splash + 自定义 SplashActivity）
  - 症状：连续出现两页启动画面（圆形Logo+正方形Logo）
  - 解决：移除独立 `SplashActivity`，改由 `MainActivity` 承载唯一 Compose 启动页（白底居中 `applogo_trans.png`，宽度为屏宽一半；底部“Look the world you like”）
  - 验证：系统 Splash 后进入唯一自定义启动页，再进入主页
  - 参考代码：`app/src/main/java/com/looklook/app/MainActivity.kt`、`app/src/main/AndroidManifest.xml`
  - 影响范围：启动体验一致性、品牌展示统一
  - 复盘建议：Android 12+ 保留系统 Splash，视觉统一即可；自定义页作为过渡
- HomeScreen 编译错误与未解析引用
  - 症状：`fillMaxWidth/Row/size` 未解析、Composable 调用环境报错
  - 解决：补齐布局导入（`Row/width/size/fillMaxWidth/aspectRatio`），整理顶部实现为自定义居中标题
  - 验证：编译通过，主页渲染正常
  - 参考代码：`app/src/main/java/com/looklook/feature/home/ui/HomeScreen.kt`
  - 影响范围：主页渲染、点击导航
  - 复盘建议：启用 IDE 未使用/未导入检查；提交前本地编译
- Unresolved reference（状态栏与手势）
  - 症状：`statusBarsPadding/pointerInput/detectTapGestures` 未解析
  - 原因：缺少对应包导入或未引入 Compose Foundation/Pointer 依赖
  - 解决：补齐导入：`androidx.compose.foundation.layout.statusBarsPadding`、`androidx.compose.ui.input.pointer.pointerInput`、`androidx.compose.foundation.gestures.detectTapGestures`；已添加foundation依赖
  - 验证：构建通过，顶部安全区与播放手势正常
  - 参考代码：`HomeScreen.kt` 顶部避让、`VideoFeedScreen.kt` 手势控制
  - 影响范围：多机型适配、交互体验
  - 复盘建议：建立常用扩展函数导入清单；模板化 UI 顶部避让与手势层
- 竖屏视频 Feed 状态作用域错误
  - 症状：`stateIn(this, ...)` 期望 CoroutineScope 报错
  - 解决：改为 `viewModelScope`，并添加 Compose foundation 依赖（用于 `VerticalPager`）
  - 验证：Feed 可上下滑切换并播放
  - 参考代码：`app/src/main/java/com/looklook/feature/video/ui/VideoFeedScreen.kt`
  - 影响范围：数据流稳定性、页面切换流畅度
  - 复盘建议：Flow 生命周期统一用 `viewModelScope`；分页与播放器状态分离
- BuildConfig 未解析与本地演示视频
  - 症状：`BuildConfig` 未生成/未导入；本地视频路径不可用
  - 解决：启用 `buildFeatures.buildConfig` 并导入 `com.looklook.BuildConfig`；设置 `DEBUG_LOCAL_VIDEO_PATH=android.resource://com.looklook/raw/demo`
  - 验证：`res/raw/demo.mp4` 可直接播放
  - 参考代码：`app/build.gradle.kts`、`app/src/main/java/com/looklook/core/repository/VideoRepository.kt`
  - 影响范围：演示数据与可视验证
  - 复盘建议：构建字段集中管理（BaseURL、调试路径等）
- 导航动画弃用与实验 API 警告
  - 症状：Accompanist 动画 API 弃用/实验性报错
  - 解决：迁移到 AndroidX `navigation-compose` 的 `NavHost`，保留淡入淡出转场；必要处 `@OptIn`
  - 验证：路由与转场正常
  - 参考代码：`app/src/main/java/com/looklook/navigation/NavGraph.kt`
  - 影响范围：路由稳定性；版本升级兼容
  - 复盘建议：优先采用官方库，避免弃用 API 带来的维护成本
- AGP 不兼容与 AndroidX 未启用
  - 症状：AGP 8.6.1 环境不支持；`android.useAndroidX` 未开启编译失败
  - 解决：AGP 降级到 8.5.1；在 `gradle.properties` 开启 `android.useAndroidX=true`、`enableJetifier=true`
  - 验证：构建通过
  - 参考代码：`build.gradle.kts`、`gradle.properties`
  - 影响范围：构建管线；IDE 与 CI 一致性
  - 复盘建议：遵循支持矩阵；升级前对齐工程依赖
- Gradle 缓存异常
  - 症状：`metadata.bin FileNotFound` 导致构建中断
  - 解决：清理本机 Gradle 缓存（IDE Invalidate Caches 或删除 `E:\Android\.gradle\caches\transforms-4`），重新同步
  - 验证：后续构建成功
  - 影响范围：本机开发环境；CI 缓存策略
  - 复盘建议：提供一键清理脚本与文档提示

### 播放页 UI 迁移（抖音式控件）
- 症状：点击后仍出现传统控制条（暂停/快进/设置），与需求不符
- 原因：主页点击仍导航到旧 `VideoPlayerScreen`（`useController=true`）而非竖屏 `VideoFeedScreen`
- 解决：
  - 导航改为 `feed?start=<index>`；主页 `itemsIndexed` 传入索引
  - `VideoFeedScreen`：`useController=false`，右侧头像+按钮列，底部描述；单击暂停/播放、长按 2x；READY 前显示封面缓冲
- 验证：主页点击进入竖屏 Feed，无传统面板；未就绪显示封面，就绪后播放；手势与按钮交互正常
- 参考代码：`app/src/main/java/com/looklook/navigation/NavGraph.kt`、`app/src/main/java/com/looklook/feature/home/ui/HomeScreen.kt`、`app/src/main/java/com/looklook/feature/video/ui/VideoFeedScreen.kt`
- 远端接入问题与修复（2025-11-28）
  - 症状：主页显示远端卡片，进入Feed白屏；视频未播放、缓冲层未显示
  - 原因1：JSON链接含反引号/空格，导致播放器URI不合法
  - 原因2：Pager初始页数为0，数据未到达时页面为空
  - 解决：DTO映射时统一 `sanitizeUrl()`（去反引号与空格/trim）；`pageCount=max(1, size)` 并提供占位页；READY前始终显示封面覆盖层；`Uri.parse(cleanUrl)` 创建 MediaItem
  - 验证：主页五条视频可见；Feed进入时显示封面缓冲，READY后正常播放；异常时保留UI并可重试
- 播放性能诊断与优化（2025-11-28）
  - 指标：首帧耗时、重缓冲次数/时长、丢帧、带宽估计；采集 Player Analytics 日志
  - Player：LoadControl(min=15s,max=30s,playback=800ms,rebuffer=1500ms)、repeatMode=ONE、keepContentOnPlayerReset、裁剪缩放
  - 数据源：OkHttpDataSource + SimpleCache（200MB）→ CacheDataSource；自定义UA与超时、支持重定向
  - 预加载（2025-12-01）：当前页设置 `MediaItems(current,next)` 并 `prepare`；后台用 `CacheDataSource` 读取下一条视频前 512KB 进行热缓存；仅当前页绑定 `player`，非当前页显示封面与指示避免 Surface 竞争
  - 资源化与可视调整（2025-12-01）：主页标签字体 `home_tag_text_size_sp` 与标签间距 `home_tag_padding_*`；播放页操作组右/下边距与间距/尺寸在 `dimens.xml` 可视化调整；进度条颜色/高度资源化
  - 生命周期：退出时先暂停与清理绑定，避免残留；错误提示与重试入口
  - 验证：首帧时间降低、滑动无黑屏、循环无停顿、退出丝滑
- 竖屏视频滑动后完全露出卡住（Feed 页面）
  - 症状：在 VerticalPager 中，滑动到下一个视频半露出时可以播放；当松手让页面 settle、下一个视频完全露出后卡住不再继续播放。首尾视频无此问题，仅中间视频复现。
  - 原因：单实例 `ExoPlayer` 被同时绑定到多个 `PlayerView`（两个页面同时半可见），在页面 settle 时 Surface 切换/竞争导致渲染停止。
  - 解决：仅为当前页的 `PlayerView` 绑定 `player`，其他页设置为 `null`；并将非当前页的封面与加载指示始终可见，避免空白/误显示。
  - 代码：`app/src/main/java/com/looklook/feature/video/ui/VideoFeedScreen.kt:126-134`（按当前页绑定 `view.player`）、`app/src/main/java/com/looklook/feature/video/ui/VideoFeedScreen.kt:130-134`（非当前页强制封面/指示）。
  - 验证：本地构建 `assembleDebug` 通过；在中间任意视频进行上下滑动后，页面完全露出时可正常继续播放，无卡住现象；首尾视频表现一致。
