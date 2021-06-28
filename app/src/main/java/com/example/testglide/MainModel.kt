package com.example.testglide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Single
import java.io.ByteArrayOutputStream

class MainModel {
    
    private val dataImage = "https://static.wikia.nocookie.net/among-us/images/8/8f/AmongUsIcon.png/revision/latest/top-crop/width/360/height/450?cb=20201005175954&path-prefix=ru"

    fun getData(): Single<String> {
        return Single.just(dataImage)
    }



}