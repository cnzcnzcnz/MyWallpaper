package com.abstraksi.kimetsunoyaiba.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.os.Environment
import android.text.format.DateFormat
import android.util.DisplayMetrics
import com.yalantis.ucrop.view.CropImageView
import com.abstraksi.kimetsunoyaiba.R
import timber.log.Timber
import java.io.*
import java.util.*


object Utils {

    fun checkConnection(context: Context): Boolean {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
    }

    fun copyTempPhoto(file: File?, newFile: File): String? {
        return copy(file, File(newFile, PHOTO_TEMP))
    }

    private fun copy(file: File?, file2: File): String? {
        val fileInputStream = FileInputStream(file)
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file2)
            val bArr = ByteArray(1024)
            while (true) {
                val read: Int = fileInputStream.read(bArr)
                if (read > 0) {
                    fileOutputStream.write(bArr, 0, read)
                } else {
                    fileOutputStream.close()
                    fileInputStream.close()
                    return file2.absolutePath
                }
            }
        } catch (th: Throwable) {
            fileInputStream.close()
            throw IllegalArgumentException(th)
        }
    }

    fun readFileToByte(context: Context?, file: File?): ByteArray? {
        return try {
            val inputStream: InputStream = FileInputStream(file)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            buffer
        } catch (e: IOException) {
            Timber.e(e.localizedMessage)
            null
        }
    }

    fun savePhotoToStorage(context: Context, file: File): String {
        val rootPath = Environment.getExternalStorageDirectory().path
        val dir = File("$rootPath/Pictures/${context.getString(R.string.app_name)}")
        if (!dir.exists()){
            dir.mkdirs()
        }

        val fileNameFormat = context.getString(R.string.app_name) + DateFormat.format("yyyy_MM_dd_hh_mm_ss", Date()).toString()+".jpg"
        val file2 = File(dir, fileNameFormat)
        copy(file, file2)
        return file2.path
    }

    private fun getResizedBitmap(bitmap: Bitmap, i: Int, i2: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val f = i.toFloat() / width.toFloat()
        val f2 = i2.toFloat() / height.toFloat()
        val matrix = Matrix()
        matrix.postScale(f, f2)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
    }

    fun getWallpaperBitmap(context: Context, bitmap: Bitmap): Bitmap {
        val wallpaperBitmap: Bitmap = getResizedBitmap(
                bitmap,
                DisplayMetrics().getWidth(context),
                DisplayMetrics().getHeight(context)
        )

        val createBitmap = Bitmap.createBitmap(
                DisplayMetrics().getWidth(context),
                DisplayMetrics().getHeight(context),
                Bitmap.Config.ARGB_8888
        )

        Canvas(createBitmap).drawBitmap(
                wallpaperBitmap,
                CropImageView.DEFAULT_ASPECT_RATIO,
                CropImageView.DEFAULT_ASPECT_RATIO,
                null
        )
        return createBitmap
    }

}