package com.abstraksi.kimetsunoyaiba.presentation.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.abstraksi.kimetsunoyaiba.R
import com.abstraksi.kimetsunoyaiba.presentation.base.BaseActivity
import com.abstraksi.kimetsunoyaiba.presentation.main.item.PhotoItem
import com.abstraksi.kimetsunoyaiba.presentation.state.UiState
import com.abstraksi.kimetsunoyaiba.presentation.viewmodel.PhotoViewModel
import com.abstraksi.kimetsunoyaiba.utils.*
import com.abstraksi.kimetsunoyaiba.utils.listener.OnAppItemClickListener
import kotlinx.android.synthetic.main.activity_favorite.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteActivity : BaseActivity() {

    private val photoVM: PhotoViewModel by viewModel()
    private val groupAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()

    override fun getLayoutResId() = R.layout.activity_favorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRv()

        btnBack.setOnClickListener {
            onBackPressed()
        }

        photoVM.favPhotos.observe(this, Observer { state ->
        when(state) {
            is UiState.Loading -> {

            }
            is UiState.Success -> {
                groupAdapter.clear()
                if (state.data.isEmpty()){
                    txtEmpty.visible()
                } else {
                    txtEmpty.gone()
                    state.data.forEach {
                        groupAdapter.add(PhotoItem(it){
                            showInterstitialAd(object : OnAppItemClickListener {
                                override fun onItemClicked() {
                                    startActivity<PhotoViewActivity>(
                                            PHOTO_EXTRA to it,
                                            ALBUM_EXTRA to it.album
                                    )
                                }
                            })
                        })
                    }
                }
            }
            is UiState.Error -> {

            }
        } })

        photoVM.findFavPhotos()

    }

    private fun initRv(){
        rvPhoto.apply {
            val stagLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            groupAdapter.setHasStableIds(true)
            rvPhoto.apply {
                layoutManager = stagLayoutManager
                stagLayoutManager.gapStrategy =
                        StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                addItemDecoration(ItemOffsetDecoration(context, R.dimen.grid_margin))
                itemAnimator = null
                adapter = groupAdapter
            }
        }
    }
}