package com.ab.screensizesupport.model

import android.net.Uri

data class VideoResultEntity(
    val id : String = "",
    val videoUri : String,
    val name : String,
    val preview : String
)