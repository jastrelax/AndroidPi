package com.androidpi.news.vo

import com.androidpi.common.datetime.DateTimeUtils
import com.androidpi.news.entity.News
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by jastrelax on 2017/11/28.
 */
class CoverNews(var mNews: MutableList<News>) {

    companion object {
        fun newInstance(newsList: List<News>) : CoverNews? {
            val now = Calendar.getInstance(Locale.CHINA).time
            val validCoverNews = ArrayList<News>()
            if (newsList.isEmpty()) return null
            for (news in newsList) {
                if (news.publishedAt == null)
                    continue
                val time = DateTimeUtils.parseDateTime(news.publishedAt!!)
                if (time > now)
                    continue
                validCoverNews.add(news)
            }
            return if (validCoverNews.isEmpty()) null else CoverNews(validCoverNews)
        }
    }
}