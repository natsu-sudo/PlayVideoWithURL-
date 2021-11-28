package com.assignment.kutuki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assignment.kutuki.R
import com.assignment.kutuki.pojo.VideoCategoriesDummy
import com.bumptech.glide.Glide

class ListOfCategoryAdapter(private val listener:(String)->Unit) : ListAdapter<VideoCategoriesDummy, ListOfCategoryAdapter.ViewHolder>(DiffCallBack()) {
    inner class ViewHolder(private val view: View):RecyclerView.ViewHolder(view) {
        private val getImageView:ImageView=view.findViewById(R.id.circle_name)
        init {
            itemView.setOnClickListener {
                if (getItem(adapterPosition).name[8] == '0'){
                    listener.invoke("${getItem(adapterPosition).name.substring(0,8)}${getItem(adapterPosition).name.substring(9)}")
                }else{
                    listener.invoke(getItem(adapterPosition).name)
                }

            }
        }

        fun onBind(item: VideoCategoriesDummy){
            Glide.with(view)
                    .load(item.image)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(getImageView)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.category_list_videos,parent,false)
        val viewGroup:ViewGroup.LayoutParams=view.layoutParams
        viewGroup.width= (parent.width*0.25).toInt()
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }


}

 class DiffCallBack:DiffUtil.ItemCallback<VideoCategoriesDummy>() {
    override fun areItemsTheSame(oldItem: VideoCategoriesDummy, newItem: VideoCategoriesDummy): Boolean {
        return oldItem.name==newItem.name
    }

    override fun areContentsTheSame(oldItem: VideoCategoriesDummy, newItem: VideoCategoriesDummy): Boolean {
        return oldItem==newItem
    }

}
