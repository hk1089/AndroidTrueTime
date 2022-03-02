package com.android.truetime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.truetime.truetimerx.TrueTimeRx
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TrueTimeRx().build().initializeRx()
            .subscribeOn(Schedulers.io())
            .subscribe({ date ->
                Log.v("MainActivity", "TrueTime was initialized and we have a time: " + date);
            }, {
                it.printStackTrace()
            })
    }
}