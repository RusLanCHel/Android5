package com.example.testglide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.bumptech.glide.Glide
import com.example.testglide.databinding.ActivityMainBinding
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.ByteArrayOutputStream

class MainActivity : MvpAppCompatActivity(), MainView {

    var vb: ActivityMainBinding? = null
    private lateinit var disposables: CompositeDisposable
    private lateinit var disposablesForImage: CompositeDisposable
    val presenter by moxyPresenter { MainPresenter(MainModel()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb?.root)

        vb?.loadButton?.setOnClickListener {
            presenter.loadImage()
            vb?.progressBar?.visibility = View.VISIBLE
            vb?.downloadImageButton?.visibility = View.VISIBLE
        }

        vb?.downloadImageButton?.setOnClickListener {
            vb?.image?.isDrawingCacheEnabled = true
            val bitmap = vb?.image?.drawingCache
            disposablesForImage = CompositeDisposable()
            disposablesForImage +=
                Single
                    .just(bitmap?.let { it1 -> convertImage(it1, 100) }?.let { it2 -> saveImage(it2) })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        ::onDownloadImageSuccess,
                        ::onDownloadImageError
                    )
        }
    }
    fun onDownloadImageSuccess(text: String){
        println("Success: $text")
    }

    fun onDownloadImageError(e: Throwable){
        e.printStackTrace()
    }

    fun saveImage(bitmap: Bitmap) : String = MediaStore.Images.Media.insertImage(
        applicationContext.contentResolver,
        bitmap,
        "myOwnImage.png",
        "Hi, I just save my image"
    )

    fun convertImage(bitmap: Bitmap, quality: Int) : Bitmap{
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray().also {
            stream.flush()
            stream.close()
            return BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }

    private fun onLoadImageSuccess(url: String){
        vb?.image?.let {
            Glide
                .with(this)
                .load(url)
                .into(it)
        }
        vb?.progressBar?.visibility = View.INVISIBLE
    }

    fun onLoadImageError(e: Throwable){
        e.printStackTrace()
    }

    override fun onStop() {
        super.onStop()
        try {
            disposables.dispose()
            disposablesForImage.dispose()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun showImage(url: Single<String>) {
        disposables = CompositeDisposable()
        disposables +=
            url
                .subscribe(
                    ::onLoadImageSuccess,
                    ::onLoadImageError
                )
    }
}