package com.antonov.hw4

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antonov.hw4.recycler.PhotoAdapter
import com.antonov.hw4.databinding.ActivityMainBinding
import com.antonov.hw4.recycler.Photo
import com.antonov.hw4.service.MyService
import kotlinx.coroutines.*

private lateinit var binding: ActivityMainBinding

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    private lateinit var recycler : RecyclerView
    var myService : MyService? = null
    var isBound : Boolean = false
    val ioScope : CoroutineScope = CoroutineScope(Dispatchers.IO)
    var showingPhoto : Int = -1

    private val serviceConnection : ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service : IBinder) {
            val binderBridge : MyService.MyBinder = service as MyService.MyBinder
            myService = binderBridge.getMyService()
            isBound = true
            if(myService != null && myService!!.photos.isNotEmpty()) {
                setUpPhotos()
                if(showingPhoto != -1){
                    showHiResPhoto(showingPhoto)
                }
            } else {
                ioScope.launch{
                    try {
                        myService!!.downloadPhotos()
                        withContext(Dispatchers.Main) {
                            setUpPhotos()
                        }
                    } catch (e : Throwable) {
                        withContext(Dispatchers.Main){
                            Toast.makeText(
                                this@MainActivity,
                                "Can't download photos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myService = null
            isBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.setContentView(binding.root)

        report("onCreate")

        recycler = binding.imgRecycler

        showingPhoto = savedInstanceState?.getInt("photoInd") ?: -1

        binding.close.setOnClickListener {
            binding.hiResView.visibility = View.GONE
            showingPhoto = -1
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("photoInd", showingPhoto)
    }

    fun setUpPhotos(){
        report("setting up photos")
        val viewManager = LinearLayoutManager(this)
        recycler.apply {
            layoutManager = viewManager
            adapter = PhotoAdapter(myService, BitmapFactory.decodeResource(resources, R.drawable.none))
            {
                showHiResPhoto(it)
            }
        }
    }

    fun showHiResPhoto(ind : Int){
        showingPhoto = ind
        binding.hiResView.visibility = View.VISIBLE
        if(myService != null){
            val width = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                windowManager.currentWindowMetrics.bounds.width()
            } else {
                Resources.getSystem().getDisplayMetrics().widthPixels
            }
            ioScope.launch {
                try {
                    val photo = myService?.getFullPhoto(ind, width)
                    withContext(Dispatchers.Main) {
                        if (photo != null) {
                            binding.hiResImg.setImageBitmap(photo)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Can't download photo $ind",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e : Throwable) {
                    withContext(Dispatchers.Main){
                        Toast.makeText(
                            this@MainActivity,
                            "Can't download photo $ind",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        binding.hiResImg.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.none))
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MyService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

        recycler.hasPendingAdapterUpdates()


    }

    override fun onStop() {
        super.onStop()


        if(isBound){
            unbindService(serviceConnection)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }


    private fun report(msg: String) {
        Log.d("Main_Activity", msg)
    }
}