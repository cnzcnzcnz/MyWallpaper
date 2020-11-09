package com.abstraksi.kimetsunoyaiba.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.abstraksi.kimetsunoyaiba.R


fun ImageView.loadImageFromUrl(url: String){
    if (url.isEmpty()) return

    Glide.with(this.context)
        .load(url)
        .placeholder(if (url.endsWith(".gif")) R.drawable.placeholder_gif else R.drawable.placeholder_image)
        .into(this)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invis(){
    this.visibility = View.INVISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}

fun DisplayMetrics.getHeight(context: Context): Int {
    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(this)
    return this.heightPixels
}

fun DisplayMetrics.getWidth(context: Context): Int {
    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(this)
    return this.widthPixels
}
