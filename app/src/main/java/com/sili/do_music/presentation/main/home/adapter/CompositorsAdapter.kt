package com.sili.do_music.presentation.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.sili.do_music.databinding.CardOfHomeBinding
import com.sili.do_music.business.model.main.Compositor
import com.sili.do_music.util.Constants.Companion.BASE_URL
import com.sili.do_music.util.shimmerDrawable

class CompositorsAdapter(
    private val interaction: Interaction_Instrument
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Compositor>() {
        override fun areItemsTheSame(oldItem: Compositor, newItem: Compositor): Boolean {
            return newItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: Compositor, newItem: Compositor): Boolean {
            return newItem == oldItem
        }
    }

    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(differCallback).build()
        )

    class CompositorViewHolder(
        private val interaction: Interaction_Instrument,//InteractionCompositor,
        private val binding: CardOfHomeBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(compositor: Compositor) {
            binding.root.setOnClickListener {
                interaction.onItemSelected(compositor.id, compositor.name)
            }
            Glide.with(binding.root)
                .load(BASE_URL + "api/doc/logo?mini=true&uniqueName=" + compositor.fileId)
                .placeholder(shimmerDrawable)
                .into(binding.compositorImage)

            binding.compositorName.text = compositor.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompositorViewHolder {
        val binding =
            CardOfHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompositorViewHolder(interaction, binding)
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

    fun submitList(compositorList: List<Compositor>?) {
        val newList = compositorList?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
