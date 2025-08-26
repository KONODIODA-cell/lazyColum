# LazyColum

## 项目简介

LazyColum 是一个基于 Jetpack Compose 的 Android 应用，用于对比和演示 `LazyColumn` 与普通 `Column` 在大数据量场景下的性能差异。通过可视化界面，用户可以动态切换组件类型、调整 item 数量，并观察两种实现方式在渲染和交互上的表现。

## 主要功能
- 支持动态切换 `LazyColumn` 和 `Column` 组件。
- 可通过滑块调整 item 数量（10~10000）。
- 每个 item 展示标题、描述和可展开的细节内容（含图片）。
- 性能对比直观，适合 Compose 学习和性能调优参考。

## 技术栈
- Kotlin
- Jetpack Compose
- Material3 设计体系
- 协程（用于异步数据生成）

## 目录结构
- `app/src/main/java/com/shaw/lazycolum/`
  - `MainActivity.kt`：主界面与核心逻辑。
  - `ui/theme/`：主题、配色、字体等样式定义。
- `app/src/main/res/`：资源文件（图片、布局等）。
- `build.gradle.kts`：项目和模块构建配置。

## 如何运行
1. 使用 Android Studio 打开本项目。
2. 连接 Android 设备或启动模拟器。
3. 编译并运行应用。

## 适用场景
- Jetpack Compose 初学者学习列表渲染原理。
- 性能测试与优化实践。
- 组件切换与 UI 响应性体验。

## License
MIT
