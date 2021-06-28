package com.example.testglide

import io.reactivex.Single
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface MainView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showImage(url: Single<String>)
}