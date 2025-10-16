package com.medlog.mscptiv2.model

data class RowItem(
    val step: String,
    val location: String,
    val check: String,
    val description: String,
    val checked: Boolean = false
)
