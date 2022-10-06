package com.sili.do_music.presentation.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.sili.do_music.R
import com.sili.do_music.business.model.main.Favourite
import com.sili.do_music.databinding.CardOfTheoryBinding
import com.sili.do_music.util.shimmerDrawable

class FavouriteAdapter(
    private val interaction: Interaction_Favourite? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Favourite>() {
        override fun areItemsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return newItem.favoriteId == oldItem.favoriteId
        }

        override fun areContentsTheSame(oldItem: Favourite, newItem: Favourite): Boolean {
            return newItem == oldItem
        }
    }

    class FavouriteViewHolder(
        private val interaction: Interaction_Favourite?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favourite: Favourite) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition)
            }
            binding.bookImage.layoutParams.width = binding.bookImage.layoutParams.height
            Glide.with(binding.root)
                .load("https://domusic.uz/api/doc/logo?mini=true&uniqueName=" + favourite.logoId)
                .placeholder(shimmerDrawable)
                .into(binding.bookImage)
            binding.bookLike.setImageResource(R.drawable.ic_favourite_enabled_in_card)
            binding.bookAuthor.text = favourite.compositorName
            binding.bookName.text = favourite.noteName

            favourite.opusEdition?.let {
                if (it != "") {
                    binding.bookEditionNotChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.text = favourite.opusEdition
                }
            }

            favourite.instrumentName?.let {
                binding.bookEditionNotChangedInstr.visibility = View.VISIBLE
                binding.bookEditionChangedInstr.visibility = View.VISIBLE
                binding.bookEditionChangedInstr.text = favourite.instrumentName
            }

            binding.bookLike.setOnClickListener {
                var id: Int = -1
                favourite.noteId?.let { noteId ->
                    id = noteId
                }

                favourite.bookId?.let { bookId ->
                    id = bookId
                }

                favourite.vocalsId?.let { vocalsId ->
                    id = vocalsId
                }
                interaction?.onDeleteSelected(
                    id, false,
                    favourite.compositorName!!
                )
            }

        }

    }


    internal inner class FavouriteRecyclerChangeCallback(
        private val adapter: FavouriteAdapter
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


    private val differ =
        AsyncListDiffer(
            FavouriteRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(differCallback).build()
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            CardOfTheoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteAdapter.FavouriteViewHolder(interaction, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FavouriteViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }


    fun submitList(favourites: List<Favourite>?) {
        val newList = favourites?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

interface Interaction_Favourite {
    fun onDeleteSelected(itemId: Int, isFav: Boolean, compositorName: String)
    fun onItemSelected(position: Int)
}