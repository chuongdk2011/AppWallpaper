package com.example.appwallpaper

import android.app.Dialog
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.transition.Transition
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import javax.sql.DataSource
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

class XemTruocActivity : AppCompatActivity() {

    lateinit var linkanh: String
    private val PERMISSION_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xem_truoc)
        linkanh = intent.getStringExtra("linkanh")!!
        var btn_ok = findViewById<TextView>(R.id.tv_set)
        var img_xemtruoc = findViewById<ImageView>(R.id.img_xemtruoc)
        var img_back = findViewById<ImageView>(R.id.img_back)
        var btn_download = findViewById<TextView>(R.id.tv_dl)

        img_back.setOnClickListener {
            onBackPressed()
        }
        Log.d("chuongdk", "onCreate: $linkanh")
        Glide.with(this).load(linkanh).into(img_xemtruoc)

        btn_ok.setOnClickListener { showDialog() }
        btn_download.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            } else {

                downloadImage(linkanh)
            }

        }

    }

    private fun downloadImage(imageUrl: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(imageUrl)

        val request = DownloadManager.Request(downloadUri)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle("Downloading Image")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")

        downloadManager.enqueue(request)


    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.customdialog)


        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width =
            resources.getDimensionPixelSize(R.dimen.dialog_width) // Đặt kích thước cố định cho chiều ngang
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParams


        val lll_mhc: LinearLayout = dialog.findViewById(R.id.lll_mhc)
        val lll_mhk: LinearLayout = dialog.findViewById(R.id.lll_mhk)
        val lll_cahai: LinearLayout = dialog.findViewById(R.id.lll_cahai)
        val closeButton: Button = dialog.findViewById(R.id.buttonDialogClose)

        lll_cahai.setOnClickListener {
            setWallpaper(linkanh)
            dialog.dismiss()
        }

        lll_mhk.setOnClickListener {
            setLockScreenWallpaper(linkanh)
            dialog.dismiss()
        }
        lll_mhc.setOnClickListener {
            setHomeScreenWallpaper(linkanh)
            dialog.dismiss()
        }

        closeButton.setOnClickListener {

            dialog.dismiss()
        }


        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        dialog.show()
    }

    private fun setWallpaper(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    setWallpaperFromBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // Handle loading failure if needed
                }
            })
    }

    private fun setWallpaperFromBitmap(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        try {
            wallpaperManager.setBitmap(bitmap)
            Toast.makeText(this@XemTruocActivity, "Đặt màn hình nền thành công", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            Log.d("chuongdk", "setWallpaperFromBitmap: ${e.localizedMessage}")
        }
    }

    // Kiểm tra xem màn hình khóa có được bảo vệ hay không
    private fun isKeyguardSecure(context: Context): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardSecure
    }

    // Đặt hình nền khóa từ link ảnh (nếu màn hình khóa được bảo vệ)
    private fun setLockScreenWallpaper(imageUrl: String) {
        if (isKeyguardSecure(this)) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        setLockScreenWallpaperFromBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Do nothing
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        // Handle loading failure if needed
                    }
                })
        } else {
            // Handle case where keyguard is not secure
        }
    }

    private fun setLockScreenWallpaperFromBitmap(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        try {
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            Toast.makeText(
                this@XemTruocActivity,
                "Thay đổi màn hình khóa thành công",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            // Handle exception if setting lock screen wallpaper fails
        }
    }

    private fun setHomeScreenWallpaper(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    setHomeScreenWallpaperFromBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // Handle loading failure if needed
                }
            })
    }

    // Đặt hình nền chính từ bitmap
    private fun setHomeScreenWallpaperFromBitmap(bitmap: Bitmap) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        try {
            // Đặt hình nền chỉ cho màn hình chính
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)

            Toast.makeText(
                this@XemTruocActivity,
                "Đổi màn hình chính thành công",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {

        }
    }


}