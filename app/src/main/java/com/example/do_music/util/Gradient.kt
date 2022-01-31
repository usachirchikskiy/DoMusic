package com.example.do_music.util

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

fun setgradient(textView:TextView) {
    val paint = textView.paint
    val width = paint.measureText(textView.text.toString())
    val textShader: Shader = LinearGradient(
        0f, 0f, width, textView.textSize, intArrayOf(
            Color.parseColor("#8E2DE2"),
            Color.parseColor("#4A00E0")
        ), null, Shader.TileMode.REPEAT
    )
    textView.paint.setShader(textShader)
}