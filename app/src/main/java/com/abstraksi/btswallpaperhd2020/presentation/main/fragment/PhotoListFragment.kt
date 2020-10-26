package com.abstraksi.btswallpaperhd2020.presentation.main.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.ajalt.timberkt.Timber
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.abstraksi.btswallpaperhd2020.R
import com.abstraksi.btswallpaperhd2020.config.AdsHelper
import com.abstraksi.btswallpaperhd2020.config.AdsUtils
import com.abstraksi.btswallpaperhd2020.domain.Photo
import com.abstraksi.btswallpaperhd2020.presentation.main.HomeActivity
import com.abstraksi.btswallpaperhd2020.presentation.viewmodel.PhotoViewModel
import com.abstraksi.btswallpaperhd2020.presentation.main.PhotoViewActivity
import com.abstraksi.btswallpaperhd2020.presentation.main.item.FanNativeItem
import com.abstraksi.btswallpaperhd2020.presentation.main.item.NativeAdItem
import com.abstraksi.btswallpaperhd2020.presentation.main.item.PhotoItem
import com.abstraksi.btswallpaperhd2020.presentation.state.UiState
import com.abstraksi.btswallpaperhd2020.utils.*
import com.abstraksi.btswallpaperhd2020.utils.listener.OnAppItemClickListener
import kotlinx.android.synthetic.main.fragment_photo_list.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.min

private const val ARG_ALBUM = "album"

class PhotoListFragment : Fragment() {

    private var photoList = mutableListOf<Photo>()
    private var album: String? = null
    private val vm: PhotoViewModel by viewModel()
    private val groupAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            album = it.getString(ARG_ALBUM)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()

        vm.photos.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UiState.Loading -> {
//                    spin_kit.visible()
                }

                is UiState.Success -> {
                    if (state.data.isNotEmpty()) spin_kit.gone()
                    photoList.clear()
                    photoList.addAll(state.data.reversed())
                    populateDate(getChunkedList(0))
                }
                is UiState.Error -> {
                    spin_kit.gone()
                    Timber.e(state.throwable)
                }
            }
        })


    }

    override fun onResume() {
        super.onResume()
        album?.let {
            if (photoList.isEmpty()) {
                vm.getPhotoLokal(it)
            }
        }
    }

    private fun getChunkedList(page: Int): List<Photo> {
        val list = mutableListOf<Photo>()
        if (photoList.isNotEmpty()) {
            val paging = min(((page + 1) * 20) - 1, photoList.size - 1)
            for (i in page * 20..paging) {
                list.add(photoList[i])
            }
        }
        return list
    }

    private fun populateDate(data: List<Photo>) {
        data.forEach {
            groupAdapter.add(
                    PhotoItem(it) {
                        (requireActivity() as HomeActivity).showInterstitialAd(object : OnAppItemClickListener {
                            override fun onItemClicked() {
                                requireActivity().startActivity<PhotoViewActivity>(
                                        PHOTO_EXTRA to it,
                                        ALBUM_EXTRA to album
                                )
                            }
                        })

                    })
        }
        fun showFanNative(){
            AdsHelper.nativeAd?.let {
                if (!it.isAdLoaded) {
                    if (AdsHelper.mNativeAds.isNotEmpty()){
                        groupAdapter.add(NativeAdItem(AdsHelper.mNativeAds.random()))
                    }
                    return@let
                }
                if (it.isAdInvalidated) {
                    if (AdsHelper.mNativeAds.isNotEmpty()){
                        groupAdapter.add(NativeAdItem(AdsHelper.mNativeAds.random()))
                    }
                    return@let
                }
                groupAdapter.add(FanNativeItem(it))
            }
        }

        if (data.isNotEmpty()) {
            if (AdsUtils.ADS_PRIMARY_SELECTED == AdsUtils.ADMOB) {
                if (AdsHelper.mNativeAds.isNotEmpty()){
                    groupAdapter.add(NativeAdItem(AdsHelper.mNativeAds.random()))
                } else {
                    showFanNative()
                }
            } else {
                showFanNative()
            }
        }
    }


    private fun initRv() {
        val stagLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        groupAdapter.setHasStableIds(true)
        rvPhoto.apply {
            layoutManager = stagLayoutManager
            stagLayoutManager.gapStrategy =
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_margin))
            itemAnimator = null
            adapter = groupAdapter

            addOnScrollListener(object : EndlessRecyclerViewScrollListener(stagLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    spin_kit.visible()
                    Looper.myLooper()?.let {
                        Handler(it).postDelayed({
                            populateDate(getChunkedList(page))
                            spin_kit?.gone()
                        }, 2000)
                    }
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(album: String) =
                PhotoListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_ALBUM, album)
                    }
                }
    }
}