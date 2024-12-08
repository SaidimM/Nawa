package com.saidim.nawa.view.models.items

import android.graphics.drawable.Drawable

interface Item {
    val title: String
    val image: Drawable
    val data: Any
}