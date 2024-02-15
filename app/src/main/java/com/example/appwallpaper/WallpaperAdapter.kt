package com.example.appwallpaper

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WallpaperAdapter(var list:ArrayList<WallpaperDTO>) : RecyclerView.Adapter<WallpaperAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WallpaperAdapter.MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wallpaper, parent, false)
        return WallpaperAdapter.MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: WallpaperAdapter.MyViewHolder, position: Int) {
        var curWall = list.get(position);

        Glide
            .with(holder.itemView.context)
            .load(curWall.image)
            .centerCrop()
            .into(holder.img_wallpp)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,XemTruocActivity::class.java)
            intent.putExtra("linkanh",curWall.image)
            holder.itemView.context.startActivity(intent)
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_wallpp = itemView.findViewById<ImageView>(R.id.img_wallpaper)

    }
}