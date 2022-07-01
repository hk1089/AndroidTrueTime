package com.android.truetime

import android.annotation.SuppressLint
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import com.android.truetime.truetimerx.TrueTimeRx
import com.android.truetime.truetimerx.TrueTimeRx.initRxTrueTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*


class AppClass():Application() {
    override fun onCreate() {
        super.onCreate()
        initRxTrueTime(applicationContext)
    }

}