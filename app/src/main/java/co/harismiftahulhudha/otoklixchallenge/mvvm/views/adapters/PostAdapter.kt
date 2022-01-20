package co.harismiftahulhudha.otoklixchallenge.mvvm.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.harismiftahulhudha.otoklixchallenge.BuildConfig
import co.harismiftahulhudha.otoklixchallenge.databinding.ItemLoadingBinding
import co.harismiftahulhudha.otoklixchallenge.databinding.ItemPostBinding
import co.harismiftahulhudha.otoklixchallenge.helpers.FormatStringHelper
import co.harismiftahulhudha.otoklixchallenge.mvvm.models.PostModel

class PostAdapter(
    private val formatStringHelper: FormatStringHelper,
    private val onItemClick: (PostModel, Int) -> Unit,
    private val onItemMenuClick: (view: View, PostModel, Int) -> Unit,
): ListAdapter<PostModel, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        val VIEW_ITEM = 1
        val VIEW_LOADING = 0
    }

    fun getModels(
        data: MutableList<PostModel>,
        isLoading: Boolean = false
    ): MutableList<PostModel> {
        val models: MutableList<PostModel> = mutableListOf()
        models.addAll(data)
        val isPaginate = if (data.size > 0 && data.size % BuildConfig.LIMIT.toInt() == 0) {
            true
        } else isLoading && data.size == 0
        if (isPaginate && !isLoading(models)) {
            models.add(addLoading())
        }
        return models
    }

    private fun addLoading(): PostModel {
        val loading = PostModel(-1L)
        return loading
    }

    private fun isLoading(data: MutableList<PostModel>): Boolean {
        return data.size > 0 && data[data.size - 1].id == -1L
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_ITEM -> {
                ViewHolder(ItemPostBinding.inflate(inflater, parent, false))
            }
            else -> {
                LoadingHolder(ItemLoadingBinding.inflate(inflater, parent, false))
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        if (viewHolder.itemViewType == VIEW_ITEM) {
            val holder = viewHolder as ViewHolder
            holder.bind(model, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            getItem(position).id == -1L -> {
                VIEW_LOADING
            }
            else -> {
                VIEW_ITEM
            }
        }
    }

    inner class ViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: PostModel, position: Int) {
            binding.apply {
                txtTitle.text = model.title
                txtContent.text = model.content
                txtPublishedAt.text = formatStringHelper.covertTimeToText(model.publishedAt)
                imgMenu.setOnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                        onItemMenuClick(imgMenu, model, position)
                    }
                }
                root.setOnClickListener {
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClick(model, position)
                    }
                }
            }
        }
    }

    inner class LoadingHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<PostModel>() {
        override fun areItemsTheSame(
            oldItem: PostModel,
            newItem: PostModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: PostModel,
            newItem: PostModel
        ) = oldItem == newItem
    }
}