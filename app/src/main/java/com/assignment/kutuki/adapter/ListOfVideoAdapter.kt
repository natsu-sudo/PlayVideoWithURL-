package com.assignment.kutuki.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assignment.kutuki.R
import com.assignment.kutuki.pojo.VideoDetail
import com.bumptech.glide.Glide

private const val TAG = "ListOfVideoAdapter"
class ListOfVideoAdapter(private val listener:(VideoDetail)->Unit) :ListAdapter<VideoDetail, ListOfVideoAdapter.ViewHolder>(DiffCallBackVideo()){


    inner class ViewHolder(private val view: View):RecyclerView.ViewHolder(view){
        init {
            itemView.setOnClickListener {
                listener.invoke(getItem(adapterPosition))
            }
        }

         private val getImageView:ImageView=view.findViewById(R.id.image_button)
        private val textView:TextView=view.findViewById(R.id.bottom_text)

        @RequiresApi(Build.VERSION_CODES.M)
        fun onBind(item: VideoDetail) {
            Log.d(TAG, "onBind: "+item.thumbnailURL)
            Glide.with(view)
                    .load(item.thumbnailURL)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(getImageView)

        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.video_list_recycle_view, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

}

class DiffCallBackVideo():DiffUtil.ItemCallback<VideoDetail>() {
    override fun areItemsTheSame(oldItem: VideoDetail, newItem: VideoDetail): Boolean {
        return oldItem.description==newItem.description
    }

    override fun areContentsTheSame(oldItem: VideoDetail, newItem: VideoDetail): Boolean {
        return oldItem==newItem
    }
}