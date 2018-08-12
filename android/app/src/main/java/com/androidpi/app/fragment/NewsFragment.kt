package com.androidpi.app.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidpi.app.R
import com.androidpi.app.base.BaseFragment
import com.androidpi.app.base.BindLayout
import com.androidpi.app.base.RecyclerAdapter
import com.androidpi.app.buiness.view.NewsView
import com.androidpi.app.buiness.viewmodel.NewsViewModel
import com.androidpi.app.databinding.FragmentNewsBinding
import com.androidpi.app.viewholder.ErrorViewHolder
import com.androidpi.app.viewholder.NewsViewHolder
import com.androidpi.app.viewholder.items.ErrorItem
import com.androidpi.base.widget.literefresh.*
import com.androidpi.news.model.NewsListModel.Companion.PAGE_SIZE
import com.androidpi.news.vo.NewsPagination
import javax.inject.Inject

/**
 * Created by jastrelax on 2017/11/7.
 */
@BindLayout(R.layout.fragment_news)
class NewsFragment : BaseFragment<FragmentNewsBinding>(), NewsView {

    lateinit var mNewsModel: NewsViewModel

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    lateinit var mAdapter: RecyclerAdapter

    var mNewsCategory: String? = null

    companion object {

        val KEY_CATEGORY = "com.androidpi.app.fragment.NewsFragment.KEY_PORTAL"

        fun newInstance(): NewsFragment {
            return NewsFragment()
        }

        fun newInstance(category: String?): NewsFragment {
            val args = Bundle()
            args.putString(KEY_CATEGORY, category)
            val fragment = NewsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If retainInstance is set to be true, on configuration change,
        // onCreate will not be called, therefore [NewsViewModel] will not be recreated.
        mNewsModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(NewsViewModel::class.java)
        mNewsCategory = arguments?.getString(KEY_CATEGORY)
        mNewsModel.mCategory = mNewsCategory

        mNewsModel.mNews.observe(this, Observer { t ->
            refreshFinished()
            if (t == null) return@Observer

            if (t.isSuccess) {
                if (t.data == null) {
                    mAdapter.setPayloads(ErrorItem("数据为空"))
                    return@Observer
                }
                if ((t.data as NewsPagination).isFirstPage()) {
                    mAdapter.setPayloads(t.data?.newsList)
                } else {
                    mAdapter.addPayloads(t.data?.newsList)
                }
            } else if (t.isError) {
                mAdapter.setPayloads(ErrorItem("加载失败"))
            } else if (t.isLoading) {

            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // If retainInstance is set to be true, on configuration change,
        // View state will be restored,
        // thus the referenced obsolete activity context should not be used any more,
        // otherwise a memory leak may occur.
        mBinding.recyclerNews.layoutManager = LinearLayoutManager(context)
        mBinding.recyclerNews.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        mAdapter = RecyclerAdapter()
        mAdapter.setFragmentManager(childFragmentManager)
        mAdapter.register(NewsViewHolder::class.java,
                ErrorViewHolder::class.java)
        mBinding.recyclerNews.adapter = mAdapter

        //
        val lpHeader = mBinding.scrollHeader.layoutParams as CoordinatorLayout.LayoutParams
        val headerBehavior = RefreshHeaderBehavior<View>()
        headerBehavior.addOnPullingListener(object : OnPullingListener {

            override fun onStartPulling(max: Int) {
                mBinding.pullingProgress.visibility = View.VISIBLE
                mBinding.pullingProgress.max = max
            }

            override fun onPulling(current: Int, delta: Int, max: Int) {
                mBinding.pullingProgress.setProgress(current)
            }

            override fun onStopPulling(current: Int, max: Int) {
                mBinding.pullingProgress.visibility = View.GONE
            }

        })
        headerBehavior.addOnRefreshListener(object : OnRefreshListener {

            override fun onRefreshStart() {
            }

            override fun onRefreshReady() {
            }

            override fun onRefresh() {
                loadFirstPage()
            }

            override fun onRefreshComplete() {
            }
        })
        lpHeader.behavior = headerBehavior
        mBinding.scrollHeader.layoutParams = lpHeader

        val lpFooter = mBinding.scrollFooter.layoutParams as CoordinatorLayout.LayoutParams
        val footerBehavior = RefreshFooterBehavior<View>(context)
        footerBehavior.addOnRefreshListener(object : OnRefreshListener {

            override fun onRefreshStart() {
            }

            override fun onRefreshReady() {
            }

            override fun onRefresh() {
                loadNextPage()
            }

            override fun onRefreshComplete() {
            }
        })

        footerBehavior.addOnPullingListener(object : OnPullingListener {
            override fun onPulling(current: Int, delta: Int, max: Int) {
            }

            override fun onStartPulling(max: Int) {

            }

            override fun onStopPulling(current: Int, max: Int) {
            }
        })

        lpFooter.behavior = footerBehavior
        mBinding.scrollFooter.layoutParams = lpFooter

        val lpScroll = mBinding.recyclerNews.layoutParams as CoordinatorLayout.LayoutParams
        lpScroll.behavior = RefreshContentBehavior<View>()
        mBinding.recyclerNews.layoutParams = lpScroll

        // pull up to load more
        mBinding.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            val THRESHOULD = PAGE_SIZE / 2

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy == 0 || footerBehavior.isRefreshing)
                    return
                val lastVisibleItem = (recyclerView?.layoutManager as LinearLayoutManager)
                        .findLastVisibleItemPosition()
                val totalItemCount = recyclerView.layoutManager.itemCount
                if (totalItemCount <= lastVisibleItem + THRESHOULD) {
//                    footerBehavior.refresh()
                }
            }
        })

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (null == savedInstanceState || mNewsModel.mNews.value == null) {
            val lpHeader = mBinding.scrollHeader.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = lpHeader.behavior as RefreshHeaderBehavior
            behavior.refresh()
        }
    }

    fun refreshFinished() {
        val lpHeader = mBinding.scrollHeader.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = lpHeader.behavior as RefreshHeaderBehavior
        behavior.refreshComplete()

        val lpFooter = mBinding.scrollFooter.layoutParams as CoordinatorLayout.LayoutParams
        val footerBehavior = lpFooter.behavior as RefreshFooterBehavior
        footerBehavior.refreshComplete()
    }

    override fun loadFirstPage() {
        mNewsModel.refreshPage()
    }

    override fun loadNextPage() {
        mNewsModel.nextPage()
    }
}