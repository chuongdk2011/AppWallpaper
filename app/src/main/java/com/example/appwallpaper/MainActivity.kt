package com.example.appwallpaper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var rcv_wall: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private lateinit var wallpaperAdapter: CategoryAdapter
    private val READ_STORAGE_PERMISSION_CODE = 123
    private lateinit var tv_title:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbRef = FirebaseDatabase.getInstance().getReference("WallPaper")
        initView()
        setupRecyclerView()
        fetchWallpaperData()
        FirebaseAnalytics.getInstance(this)
        checkReadStoragePermission()

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        val content = remoteConfig.getString("add_a_category")
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result

                    Log.d("chuongdk", "Config params updated: $updated")
                    Toast.makeText(this, "Fetch and activate succeeded", Toast.LENGTH_SHORT,).show()
                } else {
                    Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT,).show()
                }
                tv_title.text = content
            }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate : ConfigUpdate) {
                Log.d("chuongdk", "Updated keys: " + configUpdate.updatedKeys);

                Log.d("chuongdk", "onUpdate: $content")
                if (configUpdate.updatedKeys.contains("add_a_category")) {
                    remoteConfig.activate().addOnCompleteListener {
                        tv_title.text = content
                    }
                }
            }

            override fun onError(error : FirebaseRemoteConfigException) {
                Log.w("chuongdk", "Config update error with code: " + error.code, error)
            }
        })
    }

    private fun initView() {
        rcv_wall = findViewById(R.id.rcv_wall)
        tv_title = findViewById(R.id.tv_title)
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


                val deviceCatDTO = CatDTO("1", "Ảnh thiết bị")
                catList.add(0, deviceCatDTO)

                // Lấy ảnh từ drawable
                val drawableImageName =
                    "anhthietbi" // Thay thế "your_drawable_image_name" bằng tên của ảnh trong drawable
                val drawableImageId =
                    resources.getIdentifier(drawableImageName, "drawable", packageName)
                val drawableImagePath =
                    "android.resource://${packageName}/drawable/${drawableImageName}"


                val deviceWallpaperDTO = WallpaperDTO("1", drawableImagePath, "1")
                wallpaperList.add(deviceWallpaperDTO)

                val categorySnapshot = snapshot.child("Category")
                for (catSnapshot in categorySnapshot.children) {
                    val cat = catSnapshot.getValue(CatDTO::class.java)
                    cat?.let {
                        catList.add(it)
                    }

                    val imageSnapshot = snapshot.child("Image").children.firstOrNull {
                        it.child("idCat").getValue(String::class.java)?.toString() == cat?.id
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

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


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
