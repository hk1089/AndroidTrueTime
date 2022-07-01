package com.android.truetime

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.truetime.truetimerx.TrueTimeRx
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.*


class MainActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            TrueTime.isInitialized()
            Log.v("MainActivity", "TrueTime was initialized and we have a time: " + Date());
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("GMT+05:30")
        formatter.format(Date())
        Log.e("RYATA", "rawDate>> ${ formatter.format(Date())}")


        try {
            TrueTimeRx.build().withLoggingEnabled(true).initializeRx("time.google.com")
                .subscribeOn(Schedulers.io())
                .subscribe({ date ->
                    Log.v("MainActivity", "TrueTime was initialized and we have a time: " + date);
                }, {
                    it.printStackTrace()
                })
        }catch (e: Exception){
            e.printStackTrace()
        }

    }
}