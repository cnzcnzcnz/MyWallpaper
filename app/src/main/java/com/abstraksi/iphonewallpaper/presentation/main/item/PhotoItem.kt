package com.abstraksi.iphonewallpaper.presentation.main.item

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_row_photo.view.*
import com.abstraksi.iphonewallpaper.R
import com.abstraksi.iphonewallpaper.domain.Photo
import com.abstraksi.iphonewallpaper.utils.loadImageFromUrl

class PhotoItem(
        val photo: Photo,
        val listener: (Photo) -> Unit
) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            itemView.photo.loadImageFromUrl(photo.thumbnail)
            itemView.setOnClickListener {
                listener(photo)
            }
        }
    }

    override fun getLayout() = R.layout.item_row_photo
}