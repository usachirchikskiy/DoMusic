package com.example.do_music.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.CardOfTheoryBinding
import com.example.do_music.model.TheoryInfo

class TheoryAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<TheoryInfo>() {
        override fun areItemsTheSame(oldItem: TheoryInfo, newItem: TheoryInfo): Boolean {
            return newItem.bookId == oldItem.bookId
        }

        override fun areContentsTheSame(oldItem: TheoryInfo, newItem: TheoryInfo): Boolean {
            return newItem==oldItem
//            return newItem == oldItem
        }
    }


    class TheoryViewHolder(
        private val interaction: Interaction?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: TheoryInfo) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, book)
            }
            if (book.isFavourite != false && book.isFavourite != null) {
                binding.bookLike.setImageResource(R.drawable.ic_selected_in_card)
            } else {
                binding.bookLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            Glide.with(binding.root)
                .load("https://domusic.uz/api/doc/logo?mini=true&uniqueName=" + book.logoId)
                .into(binding.bookImage)

            binding.bookAuthor.text = book.authorName
            binding.bookName.text = book.bookName
            binding.bookEditionChanged.text = book.opusEdition

            binding.bookLike.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    interaction?.onLikeSelected(position = adapterPosition)
                    if (book.isFavourite != false) {
                        binding.bookLike.setImageResource(R.drawable.ic_selected_in_card)
                    } else {
                        binding.bookLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                }

            })

        }
    }


    internal inner class BookRecyclerChangeCallback(
        private val adapter: TheoryAdapter
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
            BookRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(differCallback).build()
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            CardOfTheoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TheoryAdapter.TheoryViewHolder(interaction, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TheoryViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }


    fun submitList(books: List<TheoryInfo>?) {
        val newList = books?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

interface Interaction {
    fun onItemSelected(position: Int, item: TheoryInfo)
    fun onLikeSelected(position: Int)
}