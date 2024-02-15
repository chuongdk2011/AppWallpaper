package com.example.appwallpaper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var rcv_wall: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var wallpaperAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbRef = FirebaseDatabase.getInstance().getReference("WallPaper")
        initView()
        setupRecyclerView()
        fetchWallpaperData()
        FirebaseAnalytics.getInstance(this)
    }

    private fun initView() {
        rcv_wall = findViewById(R.id.rcv_wall)
    }

    private fun setupRecyclerView() {


        wallpaperAdapter = CategoryAdapter(arrayListOf(), arrayListOf())

        rcv_wall.adapter = wallpaperAdapter
    }

    private fun fetchWallpaperData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val catList = ArrayList<CatDTO>()
                val wallpaperList = ArrayList<WallpaperDTO>()

                val categorySnapshot = snapshot.child("Category")
                for (catSnapshot in categorySnapshot.children) {
                    val cat = catSnapshot.getValue(CatDTO::class.java)
                    cat?.let {
                        catList.add(it)
                    }

                    val imageSnapshot = snapshot.child("Image").children.firstOrNull {
                        it.child("idCat").getValue(Long::class.java) == cat?.id?.toLong()
                    }
                    val wallpaper = imageSnapshot?.getValue(WallpaperDTO::class.java)
                    wallpaper?.let {
                        wallpaperList.add(it)
                    }
                }

                Log.d("chuongdk", "onDataChange: catList=$catList, wallpaperList=$wallpaperList")

                wallpaperAdapter.catList = catList
                wallpaperAdapter.wallpaperList = wallpaperList
                wallpaperAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("chuongdk", "onCancelled: ${error.message}")
            }
        })
    }
}
