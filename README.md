## ComposeWidget

Compose内置了很多组件，可满足基本的开发需求，在复杂的UI组成时也推荐组合的方式，有种搭积木的感觉，但实际应用中不可避免的要自定义组件。

### 源码地址

（项目用于自定义组件演示，后期会逐步提供一些常用的自定义组件）

### Compose的测量和布局

在AndroidView的测量中，View的测量通常会触发多次，随着布局层级的增多，测量次数增长是很恐怖的，后来Android有了ConstraintLayout用来解决布局嵌套过多的问题，一定程度上提高了View的绘制性能。这与本文关系不是太大，感兴趣的小伙伴可以自己试试。
言归正传！Compose组件在测量时区别与Android
View,它的每个子组件一般来说只被测量一次，在测量时会根据父组件的约束条件（父组件对子组件的宽高限制等）来进行自我测量，在测量后执行layout，摆放布局。当组件参数发生变化时，产生重组。

### Compose自定义View的方式

Android
View方式实现的UI在自定义布局时通常是继承View或者某个ViewGroup,按需重写onDraw、onMeasure，onLayout等方法，同时还可以自己实现drawable的绘制。而Compose主要是通过Modifier、Layout、Canvas等直接或者间接影响组件的测量、布局、绘制方式来实现。接下来本文以这三种方式来实现"
文字竖排"视图效果

#### Modifier

Modify本身含有graphicsLayer等操作符可以实现旋转、缩放、裁剪等操作，在这些操作不满足我们的需求时可以对齐扩展。
本文通过扩展Modifier，增加vertical修饰函数，在layout时将文字横排时所得的宽高互换，（横排时的宽为竖排时的高、横排时的高为竖排的宽），在经过旋转，即可得到竖排文字。

```kotlin
fun Modifier.vertical() = this
    .then(
        layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            //关键点：按宽高互换后的尺寸layout占位
            layout(placeable.height, placeable.width) {

                placeable.place(
                    x = (placeable.height - placeable.width) / 2,
                    y = (placeable.width - placeable.height) / 2
                )
            }
        }
    )
    .rotate(90f)

//使用
Text(
    text = "竖排文字一", modifier = Modifier
        .vertical()
)
```

#### Canvas

Canvas提供了drawRect、drawArc、drawCircle、drawImage、drawLine、drawPath等绘制Api,涵盖了日常需求。
本文Canvas实现竖排文字是通过textMeasurer测量单个文字，然后依次绘制没个文字即可。注意drawText绘制时offset参数，文字竖排时y轴偏移量是lastBaseline。

```kotlin
@OptIn(ExperimentalTextApi::class)
@Composable
fun VerticalText(
    text: String,
    color: Color = Color.Unspecified,
    textStyle: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = modifier) {
        text.toCharArray().forEachIndexed { index, it ->
            val textLayoutResult = textMeasurer.measure(it.toString(), textStyle)
            textLayoutResult.lastBaseline
            drawText(
                textLayoutResult,
                color,
                Offset(0f, index * textLayoutResult.lastBaseline)
            )
        }
    }
}

```

#### Layout

对于竖排文字，此方式实现是大材小用了，思路与Column组件是一致的，主要是layout时，每个子组件在前一个组件下方。

```kotlin
@Composable
inline fun VerticalLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            var yOffset = 0
            placeables.forEach { placeable ->
                placeable.place(0, yOffset)
                yOffset += placeable.height
            }
        }
    }

}
```

### 下期预告

在学习了Compose如何自定义组件后，我们结合自定义组件和手势处理，实现一个Banner