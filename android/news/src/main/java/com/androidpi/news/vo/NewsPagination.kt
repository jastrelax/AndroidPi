package com.androidpi.news.vo

import com.androidpi.news.entity.News

/**
 * Created by jastrelax on 2017/12/16.
 */
class NewsPagination() {

    var lastCachedPageNum: String? = null

    var newsList: MutableList<News> = mutableListOf()

    var page: Int? = null

    var nextPage: Int? = null

    var offset: Int = 0

    constructor(page: Int, nextPage: Int, list: List<News>): this() {
        this.page = page
        this.nextPage = nextPage
        this.newsList.addAll(list)
    }
}