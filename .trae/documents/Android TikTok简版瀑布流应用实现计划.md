## 技术栈一致性说明

* README写明“语言: Java”，但Jetpack Compose与Hilt、协程在Android场景中以Kotlin为主；为避免后续大量互操作复杂度，建议全量采用Kotlin实现（Compose + Hilt + Coroutines + Retrofit + Room + DataStore + Media3/ExoPlayer）。

* 如需坚持Java，请确认；若无异议，本计划默认使用Kotlin实现并在Gradle与包结构中统一。

## 项目初始化

* 使用最新Android Gradle插件与Compose BOM，`minSdk=24`、`targetSdk=35`，启用Compose与Kotlin。

* 建立基础应用模块`app`，后续按需拆分feature与core模块（初期单模块更快迭代）。

## 依赖与配置

* UI: Jetpack Compose（Material3、Foundation、Navigation-Compose）。

* 网络: Retrofit2 + OkHttp3（Logging Interceptor、缓存策略）。

* 图片: Coil（支持Compose Image）。

* 视频: Media3（`media3-exoplayer` + `media3-ui` 或 Compose中通过`AndroidView`嵌入`PlayerView`）。

* 数据: Room（本地缓存）+ DataStore（轻量配置与会话数据）。

* DI: Hilt（KSP）。

* 导航与动画: Navigation-Compose + Accompanist Navigation Animation实现自定义转场。

* 分页: Paging 3 与Compose的`Lazy*`结合。

* 调试与质量: LeakCanary（debug）、Timber日志、JUnit/MockK/Turbine。

## 模块与包结构

* `feature/auth`（认证：短信验证码登录、会话管理）。

* `feature/home`（瀑布流视频主页：封面、标题、标签、头像昵称）。

* `feature/video`（播放页：横竖屏切换、网络流播放）。

* `feature/profile`（个人中心：信息展示、发布视频列表）。

* `core/network`（Retrofit服务、拦截器、错误与重试策略）。

* `core/database`（Room实体、DAO、数据库）。

* `core/datastore`（用户会话与偏好）。

* `core/model`（领域模型与DTO映射）。

* `core/repository`（仓库层聚合数据源）。

* `core/common`（结果封装、错误类型、util）。

## 架构与通用基建

* MVVM：ViewModel持有`UiState`（不可变数据类）+ `UiEvent`。

* 协程 + Flow：Repository返回`Flow<PagingData<Video>>`、`Flow<User>`等。

* 统一错误处理：`Result<T>`封装、网络异常映射、人性化提示。

* 依赖注入：Hilt提供Retrofit/Room/Repositories/ViewModels。

## 首页瀑布流

* 使用`LazyVerticalGrid`实现双列“卡片式瀑布流”，卡片支持不等高内容。

* 卡片信息：封面图（Coil）、标签、长标题、头像与昵称，小红书风格圆角与阴影。

* 支持分页加载与预取、下拉刷新、加载占位骨架。

* 点击卡片跳转播放页并携带视频ID与流地址。

## 视频播放

* 播放页集成Media3 ExoPlayer，`AndroidView`嵌入`PlayerView`或Compose封装。

* 支持竖屏为主、横屏全屏切换；生命周期绑定（自动释放）。

* 网络自适应缓冲、错误提示、重试与播放进度恢复。

## 用户认证

* 短信验证码流程：`requestSmsCode(phone)` → `verifyCode(phone, code)` → 会话保存到DataStore。

* 登录后拉取`UserProfile`并展示在个人中心；退出登录清理会话与本地缓存。

* 安全：输入校验、防刷限制（服务端配合），仅HTTPS。

## 个人中心

* 展示用户头像昵称、简介；列表显示该用户发布的视频（分页）。

* 设计与交互参考抖音风格，卡片一致的样式系统。

## 转场动画

* 使用Accompanist Navigation Animation为主页→播放页、主页→个人中心等路由添加自定义转场（淡入淡出+位移）。

* 卡片共享元素视觉：封面缩放过渡（Compose端近似实现）。

## 数据与网络

* Retrofit接口：`AuthApi`、`VideoApi`、`ProfileApi`；超时与重试策略、统一`ResponseAdapter`。

* Room缓存：视频列表与profile本地镜像，离线可浏览封面与基础信息。

* Paging来源：网络+数据库合并（RemoteMediator）以获得顺畅滚动体验。

## 性能优化

* 图片与视频预加载：列表滑动中提前请求封面与部分视频元数据。

* Compose性能：稳定keys、避免不必要重组、使用`remember`与`derivedStateOf`。

* ExoPlayer缓冲与内存占用调优、合理释放与复用。

## 安全与隐私

* 仅请求必要权限，电话/SMS相关权限按需与合规处理。

* 敏感信息加密存储，会话Token不写日志，不入版本库。

## 测试与质量保障

* 单元测试：Repository与ViewModel；Flow用Turbine校验。

* UI测试：Compose测试规则、导航与状态切换；播放页基本行为。

* 工具：LeakCanary监测泄漏、StrictMode（debug）。

## 交付与里程碑

* 里程碑1：项目与依赖初始化、基础架构、主页瀑布流静态数据。

* 里程碑2：网络与分页打通、播放页集成Media3、基本转场。

* 里程碑3：认证流程与个人中心、缓存与离线、性能优化。

* 里程碑4：测试完善、文档与演示数据、问题收尾。

## 资源与对接

* 需要后端API域名与接口契约（中国区计算资源），短信服务（阿里云短信等）、视频存储（阿里云OSS/腾讯云COS）。

* 暂无真实服务时，先以Mock与本地演示数据实现，接口与模型保持一致以便后续替换。

## 需要确认

* 是否同意将实现语言统一为Kotlin以匹配Compose与现代Android栈。是

* 是否已有后端API与短信服务对接信息；若暂无，将先以Mock实现，待接入后替换。暂无，先以Mock实现

