package com.antonov.hw4.recycler

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.antonov.hw4.databinding.ListCardItemBinding
import com.antonov.hw4.service.MyService
import com.antonov.hw4.service.PhotoJson

class PhotoAdapter(
    private var photoService: MyService?,
    private val defaultBitmap  :Bitmap,
    private val onClickName: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {

        val holder = PhotoViewHolder(
            ListCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        holder.textView.setOnClickListener {
            (if(photoService != null) photoService!!.photos[holder.bindingAdapterPosition].id else "none")?.let { it1 ->
                onClickName(
                    holder.bindingAdapterPosition
                )
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int){
        report("onBind :: bind to $position")
        if(photoService != null) {
            report("onBind :: getting small Photo")
            val photo = photoService!!.getSmallPhoto(position)
            holder.bind(photoService!!.photos[position], photo)
        } else {
            report("onBind :: defaultCard")
            holder.bind(null, defaultBitmap)
        }
    }

    override fun getItemCount() =
        if(photoService != null) photoService!!.photos.size else 1


    class PhotoViewHolder(rootBinding: ListCardItemBinding) :
        RecyclerView.ViewHolder(rootBinding.root) {
        val textView: TextView = rootBinding.cardText
        val imageView: ImageView = rootBinding.photoView

        fun bind(photo: PhotoJson?, draw: Bitmap?) {
            textView.text = if (photo != null) photo.id else "none"
            imageView.setImageBitmap(draw)
        }
    }

    private fun report(msg : String){
        Log.d("Adapter", msg)
    }
}