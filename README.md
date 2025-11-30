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
