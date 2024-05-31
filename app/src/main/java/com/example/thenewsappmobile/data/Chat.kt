package com.ahmedapps.thenewsapp.data

import android.graphics.Bitmap
/*
Person Responsible for this class : DINH HUU QUANG ANH - 21110752
*/

data class Chat (
    val prompt: String,
    val bitmap: Bitmap?,
    val isFromUser: Boolean
)