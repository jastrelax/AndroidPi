package cn.androidpi.data.remote.dto

import cn.androidpi.news.entity.News
import com.google.gson.annotations.SerializedName

/**
 * Created by jastrelax on 2017/11/2.
 */

class ResNews {

    var _id: IdBean? = null
    var category: String? = null
    @SerializedName("origin_title")
    var originTitle: String? = null
    @SerializedName("publish_time")
    var publishTime: String? = null
    @SerializedName("source_name")
    var sourceName: String? = null
    @SerializedName("source_url")
    var sourceUrl: String? = null
    var title: String? = null
    var url: String? = null
    var keywords: List<String>? = null

    class IdBean {

        @SerializedName("\$oid")
        var oid: String? = null
    }

    fun toNews(): News {

        var news = News()
        news.newsId = _id?.oid
        news.category = category
        news.originTitle = originTitle
        news.publishTime = publishTime
        news.sourceName = sourceName
        news.sourceUrl = sourceUrl
        news.url = url
        news.keywords = keywords?.toTypedArray()

        return news
    }

    override fun toString(): String {
        return "ResNews(_id=$_id, category=$category, originTitle=$originTitle, publishTime=$publishTime, sourceName=$sourceName, sourceUrl=$sourceUrl, title=$title, url=$url, keywords=$keywords)"
    }


}
