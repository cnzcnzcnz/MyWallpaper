package com.abstraksi.btswallpaperhd2020.presentation.main.item

import android.widget.Button
import com.bumptech.glide.Glide
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.abstraksi.btswallpaperhd2020.R
import com.abstraksi.btswallpaperhd2020.utils.gone
import com.abstraksi.btswallpaperhd2020.utils.invis
import com.abstraksi.btswallpaperhd2020.utils.visible
import kotlinx.android.synthetic.main.item_row_native_ad.view.*

class NativeAdItem(private val nativeAd: UnifiedNativeAd): Item(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            val adView = itemView.unified_nativead

            adView.mediaView = itemView.ad_media
//            adView.headlineView = itemView.ad_headline
//            adView.bodyView = itemView.ad_body

            adView.callToActionView = itemView.ad_call_to_action
            adView.iconView = itemView.ad_app_icon
//            adView.advertiserView = itemView.ad_advertiser

//            if (nativeAd.body == null){
//                adView.bodyView.gone()
//            } else {
//                adView.bodyView.invis()
//                (adView.bodyView as TextView).text = nativeAd.body
//            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView.invis()
            } else {
                adView.callToActionView.visible()
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView.gone()
            } else {
                Glide.with(itemView.context)
                    .load(nativeAd.icon.drawable)
                    .into(itemView.ad_app_icon)
            }

//            if (nativeAd.icon == null) {
//                adView.iconView.gone()
//            } else {
//                (adView.iconView as ImageView).setImageDrawable(
//                    nativeAd.icon.drawable
//                )
//                adView.iconView.visible()
//            }

//            if (nativeAd.advertiser == null) {
//                adView.advertiserView.invis()
//            } else {
//                (adView.advertiserView as TextView).text = nativeAd.advertiser
//                adView.advertiserView.visible()
//            }

            adView.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            val vc = nativeAd.videoController

            // Updates the UI to say whether or not this ad has a video asset.

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {
//            videoStatus.setText(String.format(Locale.getDefault(),
//                    "Video status: Ad contains a %.2f:1 video asset.",
//                    vc.getAspectRatio()));

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                        //                    refresh.setEnabled(true);
                        //                    videoStatus.setText("Video status: Video playback has ended.");
                        super.onVideoEnd()
                    }
                }
            }
//        else {
//            videoStatus.setText("Video status: Ad does not contain a video asset.");
//            refresh.setEnabled(true);
//        }

//            if (nativeAd.callToAction != null) {
//                (adView.callToActionView as Button).text = nativeAd.callToAction
//            }

//            if (nativeAd.icon == null) {
//                adView.iconView.visibility = View.GONE
//            } else {
//                val sdk = Build.VERSION.SDK_INT
//                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
//                    itemView.ad_app_icon.setBackgroundDrawable(nativeAd.icon.drawable)
//                } else {
//                    itemView.ad_app_icon.background = nativeAd.icon.drawable
//                }
//            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.item_row_native_ad
    }

}