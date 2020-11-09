package com.abstraksi.kimetsunoyaiba.presentation.main

import android.app.*
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.facebook.ads.NativeAdLayout
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.abstraksi.kimetsunoyaiba.R
import com.abstraksi.kimetsunoyaiba.config.*
import com.abstraksi.kimetsunoyaiba.presentation.base.BaseActivity
import com.abstraksi.kimetsunoyaiba.presentation.main.fragment.PhotoListFragment
import com.abstraksi.kimetsunoyaiba.utils.gone
import com.abstraksi.kimetsunoyaiba.utils.visible
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject


class HomeActivity : BaseActivity() {

    private val adsHelper: AdsHelper by inject()

    private var progressDialog: ProgressDialog? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdsHelper.loadNative(this)
        setSupportActionBar(toolbar)
        setupDrawer()
        setupTab()

        FirebaseMessaging.getInstance().subscribeToTopic(APP_FCM_TOPIC)
        adsHelper.checkGDPR(this){}

    }

    private fun setupTab(){
//        pager.isUserInputEnabled = false
        pager.offscreenPageLimit = 1
        pager.adapter = PhotoListPagerAdapter(this)

        if (ALBUM.size == 1) tab_layout.gone()

        TabLayoutMediator(tab_layout, pager) { tab, position ->
            tab.text = ALBUM[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (SHOW_PRIVACY_POLICY) menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionPrivacyPolicy){
            startActivity<PrivacyPolicyActivity>()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawer() {
        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_drawer)

        // Tie DrawerLayout events to the ActionBarToggle
        drawer_layout.addDrawerListener(drawerToggle)

        home.setOnClickListener {
            drawer_layout.closeDrawer(Gravity.LEFT)
        }

        btnInfo.setOnClickListener {
            MaterialDialog(this).show {
                customView(R.layout.dialog_disclaimer, noVerticalPadding = true)
            }
        }

        btnRating.setOnClickListener {
            startActivity<FavoriteActivity>()
        }

        btnShare.setOnClickListener {
            val link = "http://play.google.com/store/apps/details?id=$packageName"
            val msg = "${getString(R.string.share_message)} $link"
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg)
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, "Share to"))
        }

        btnMoreApps.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_app_link))))
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        } else if (!AdsHelper.isIntersShow){
            prepareDialogExit()
        } else {
            super.onBackPressed()
        }
    }

    private inner class PhotoListPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = ALBUM.size

        override fun createFragment(position: Int): Fragment =
            PhotoListFragment.newInstance(ALBUM[position])
    }

    private fun prepareDialogExit() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage("Process exit. Please wait...")
            progressDialog?.setCancelable(false)
            progressDialog?.show()
            showDialogExit()
        } else {
            Toast.makeText(this, "Please Wait. Process to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialogExit() {
        val appName = resources.getString(R.string.app_name)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_rate)
        dialog.setTitle(appName)

        var counter = 0

        fun showNativeAdmob(){
            val unifiedNativeAdView: UnifiedNativeAdView = dialog.findViewById(R.id.layout_native)
            if (AdsHelper.mNativeAds.size != 0) {
                unifiedNativeAdView.visible()
                adsHelper.populateUnifiedNativeAdViewBig(AdsHelper.mNativeAds.random(), unifiedNativeAdView)
            }
        }

        fun showNativeFan(){
            if (!AdsHelper.nativeAd!!.isAdLoaded){
                showNativeAdmob()
                return
            }
            if (AdsHelper.nativeAd!!.isAdInvalidated){
                showNativeAdmob()
                return
            }

            val fanNative: NativeAdLayout = dialog.findViewById(R.id.layout_native_fan)
            fanNative.visible()
            adsHelper.populateNativeFan(AdsHelper.nativeAd!!, fanNative)
        }

        val linearAds = dialog.findViewById<LinearLayout>(R.id.linearAds)
        if (AdsUtils.ADS_PRIMARY_SELECTED == AdsUtils.ADMOB){
            if (AdsHelper.mNativeAds.isNotEmpty()) {
                showNativeAdmob()
            } else {
                if (counter == 0){
                    AdsHelper.nativeAd?.let { showNativeFan() }
                    counter += 1
                } else {
                    linearAds.visibility = View.GONE
                }

            }
        } else {
            if (AdsHelper.nativeAd != null) {
                showNativeFan()
            } else {
                if (counter == 0){
                    if (AdsHelper.mNativeAds.isNotEmpty()) { showNativeAdmob() }
                    counter += 1
                } else {
                    linearAds.visibility = View.GONE
                }
            }
        }

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        // Button rateButton = (Button) dialog.findViewById(R.id.rate);
        val later = dialog.findViewById<View>(R.id.feedback) as Button
        val rateNow = dialog.findViewById<View>(R.id.moreapp) as Button
        // if button is clicked, close the custom dialog
        rateNow.setOnClickListener { rateApp() }

        later.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        if (progressDialog != null) {
            progressDialog?.dismiss()
        }
        progressDialog = null
        dialog.show()


    }

    private fun rateApp(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }

}