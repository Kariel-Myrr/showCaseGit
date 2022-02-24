package com.antonov.hw4.recycler

import android.graphics.Bitmap


data class Photo(
    val description: String,
    val phoneNumber: String,
    val smallPhoto: Bitmap?
)