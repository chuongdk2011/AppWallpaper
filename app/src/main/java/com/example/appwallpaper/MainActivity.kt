package com.example.appwallpaper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var rcv_wall: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var wallpaperAdapter: CategoryAdapter
    private val READ_STORAGE_PERMISSION_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbRef = FirebaseDatabase.getInstance().getReference("WallPaper")
        initView()
        setupRecyclerView()
        fetchWallpaperData()
        FirebaseAnalytics.getInstance(this)
        checkReadStoragePermission()
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


                val deviceCatDTO = CatDTO(1, "Ảnh thiết bị")
                catList.add(0, deviceCatDTO)

                // Lấy ảnh từ drawable
                val drawableImageName = "anhthietbi" // Thay thế "your_drawable_image_name" bằng tên của ảnh trong drawable
                val drawableImageId = resources.getIdentifier(drawableImageName, "drawable", packageName)
                val drawableImagePath = "android.resource://${packageName}/drawable/${drawableImageName}"


                val deviceWallpaperDTO = WallpaperDTO(1, drawableImagePath, 1)
                wallpaperList.add(deviceWallpaperDTO)

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

    private fun checkReadStoragePermission() {
        // Kiểm tra xem quyền đã được cấp hay chưa
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Quyền chưa được cấp, yêu cầu người dùng cấp quyền
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        } else {
            // Quyền đã được cấp, thực hiện các hành động cần thiết
            showToast("Quyền đọc bộ nhớ đã được cấp!")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            // Kiểm tra xem quyền đã được cấp hay không
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, thực hiện các hành động cần thiết
                showToast("Quyền đọc bộ nhớ đã được cấp!")
            } else {
                // Quyền bị từ chối, hiển thị thông báo
                showToast("Quyền đọc bộ nhớ bị từ chối!")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
