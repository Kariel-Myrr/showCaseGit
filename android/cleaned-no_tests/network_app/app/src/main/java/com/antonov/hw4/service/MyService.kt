package com.antonov.hw4.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.util.Log

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MyService : Service() {

    companion object {
        private const val COUNT = 30
        private const val KEY = "5fdD_l9luA4foaqMJBQv5ONa5PumnxR7TkNu0qo5YHA"
        private const val LINK =
            "https://api.unsplash.com/photos/random?count=$COUNT&client_id=$KEY"
    }

    var photos: List<PhotoJson> = ArrayList()
    private val cashFullPhoto: ConcurrentHashMap<String, Bitmap> = ConcurrentHashMap()
    private val cashSmallPhoto: ConcurrentHashMap<String, Bitmap> = ConcurrentHashMap()
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        report("Service Started")
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder {
        report("Service binded")
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        fun getMyService() = this@MyService
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }


    //experimental annotation for moshi
    //know that I have to make error wrapper like Success ParseError<String>, NetworkError<String>, but don't have time
    /*
    -1 - parse error
    -2 - network error

    0 - ok

    n - count of photos that wasn't downloaded

     */
    @ExperimentalStdlibApi
    suspend fun downloadPhotos(): Int {
        try {
            val connection = URL(LINK).openConnection()
            val reader = BufferedReader(InputStreamReader(connection.getInputStream()))


            val ans = reader.readLine()
            val photoAdapter: JsonAdapter<List<PhotoJson>> = moshi.adapter()
            try {
                photos = photoAdapter.fromJson(ans) ?: ArrayList()
            } catch (e: IOException) {
                report(e.message ?: "empty error body")
                return -1
            }

            var counter = 0

            val downloadJob = ioScope.launch {
                val listD = LinkedList<Deferred<Bitmap?>>()
                for (i in photos.indices) {
                    val job = async {  getSmallPhoto(i) }
                    listD.add(job)
                }
                for (job in listD) {
                    try {
                        job.await()
                    } catch (e : IOException){
                        counter++
                    }
                }
            }
            downloadJob.join()
            report("Downloaded all small photos, except: $counter")
            return counter


        } catch (e: IOException) {
            report(e.message ?: "empty error body")
            return -2
        }
    }

    suspend fun getSmallPhoto(ind: Int): Bitmap? {
        report("Getting small photo")
        if (ind < 0 || ind >= photos.size) return null
        val small = photos[ind].urls?.small ?: return null

        return cashSmallPhoto.getOrElse(small) {
            downloadSmallPhoto(small)
        }
    }

    fun getSmallPhotoFastAndNotify(ind : Int) : Bitmap?{
        report("Getting small photo fast")
        if (ind < 0 || ind >= photos.size) return null
        val small = photos[ind].urls?.small ?: return null

        return cashSmallPhoto.getOrElse(small) {
            ioScope.launch {
                getSmallPhoto(ind)
            }
            null
        }
    }

    private suspend fun downloadSmallPhoto(small: String): Bitmap {
        report("Downloading small photo $small")
        val connection: URLConnection = URL(small).openConnection()
        val ph = BitmapFactory.decodeStream(connection.getInputStream())
        cashSmallPhoto.putIfAbsent(small, ph)
        report("Photo cached")
        return ph
    }

    suspend fun getFullPhoto(ind: Int, width: Int): Bitmap? {
        if (ind < 0 || ind >= photos.size) return null
        val full = photos[ind].urls?.full ?: return null
        return cashFullPhoto.getOrElse(full) {
            downloadFullPhoto(full, width)
        }
    }

    private fun downloadFullPhoto(full: String, width: Int): Bitmap? {
        val connection: URLConnection = URL(full).openConnection()
        val bitmap = BitmapFactory.decodeStream(connection.getInputStream())
        val ph =
            Bitmap.createScaledBitmap(bitmap, width, bitmap.height * width / bitmap.width, false)
        bitmap.recycle()
        if (ph != null) {
            cashFullPhoto.putIfAbsent(full, ph)
        }
        return ph
    }

    private fun report(msg: String) {
        Log.d("MyService", msg)
    }

}