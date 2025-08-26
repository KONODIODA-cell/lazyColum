# Jetpack Compose：LazyColumn 与 Column 性能对比实战

在 Jetpack Compose 中，列表渲染是 UI 开发的高频场景。Compose 提供了两种常用的列表组件：`Column` 和 `LazyColumn`。它们在 API 使用上类似，但在性能和适用场景上有本质区别。本文结合实际项目（lazyColum），通过实验和代码分析，带你深入理解两者的差异。

## 1. 基本概念

- **Column**：最基础的纵向布局，所有子项会一次性全部渲染到内存和界面上，适合 item 数量较少的场景。
- **LazyColumn**：惰性加载的纵向列表，只会渲染当前屏幕可见的 item，随着滚动动态加载/卸载，适合大数据量场景。

## 2. 实验项目简介

本项目 `lazyColum` 提供了一个可视化对比界面，支持：
- 动态切换 Column 和 LazyColumn 两种实现
- 通过滑块调整 item 数量（10~10000）
- 每个 item 展示标题、描述、可展开的细节内容（含图片）

核心代码片段如下：

```kotlin
// 切换组件
if (useLazyColumn) {
    LazyColumnExample(items)
} else {
    NonLazyList(items)
}
```

## 3. 现象对比

### 3.1 小数据量（如 10~100 项）

- **Column** 和 **LazyColumn** 都能流畅渲染，界面无明显卡顿。
- 内存占用差异不大，滚动体验一致。

### 3.2 大数据量（如 1000~10000 项）

- **Column**：
  - 首次渲染明显变慢，甚至出现界面卡死、ANR。
  - 内存占用急剧上升，所有 item 都会被创建和布局。
  - 滚动时流畅度下降，尤其是 item 复杂时更明显。
- **LazyColumn**：
  - 首次渲染依然流畅，只加载可见区域的 item。
  - 内存占用随可见 item 数量变化，极低。
  - 滚动时动态加载，体验始终流畅。

### 3.3 交互体验

- 切换 item 数量或组件类型时，Column 方式在大数据量下会有明显的“卡顿”或“无响应”现象，而 LazyColumn 基本无感知。

## 4. 原理分析

- **Column**：所有子项都在 Compose Tree 中实例化、测量和绘制，item 越多，消耗越大。
- **LazyColumn**：采用惰性加载机制，只实例化和布局当前屏幕可见的 item，滚动时复用和回收不可见 item，极大节省资源。

## 5. 代码对比

### Column 实现

```kotlin
@Composable
fun NonLazyList(items: List<MyItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            ComplexListItem(item = item)
        }
    }
}
```

### LazyColumn 实现

```kotlin
@Composable
fun LazyColumnExample(items: List<MyItem>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items) { item ->
            ComplexListItem(item = item)
        }
    }
}
```

## 6. 结论与建议

- **小数据量**：两者均可，Column 代码更简单。
- **大数据量**：务必使用 LazyColumn，避免性能和内存问题。
- **实际开发**：只要是列表（尤其是动态数据、无限滚动等），优先选择 LazyColumn。

## 7. 项目体验

你可以 clone 本项目，运行后通过滑块和切换按钮直观体验两者的差异。

项目地址：[https://github.com/Brand0shaw/lazyColum](https://github.com/Brand0shaw/lazyColum)

源码已开源，欢迎学习和交流。

---

如需进一步补充代码细节、性能测试数据或动图演示，可随时告知！
