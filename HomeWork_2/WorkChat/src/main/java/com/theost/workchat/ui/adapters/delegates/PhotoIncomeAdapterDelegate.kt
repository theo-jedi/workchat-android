package com.theost.workchat.ui.adapters.delegates

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.theost.workchat.application.GlideApp
import com.theost.workchat.data.models.state.MessageType
import com.theost.workchat.data.models.ui.ListPhoto
import com.theost.workchat.databinding.ItemPhotoIncomeBinding
import com.theost.workchat.network.api.ApiConfig
import com.theost.workchat.ui.interfaces.AdapterDelegate
import com.theost.workchat.ui.interfaces.DelegateItem
import okhttp3.Credentials

class PhotoIncomeAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemPhotoIncomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int
    ) {
        (holder as ViewHolder).bind(item as ListPhoto)
    }

    override fun isOfViewType(item: DelegateItem): Boolean =
        item is ListPhoto && item.messageType == MessageType.INCOME

    class ViewHolder(private val binding: ItemPhotoIncomeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isLoaded: Boolean = false

        fun bind(listPhoto: ListPhoto) {
            if (!isLoaded) {
                // https://github.com/bumptech/glide/issues/835#issuecomment-167438903
                binding.photoView.layout(0, 0, 0, 0)
            }

            val authorizedUrl = GlideUrl(
                listPhoto.url,
                LazyHeaders.Builder().addHeader(
                    "Authorization",
                    Credentials.basic(ApiConfig.AUTH_EMAIL, ApiConfig.AUTH_API_KEY)
                ).build()
            )

            GlideApp.with(binding.root)
                .load(authorizedUrl)
                .override(Target.SIZE_ORIGINAL)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.root.visibility = View.VISIBLE
                        binding.photoView.setImageDrawable(resource)
                        if (!isLoaded) {
                            binding.root.animate().alpha(1f).duration = 600
                            isLoaded = true
                        }
                        return true
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean = false
                })
                .into(binding.photoView)
        }
    }
}