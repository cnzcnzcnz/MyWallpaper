package com.abstraksi.quotes.presentation.main.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.stfalcon.imageviewer.StfalconImageViewer
import com.abstraksi.quotes.R
import kotlinx.android.synthetic.main.fragment_photo_view.*
import com.abstraksi.quotes.domain.Photo
import com.abstraksi.quotes.presentation.main.PhotoViewActivity
import com.abstraksi.quotes.utils.PHOTO_EXTRA
import com.abstraksi.quotes.utils.gone
import com.abstraksi.quotes.utils.listener.OnAppItemClickListener


class PhotoViewFragment : Fragment() {

    private var photo: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photo = it.getParcelable(PHOTO_EXTRA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photo?.let {
            Glide.with(requireContext())
                .load(photo?.path)
                .apply(RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false;
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        cardLoading.gone()
                        containerPhoto.setBackgroundResource(R.drawable.bg_photo)

                        return false;
                    }

                })
                .into(imgPhoto)
        }

        containerPhoto?.setOnClickListener {
            (requireActivity() as PhotoViewActivity).showInterstitialAd(object : OnAppItemClickListener {
                override fun onItemClicked() {
                    StfalconImageViewer.Builder<String>(
                        requireContext(),
                        listOf(photo?.path)
                    ) { view, image ->
                        Glide.with(requireContext())
                            .load(image).into(view)
                    }.show()
                }

            })
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(photo: Photo) =
            PhotoViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(PHOTO_EXTRA, photo)
                }
            }
    }
}