package com.lfork.phonelimitadvanced.main.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.DialogInterface
import android.provider.MediaStore
import android.content.Intent
import android.widget.ImageButton
import java.io.File
import kotlinx.android.synthetic.main.background_set_act.*
import java.io.IOException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Window
import android.widget.Toast
import com.lfork.phonelimitadvanced.R
import com.lfork.phonelimitadvanced.data.saveBackgroundFilePath
import com.lfork.phonelimitadvanced.utils.ContentUriUtil
import com.lfork.phonelimitadvanced.utils.setTransparentSystemUI


class BackgroundSettingActivity : AppCompatActivity() {

    val REQUEST_CAMERA = 1
    val REQUEST_ALBUM = 2
    val REQUEST_WRITE_PERMISSION = 3
    val REQUEST_READ_PERMISSION = 4
    val REQUEST_CROP = 5
    val REQUEST_CAMERA_PERMISSION = 6
    private val mPictureIb: ImageButton? = null
    private var mImageFile: File? = null
    private var resultIsOk: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.background_set_act)
        setTransparentSystemUI()
        init()

    }

    override fun onResume() {
        super.onResume()
        if (resultIsOk == false) {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun startBackgroundSelectActivity(context: Context) {
            context.startActivity(Intent(context, BackgroundSettingActivity::class.java))
        }
    }


    private fun init() {
        if (!haveReadPermission() || !haveWritePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    this@BackgroundSettingActivity,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQUEST_WRITE_PERMISSION
                )
            }
        } else {
            onClickPicker()
        }
    }


    private fun haveWritePermission(): Boolean {
        val permissionCheck1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return permissionCheck1 == PackageManager.PERMISSION_GRANTED
    }


    private fun haveReadPermission(): Boolean {
        val permissionCheck1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permissionCheck1 == PackageManager.PERMISSION_GRANTED
    }

    private fun haveCameraPermission(): Boolean {
        val permissionCheck1 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        return permissionCheck1 == PackageManager.PERMISSION_GRANTED
    }


    fun onClickPicker() {
        AlertDialog.Builder(this)
            .setTitle("选择照片")
            .setCancelable(true)
            .setItems(arrayOf("拍照", "相册"), object : DialogInterface.OnClickListener {
                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                    resultIsOk = false
                    if (i == 0) {
                        if (!haveCameraPermission()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ActivityCompat.requestPermissions(
                                    this@BackgroundSettingActivity,
                                    arrayOf(Manifest.permission.CAMERA),
                                    REQUEST_CAMERA_PERMISSION
                                )
                            }
                        } else {
                            selectCamera()
                        }
                    } else {
                        selectAlbum()
                    }
                }
            })
            .setOnCancelListener {
                finish()
            }
            .create()
            .show()
    }


    private fun selectAlbum() {
        val albumIntent = Intent(Intent.ACTION_PICK)
        albumIntent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(albumIntent, REQUEST_ALBUM)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK != resultCode) {
            return
        }
        resultIsOk = true
        when (requestCode) {
            REQUEST_CAMERA -> {
                saveBackgroundFilePath(mImageFile!!.absolutePath)
//                iv_bg.setImageURI(Uri.fromFile(mImageFile))
                SettingsChangeManager.notifyBackgroundChanged()
                finish()
            }
            REQUEST_ALBUM -> {
                createImageFile()
                if (!mImageFile!!.exists()) {
                    return
                }
                val uri = data!!.data
                if (uri != null) {
//                    toast("图片真实途径：${ContentUriUtil.getPath(this, uri)}")
//                    iv_bg.setImageURI(uri)

                    saveBackgroundFilePath(ContentUriUtil.getPath(this, uri))
                    SettingsChangeManager.notifyBackgroundChanged()
                }

                finish()
            }
//            REQUEST_CROP -> mPictureIb!!.setImageURI(Uri.fromFile(mImageFile))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_PERMISSION, REQUEST_READ_PERMISSION -> {
                if (haveWritePermission() && haveReadPermission()) {
                    onClickPicker()
                } else {
                    toast("文件权限不足,程序无法正常运行，请授予相应权限")
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (haveCameraPermission()) {
                    selectCamera()
                } else {
                    toast("相机权限不足,程序无法正常运行，请授予相应权限")
                }
            }
        }
    }

    private fun selectCamera() {
        createImageFile()
        if (!mImageFile!!.exists()) {
            return
        }
        val cameraImageUri = initImageUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        startActivityForResult(cameraIntent, REQUEST_CAMERA)
    }


    private fun initImageUri(): Uri {
        return if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                this,
                "com.lfork.phonelimitadvanced.fileprovider", mImageFile!!
            )
        } else {
            Uri.fromFile(mImageFile)
        }
    }


//    private fun cropImage(uri: Uri) {
//        val intent = Intent("com.android.camera.action.CROP")
//        intent.setDataAndType(uri, IMAGE_UNSPECIFIED)
//        intent.putExtra("crop", "true")
//        intent.putExtra("aspectX", 1)
//        intent.putExtra("aspectY", 1)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile))
//        startActivityForResult(intent, REQUEST_CROP)
//    }

    private fun createImageFile() {
        mImageFile = File(getExternalFilesDir(null), System.currentTimeMillis().toString() + ".jpg")
        try {
            mImageFile!!.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun Context.toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
