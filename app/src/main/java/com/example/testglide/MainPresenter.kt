package com.example.testglide

import android.graphics.Bitmap
import moxy.MvpPresenter

class MainPresenter(val model : MainModel) : MvpPresenter<MainView>() {
    fun loadImage(){
        viewState.showImage(model.getData())
    }

}