package com.example.appwallpaper

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class DeviceImagesActivity : AppCompatActivity() {

    private lateinit var rcv_wallpaperDevice:RecyclerView
    private  lateinit var adapter:WallpaperAdapter
    var list = ArrayList<WallpaperDTO>()
    private  lateinit var img_back:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_images)

        img_back = findViewById(R.id.img_back)

        rcv_wallpaperDevice = findViewById(R.id.rcv_wallpaperDevice)
        adapter = WallpaperAdapter(list)
        rcv_wallpaperDevice.adapter = adapter

        img_back.setOnClickListener {
            onBackPressed()
        }


        loadDeviceImages()
    }

    private fun loadDeviceImages() {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Thêm các ảnh vào danh sách
                list.add(0,WallpaperDTO("1", contentUri.toString(), "1"))
            }
        }

        // Cập nhật Adapter sau khi đã lấy được danh sách ảnh
        adapter.notifyDataSetChanged()
    }
}