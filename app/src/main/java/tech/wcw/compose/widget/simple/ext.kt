package tech.wcw.compose.widget.simple

import android.app.Activity
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * @author: tech_wcw@163.com
 * @date: 2023/8/3
 */
fun Activity.configBar(
    show: Boolean = true,
    darkIcon: Boolean = true,
    color: Int = ContextCompat.getColor(this, android.R.color.white)
) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController?.let {
        insetsController.show(WindowInsetsCompat.Type.statusBars())
        insetsController.show(WindowInsetsCompat.Type.navigationBars())
        insetsController.isAppearanceLightStatusBars = darkIcon
        insetsController.isAppearanceLightNavigationBars = darkIcon
        window.statusBarColor = color
        window.navigationBarColor = color
        if (!show) {
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

fun Activity.toast(msg: String, duration: Int = LENGTH_LONG) {
    Toast.makeText(this, msg, duration)
}

fun Any.log(tag: String = this.javaClass.simpleName) {
    Log.i(tag, this.toString())
}