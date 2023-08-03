package tech.wcw.compose.widget.simple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.Preview
import tech.wcw.compose.widget.simple.ui.theme.ComposewidgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposewidgetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(text = "正常文字零", color = Color.Black)

        Text(
            text = "竖排文字一", modifier = Modifier
                .vertical()
        )

        VerticalText(text = "竖排文字二", color = Color.Black)

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposewidgetTheme {
        Greeting()
    }
    AlignmentLine
}

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





