package com.example.linksharecompose.mymemo

data class Memo(
    val memoId: String = "",
    val uid: String? = null,
    val title: String = "",
    val content: String = "",
    val imageUri: String? = null,
    val linkUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
