package com.example.do_music.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.do_music.R
import com.example.do_music.databinding.CardOfHomeBinding
import com.example.do_music.model.CompositorInfo

class CompositorsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val differCallback = object : DiffUtil.ItemCallback<CompositorInfo>() {
        override fun areItemsTheSame(oldItem: CompositorInfo, newItem: CompositorInfo): Boolean {
            return newItem.id==oldItem.id
        }

        override fun areContentsTheSame(oldItem: CompositorInfo, newItem: CompositorInfo): Boolean {
            return newItem == oldItem
        }
    }


    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(differCallback).build()
        )

class CompositorViewHolder (private val binding: CardOfHomeBinding) :
    RecyclerView.ViewHolder(binding.root) {

        fun bind(compositor: CompositorInfo) {

                 Glide.with(binding.root)
                    .load("https://domusic.uz/api/doc/logo?mini=true&uniqueName="+compositor.fileId)
                    .into(binding.compositorImage)

                binding.compositorName.text = compositor.name
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompositorViewHolder {
        val binding =
            CardOfHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompositorViewHolder(binding)
    }

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: CompositorsAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is CompositorViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }
    fun submitList(compositorList: List<CompositorInfo>?, ){
        val newList = compositorList?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}