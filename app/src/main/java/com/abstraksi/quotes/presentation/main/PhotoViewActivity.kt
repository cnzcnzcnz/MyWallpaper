package com.abstraksi.quotes.presentation.main

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ajalt.timberkt.Timber
import com.google.android.material.snackbar.Snackbar
import com.stfalcon.imageviewer.StfalconImageViewer
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import com.abstraksi.quotes.R
import com.abstraksi.quotes.data.pref.PreferencesHelper
import com.abstraksi.quotes.domain.Photo
import com.abstraksi.quotes.presentation.base.BaseActivity
import com.abstraksi.quotes.presentation.main.fragment.PhotoViewFragment
import com.abstraksi.quotes.presentation.state.UiState
import com.abstraksi.quotes.presentation.viewmodel.PhotoViewModel
import com.abstraksi.quotes.service.MaterialLiveWallpaperService
import com.abstraksi.quotes.utils.*
import com.abstraksi.quotes.utils.Utils.copyTempPhoto
import com.abstraksi.quotes.utils.Utils.getWallpaperBitmap
import com.abstraksi.quotes.utils.listener.OnAppItemClickListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_photo_view.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException

class PhotoViewActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    private val photoVM: PhotoViewModel by viewModel()
    private val prefHelper: PreferencesHelper by inject()
    private var currentPhoto: Photo? = null
    private var album: String? = null
    private var photoList = mutableListOf<Photo>()

    private val disposables = CompositeDisposable()

    private val RC_STORAGE_PERM = 110

    private var isFav = false

    override fun getLayoutResId() = R.layout.activity_photo_view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentPhoto = intent.getParcelableExtra(PHOTO_EXTRA)
        album = intent.getStringExtra(ALBUM_EXTRA)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        btnSavePhoto.setOnClickListener {
            checkStoragePermission()
        }

        btnSetWallpaper.setOnClickListener {
            Glide.with(this)
                    .downloadOnly()
                    .load(currentPhoto!!.path)
                    .into(object : CustomTarget<File>() {
                        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                            disposables.add(Observable.fromCallable<String> {
                                copyTempPhoto(resource, this@PhotoViewActivity.filesDir).toString()
                            }
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({
                                        if (currentPhoto!!.path.endsWith(".gif")){
                                            prefHelper.saveString(PreferencesHelper.WALLPAPER_PATH, it)
                                            openIntentWallpaper()
                                        } else {
                                            startCrop(Uri.parse("file://${it}"))
                                        }
                                    }, {
                                        throw IllegalArgumentException(it)
                                    })
                            )
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
        }

        btnShare.setOnClickListener {
            sharePhoto()
        }

        btnFullscreen.setOnClickListener { view ->
            showInterstitialAd(object : OnAppItemClickListener {
                override fun onItemClicked() {
                    StfalconImageViewer.Builder<String>(
                            this@PhotoViewActivity,
                            listOf(currentPhoto?.path)
                    ) { view, image ->
                        Glide.with(this@PhotoViewActivity)
                                .load(image).into(view)
                    }.show()
                }

            })
        }

        photoVM.photos.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    photoList.addAll(state.data.reversed())
                    initViewPager()
                }
                is UiState.Error -> {

                }
            }
        })

        photoVM.getPhotoLokal(album!!)

        if (!Utils.checkConnection(this)) {
            Snackbar.make(viewPager, getString(R.string.no_internet), Snackbar.LENGTH_LONG).show()
        }

        photoVM.favPhotos.observe(this, Observer { state ->
            when (state) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    if (state.data.isNotEmpty()) {
                        favPhoto()
                    } else {
                        unFavPhoto()
                    }
                }
                is UiState.Error -> {

                }
            }
        })

        currentPhoto?.let { findFavPhoto(it.photoId) }

        btnFav.setOnClickListener {
            currentPhoto?.let { photo ->
                if (isFav){
                    photoVM.deleteFav(photo){
                        unFavPhoto()
                        Snackbar.make(viewPager, getString(R.string.delete_favorite_message), Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    photoVM.insertFav(photo){
                        favPhoto()
                        Snackbar.make(viewPager, getString(R.string.save_favorite_message), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openIntentWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        try {
            wallpaperManager.clear()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                ComponentName(this, MaterialLiveWallpaperService::class.java))

        startActivity(intent)
    }

    private fun unFavPhoto(){
        isFav = false
        btnFav.setImageResource(R.drawable.ic_star_border)

    }

    private fun favPhoto(){
        isFav = true
        btnFav.setImageResource(R.drawable.ic_star)
    }

    private fun findFavPhoto(photoId: String){
        photoVM.findFavById(photoId)
    }

    private fun sharePhoto() {
        showLoading()
        Glide.with(this)
                .downloadOnly()
                .load(currentPhoto!!.path)
                .into(object : CustomTarget<File>() {
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        disposables.add(Observable.fromCallable<String> {
                            copyTempPhoto(resource, this@PhotoViewActivity.filesDir).toString()
                        }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    Looper.myLooper()?.let { it1 ->
                                        Handler(it1).postDelayed({
                                            dismissLoading()
                                            val authority = "$packageName.fileprovider"
                                            val imageUri = FileProvider.getUriForFile(
                                                    this@PhotoViewActivity,
                                                    authority, File(it)
                                            )

                                            val share = Intent(Intent.ACTION_SEND)
                                            share.type = "image/*"
                                            share.putExtra(Intent.EXTRA_STREAM, imageUri)
                                            startActivity(Intent.createChooser(share, getString(R.string.share_photo)))
                                        }, 1000)
                                    }
                                }, {
                                    throw IllegalArgumentException(it)
                                })
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    private fun savePhoto() {
        showLoading(getString(R.string.saving_photo))
        Glide.with(this)
                .downloadOnly()
                .load(currentPhoto!!.path)
                .into(object : CustomTarget<File>() {
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        disposables.add(Observable.fromCallable<String> {
                            Utils.savePhotoToStorage(this@PhotoViewActivity, resource)
                        }
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    Looper.myLooper()?.let { it1 ->
                                        Handler(it1).postDelayed({
                                            dismissLoading()
                                            sendBroadcast(
                                                    Intent(
                                                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                                            Uri.parse("file://$it")
                                                    )
                                            )
                                            Snackbar.make(viewPager, "Photo saved in ${getString(R.string.app_name)} Folder", Snackbar.LENGTH_LONG).show()
                                            showInterstitialAd()
                                        }, 1000)
                                    }
                                }, {
                                    throw IllegalArgumentException(it)
                                })
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

    }

    private fun initViewPager() {
        val pagerAdapter = PhotoViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                isFav = false
                currentPhoto = photoList[position]
                currentPhoto?.let {
                    findFavPhoto(it.photoId)
                }

            }
        })

        viewPager.setCurrentItem(
                photoList.indexOfFirst { it.path == currentPhoto?.path },
                false
        )


    }

    private fun advancedConfig(uCrop: UCrop): UCrop {
        val options = UCrop.Options()
        val width: Int = DisplayMetrics().getWidth(this)
        val height: Int = DisplayMetrics().getHeight(this)
        options.setAspectRatioOptions(
                2,
                AspectRatio("1:1", 1.0f, 1.0f),
                AspectRatio("1:2", 2.0f, 1.0f),
                AspectRatio("ORIGINAL", width.toFloat(), height.toFloat()),
                AspectRatio("3:4", 4.0f, 3.0f),
                AspectRatio("9:16", 16.0f, 9.0f)
        )
        return uCrop.withOptions(options)
    }

    fun startCrop(uri: Uri) {
        advancedConfig(
                UCrop.of(
                        uri,
                        Uri.fromFile(
                                File(
                                        this.filesDir,
                                        PHOTO_TEMP
                                )
                        )
                )
        ).start(this)
    }

    private fun handleCropError(@NonNull intent: Intent) {
        val message: CharSequence?
        val i: Int
        val error = UCrop.getError(intent)
        if (error != null) {
            message = error.message
            i = 1
        } else {
            message = "Something wrong when cropped an image."
            i = 0
        }
        Toast.makeText(this, message, i).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val resultUri = UCrop.getOutput(it)
                resultUri?.let {
                    if (Build.VERSION.SDK_INT < 24) {
                        setWallpaper(WallpaperType.HOME, resultUri)
                    } else {
                        val dialog = MaterialDialog(this)
                                .customView(R.layout.dialog_set_wallpaper, scrollable = false, noVerticalPadding = true)

                        val customView = dialog.getCustomView()
                        val btnHome = customView.findViewById<LinearLayout>(R.id.btnSetHomeScreen)
                        val btnLock = customView.findViewById<LinearLayout>(R.id.btnSetLockScreen)
                        val btnBoth = customView.findViewById<LinearLayout>(R.id.btnBoth)

                        btnHome.setOnClickListener {
                            setWallpaper(WallpaperType.HOME, resultUri)
                            dialog.dismiss()
                        }
                        btnLock.setOnClickListener {
                            setWallpaper(WallpaperType.LOCK, resultUri)
                            dialog.dismiss()
                        }
                        btnBoth.setOnClickListener {
                            setWallpaper(WallpaperType.BOTH, resultUri)
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            data?.let { handleCropError(it) }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setWallpaper(wallpaperType: WallpaperType, uri: Uri) {
        showLoading(msg = getString(R.string.setting_wallpaper))
        disposables.add(Observable.fromCallable {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val wallpaperManager = WallpaperManager.getInstance(this)
                if (Build.VERSION.SDK_INT < 24) {
                    wallpaperManager.setBitmap(bitmap)
                } else {
                    when (wallpaperType) {
                        WallpaperType.HOME -> wallpaperManager.setBitmap(
                                getWallpaperBitmap(
                                        this,
                                        bitmap
                                ), null, true, WallpaperManager.FLAG_SYSTEM
                        )
                        WallpaperType.BOTH -> wallpaperManager.setBitmap(
                                getWallpaperBitmap(
                                        this,
                                        bitmap
                                )
                        )
                        WallpaperType.LOCK -> wallpaperManager.setBitmap(
                                getWallpaperBitmap(
                                        this,
                                        bitmap
                                ), null, true, WallpaperManager.FLAG_LOCK
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                toast(e.localizedMessage)
            }
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Snackbar.make(viewPager, getString(R.string.set_wallpaper_success), Snackbar.LENGTH_LONG).show()
                    dismissLoading()
                    showInterstitialAd()
                }, {
                    Timber.e(it)
                }))
    }

    private fun showInterstitialAd() {
        showInterstitialAd(object : OnAppItemClickListener {
            override fun onItemClicked() {
            }
        })
    }

    private inner class PhotoViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = photoList.size

        override fun createFragment(position: Int): Fragment =
                PhotoViewFragment.newInstance(photoList[position])
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun checkStoragePermission() {
        val perms = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(this, perms)) {
            savePhoto()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    RC_STORAGE_PERM, perms
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        savePhoto()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}

