package com.antonov.hw4.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeStream
import android.graphics.Rect
import android.os.Binder
import android.os.IBinder
import android.util.Log

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce
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

    var photos: List<PhotoJson> = ArrayList()
    private val cashFullPhoto: ConcurrentHashMap<String, Bitmap> = ConcurrentHashMap()
    private val cashSmallPhoto: ConcurrentHashMap<String, Bitmap> = ConcurrentHashMap()
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val count = 30
    private val key = "5fdD_l9luA4foaqMJBQv5ONa5PumnxR7TkNu0qo5YHA"
    private val link = "https://api.unsplash.com/photos/random?count=$count&client_id=$key"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //TODO
        report("Service Started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(p0: Intent?): IBinder {
        report("Service binded")
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        fun getMyService() = this@MyService
    }

    //TODO if no wifi
    @ExperimentalStdlibApi
    suspend fun downloadPhotos() {
        report("Start downloading photos")
        report("Opening Connection to $link")
        val connection = URL(link).openConnection()
        report("Making reader")
        val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
        report("Getting ans")
        val ans = reader.readLine()
        report("Setting up moshi")
        val photoAdapter: JsonAdapter<List<PhotoJson>> = moshi.adapter<List<PhotoJson>>()
        var list: List<PhotoJson>?
        report("Trying to parse answer")
        try {
            list = photoAdapter.fromJson(ans)
            report("Successfully parsed photos: ${list?.size ?: -1}")
        } catch (e: IOException) {
            report("Error while getting list of photos: ${e.message}")
            list = ArrayList<PhotoJson>()
            e.printStackTrace()
        }
        photos = list ?: ArrayList()
        report("Launching coroutine scope")
        withContext(Dispatchers.IO) {
            val listD = LinkedList<Job>()
            for (i in photos.indices) {
                val small = photos[i].urls?.small
                if (small != null && !cashSmallPhoto.containsKey(small)) {
                    report("Downloading smallPhoto $i")
                    listD.add( async { downloadSmallPhoto(small) })
                }
            }
            for (i in listD.indices) {
                report("Joining $i")
                listD.get(i).join()
                report("Joined $i")
            }
            report("Downloaded all small photos: ${listD.size}")
        }
        //report( "Downloaded photo list: ${photos.size}")
        //val listType = object : TypeToken<ArrayList<PhotoJson>>() {}.type
        //val list = Gson().fromJson<ArrayList<PhotoJson>>(ans, listType)


    }

    fun getSmallPhoto(ind: Int): Bitmap? {
        report("SmallG :: Start getting small photo $ind")
        if(ind < photos.size) {
            val small = photos[ind].urls?.small
            report("SmallG :: Getting url of small photo $small")
            if (cashSmallPhoto.containsKey(small)) {
                report("SmallG :: Found small photo in memory")
                return cashSmallPhoto[small]
            } else {
                report("SmallG :: Couldn't find photo in memory, start download")
                scope.launch {
                    downloadSmallPhoto(small!!)
                    report("SmallG :: Downloaded photo $small")
                }
            }
        }
        return null
    }

    suspend fun downloadSmallPhoto(small : String){
        report("SmallD :: Downloading photo $small")
        report("SmallD :: Openenig connection and downloading small photo")
        val connection: URLConnection = URL(small).openConnection()
        val ph = BitmapFactory.decodeStream(connection.getInputStream())
        report("SmallD :: Putting small photo in memory")
        cashSmallPhoto[small] = ph
    }

    suspend fun getFullPhoto(ind: Int, width : Int): Bitmap? {
        if(ind < photos.size) {
            val full = photos.get(ind).urls?.full
            if (cashFullPhoto.containsKey(full)) {
                return cashFullPhoto[full]
            }
            downloadFullPhoto(full, width)
            return cashFullPhoto[full]
        }
        return null
    }

    fun downloadFullPhoto(full : String?, width : Int){
        val connection: URLConnection = URL(full).openConnection()
        val bitmap = BitmapFactory.decodeStream(connection.getInputStream())
        val ph = Bitmap.createScaledBitmap(bitmap, width, bitmap.height*width/bitmap.width, false)
        bitmap.recycle()
        if(ph != null) {
            cashFullPhoto.put(full!!, ph)
        }
    }

    private fun report(msg : String){
        Log.d("MyService", msg)
    }

}