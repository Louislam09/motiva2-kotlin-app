package com.lamm.motiva2

data class PhraseItem(
    var imageResource: Int,
    val text: String,
    var isLike: Boolean,
    val author: String
)