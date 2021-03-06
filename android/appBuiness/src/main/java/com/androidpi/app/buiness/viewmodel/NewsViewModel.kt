package com.androidpi.app.buiness.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.androidpi.app.base.vm.vo.Resource
import com.androidpi.news.entity.News
import com.androidpi.news.repo.NewsRepo
import com.androidpi.news.model.NewsListModel
import com.androidpi.news.vo.NewsPagination
import dagger.Lazy
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by jastrelax on 2017/11/7.
 */
class NewsViewModel @Inject constructor() : ViewModel(), NewsListModel {

    @Inject
    lateinit var mNewsRepo: Lazy<NewsRepo>

    val mNews: MutableLiveData<Resource<NewsPagination>> = MutableLiveData()

    var mCategory: String? = null

    var page = NewsPagination.FIRST_PAGE

    fun getLatestNews(isNext: Boolean, count: Int = NewsListModel.PAGE_SIZE) {
        val page = if(isNext) this.page + 1 else NewsPagination.FIRST_PAGE
        this.page = page
        Timber.d("getLatestedNews $page")
        mNewsRepo.get().refreshNews(page, count, mCategory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<News>> {

                    override fun onSuccess(t: List<News>) {
                        if (t.isEmpty()) {
                            mNews.value = Resource.error(null, NewsPagination(page, t), Exception("No more data."))
                        } else {
                            mNews.value = Resource.success(NewsPagination(page, t))
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        mNews.value = Resource.loading()
                    }

                    override fun onError(e: Throwable) {
                        mNews.value = Resource.error(null, NewsPagination(page, null), e)
                    }
                })
    }

    override fun refreshPage() {
        getLatestNews(false)
    }

    override fun nextPage() {
        getLatestNews(true)
    }
}
