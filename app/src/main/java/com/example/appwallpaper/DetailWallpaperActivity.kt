package com.example.appwallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailWallpaperActivity : AppCompatActivity() {

    lateinit var btn_back:ImageView
    lateinit var tv_title:TextView
    var idCat:String = "0"
    private lateinit var rcv_wall: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var wallpaperAdapter: WallpaperAdapter
    var list = ArrayList<WallpaperDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_wallpaper)

        initView()

        tv_title.text = intent.getStringExtra("catName")
        idCat = intent.getStringExtra("catId")!!
        btn_back.setOnClickListener {
            onBackPressed()
        }

        dbRef = FirebaseDatabase.getInstance().getReference("WallPaper").child("Image")
        Log.d("chuongdk", "onCreate: "+idCat)
        Log.d("chuongdk", "onCreate: "+intent.getStringExtra("catName"))
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (listwall in snapshot.children){
                    var wallDTO = listwall.getValue(WallpaperDTO::class.java)

                    wallDTO?.let {
                        if (wallDTO.idCat == idCat){
                            list.add(it)
                        }

                    }
                }

                wallpaperAdapter = WallpaperAdapter(list)
                rcv_wall.adapter = wallpaperAdapter
                wallpaperAdapter.notifyDataSetChanged()
                Log.d("chuongdk", "onDataChange: ${list.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("chuongdk", "onCancelled: ${error.message}")
            }
        })
    }

    private fun initView() {
        btn_back = findViewById(R.id.img_back);
        tv_title = findViewById(R.id.tv_titleDetails);
        rcv_wall = findViewById(R.id.rcv_wallpaper);

    }
}