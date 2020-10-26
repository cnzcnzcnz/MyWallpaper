package com.abstraksi.gambarpemainbola.presentation.main.item

import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.ads.AdOptionsView
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAd
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.abstraksi.gambarpemainbola.R
import kotlinx.android.synthetic.main.layout_fan_native_container.view.*


class FanNativeItem(val nativeAd: NativeAd): Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
            val adView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.item_row_native_fb, itemView.native_ad_container, false) as LinearLayout
            itemView.native_ad_container.addView(adView)

            // Add the AdOptionsView
            val adChoicesContainer: LinearLayout = adView.findViewById(R.id.ad_choices_container)
            val adOptionsView = AdOptionsView(itemView.context, nativeAd, itemView.native_ad_container)
            adChoicesContainer.removeAllViews()
            adChoicesContainer.addView(adOptionsView, 0)

            // Create native UI using the ad metadata.

            // Create native UI using the ad metadata.
//            val nativeAdIcon: AdIconView = adView.findViewById(R.id.native_ad_icon)
            val nativeAdTitle: TextView = adView.findViewById(R.id.native_ad_title)
            val nativeAdMedia: MediaView = adView.findViewById(R.id.native_ad_media)
            val nativeAdSocialContext: TextView = adView.findViewById(R.id.native_ad_social_context)
            val nativeAdBody: TextView = adView.findViewById(R.id.native_ad_body)
            val sponsoredLabel: TextView = adView.findViewById(R.id.native_ad_sponsored_label)
            val nativeAdCallToAction: Button = adView.findViewById(R.id.native_ad_call_to_action)

            // Set the Text.

            // Set the Text.
            nativeAdTitle.text = nativeAd.advertiserName
            nativeAdBody.text = nativeAd.adBodyText
            nativeAdSocialContext.text = nativeAd.adSocialContext
            nativeAdCallToAction.setVisibility(if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE)
            nativeAdCallToAction.setText(nativeAd.adCallToAction)
            sponsoredLabel.text = nativeAd.sponsoredTranslation

            // Create a list of clickable views

            // Create a list of clickable views
            val clickableViews: MutableList<View> = ArrayList()
            clickableViews.add(nativeAdTitle)
            clickableViews.add(nativeAdCallToAction)

            // Register the Title and CTA button to listen for clicks.

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
//                nativeAdIcon,
                clickableViews
            )
        }
    }

    override fun getLayout() = R.layout.layout_fan_native_container
}