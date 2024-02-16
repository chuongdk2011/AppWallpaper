package com.example.appwallpaper

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoryAdapter(var catList: ArrayList<CatDTO>, var wallpaperList: ArrayList<WallpaperDTO>) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryAdapter.MyViewHolder, position: Int) {
        val curCat = catList[position]
        val catId = curCat.id
        val catName = curCat.name

        holder.tv_nameCat.text = catName

        // Tìm wallpaper đầu tiên thuộc thể loại hiện tại
        val firstWallpaper = wallpaperList.find { it.idCat == catId }

        // Nếu có wallpaper, load ảnh đầu tiên
        if (firstWallpaper != null) {
            Glide
                .with(holder.itemView.context)
                .load(firstWallpaper.image)
                .centerCrop()
                .into(holder.img_first)
        } else {
            // Nếu không có ảnh, có thể hiển thị một ảnh mặc định hoặc ẩn ImageView
             Glide.with(holder.itemView.context).clear(holder.img_first)
             holder.img_first.setImageResource(R.drawable.anhthietbi)
        }

        holder.itemView.setOnClickListener {
            if (curCat.id == 1) {
                val intent = Intent(holder.itemView.context, DeviceImagesActivity::class.java)

                holder.itemView.context.startActivity(intent)
            } else {
                val intent = Intent(holder.itemView.context, DetailWallpaperActivity::class.java)
                intent.putExtra("catId", curCat.id)
                intent.putExtra("catName", catName)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return catList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_nameCat = itemView.findViewById<TextView>(R.id.tv_nameCat)
        val img_first = itemView.findViewById<ImageView>(R.id.img_first)
    }
}
