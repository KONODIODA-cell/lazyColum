package com.shaw.lazycolum

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PerformanceComparison()
            }
        }
    }
}


data class MyItem(
    val id: Int,
    val title: String,
    val description: String
)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PerformanceComparison() {
    var itemCount by remember { mutableIntStateOf(10) } // item项数状态
    var useLazyColumn by remember { mutableStateOf(true) }

    Column(Modifier.padding(vertical = 30.dp, horizontal = 20.dp)) {
        // 控制面板
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("item项数: $itemCount")
            Slider(
                value = itemCount.toFloat(),
                onValueChange = { itemCount = it.toInt() },
                valueRange = 10f..10000f,
                modifier = Modifier.weight(1f)
            )
        }

        ToggleButton(
            checked = useLazyColumn,
            onCheckedChange = { useLazyColumn = it } // todo 点击之后的卡顿如何解决
        ) {
            Text(
                "点击切换使用组件 ",
                modifier = Modifier.padding(vertical = 10.dp)
            )
            Text(
                if (useLazyColumn) "当前正在使用 LazyColumn" else "当前正在使用 Column",
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        // 性能测试区域
        BoxWithConstraints {
            val height = maxHeight * 1f
            var items by remember { mutableStateOf<List<MyItem>>(emptyList()) }

            // 开启协程 填装数据
            LaunchedEffect(itemCount) {
                updateCount(itemCount)?.let {
                    items = it
                }
            }

            Column(Modifier.height(height)) {
                Text("组件测试:")
                if (useLazyColumn) {
                    LazyColumnExample(items)
                } else {
                    NonLazyList(items)
                }
            }
        }
    }
}

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        content()
    }
}

@Composable
fun LazyColumnExample(items: List<MyItem>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items) { item ->
            ComplexListItem(item = item)
        }
    }
}

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

// 测试数据生成
private var lastCount = 10
private var job: Job? = null // 用于存储当前的协程
suspend fun updateCount(newCount: Int): List<MyItem>? {
    // 取消之前的协程任务
    job?.cancel()

    // 更新 lastCount
    lastCount = newCount

    // 启动一个新的协程任务
    val deferred = CoroutineScope(Dispatchers.IO).async {
        delay(500) // 等待 500 毫秒
        if (lastCount == newCount) {
            println("当前正在进行类型的创建!")
            generateItems(newCount)
        } else {
            println("不稳定不给你创建!")
            null
        }
    }

    // 等待协程任务完成并返回结果
    return deferred.await()
}

private fun generateItems(count: Int): List<MyItem> {
    return with(Dispatchers.IO) {
        List(count) { index ->
            MyItem(
                id = index,
                title = "项 $index",
                description = "描述 "
                    .repeat(Random.nextInt(0, 11)) // 不同长度的内容
            )
        }
    }
}

// item UI
@Composable
fun StaticContent(item: MyItem) {
    Text(text = item.title, style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = item.description)
}

@Composable
fun DynamicContent() {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "细节 ".repeat(10),
        style = MaterialTheme.typography.bodySmall
    )
    Image(
        imageVector = Icons.Default.AccountBox,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplexListItem(item: MyItem) {
    var expanded by remember { mutableStateOf(true) } // 默认展开
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            StaticContent(item = item)
            if (expanded) {
                DynamicContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerformanceComparisonPreview() {
    MaterialTheme {
        PerformanceComparison()
    }
}