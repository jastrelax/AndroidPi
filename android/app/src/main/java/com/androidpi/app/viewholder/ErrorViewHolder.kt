package com.androidpi.app.viewholder

import android.view.View
import com.androidpi.app.R
import com.androidpi.app.base.ui.BaseViewHolder
import com.androidpi.app.base.ui.BindLayout
import com.androidpi.app.databinding.ViewHolderErrorBinding
import com.androidpi.app.viewholder.items.ErrorItem

/**
 * Created by jastrelax on 2017/12/7.
 */
@BindLayout(value = R.layout.view_holder_error, dataTypes = arrayOf(ErrorItem::class))
class ErrorViewHolder(itemView: View) : BaseViewHolder<ViewHolderErrorBinding>(itemView) {

    override fun <T : Any?> onBind(data: T, position: Int) {
        if (data is ErrorItem) {
            binding.tvMessage.text = data.message
        }
    }
}