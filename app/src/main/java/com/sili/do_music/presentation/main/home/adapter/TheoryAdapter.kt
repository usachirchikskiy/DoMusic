package com.sili.do_music.presentation.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.sili.do_music.R
import com.sili.do_music.business.model.main.TheoryInfo
import com.sili.do_music.databinding.CardOfTheoryBinding
import com.sili.do_music.util.Constants.Companion.BASE_URL
import com.sili.do_music.util.shimmerDrawable

private const val TAG = "TheoryAdapter"

class TheoryAdapter(
    private val interaction: Interaction_Instrument? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<TheoryInfo>() {
        override fun areItemsTheSame(oldItem: TheoryInfo, newItem: TheoryInfo): Boolean {
            return newItem.bookId == oldItem.bookId
        }

        override fun areContentsTheSame(oldItem: TheoryInfo, newItem: TheoryInfo): Boolean {
            return newItem == oldItem
//            return newItem == oldItem
        }
    }


    class TheoryViewHolder(
        private val interaction: Interaction_Instrument?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: TheoryInfo) {

            binding.root.setOnClickListener {
                interaction?.onItemSelected(book.bookId)
            }
            if (book.favorite) {
                binding.bookLike.setImageResource(com.sili.do_music.R.drawable.ic_favourite_enabled_in_card)
            } else {
                binding.bookLike.setImageResource(com.sili.do_music.R.drawable.ic_favourite_disabled_in_card)
            }

            Glide.with(binding.root)
                .load(BASE_URL + "api/doc/logo?mini=true&uniqueName=" + book.logoId)
                .placeholder(shimmerDrawable)
                .into(binding.bookImage)

            binding.bookAuthor.text = book.authorName
            binding.bookName.text = book.bookName
            book.opusEdition.let {
                if (it != "") {
                    binding.bookEditionChanged.text = book.opusEdition
                    binding.bookEditionChanged.visibility = View.VISIBLE
                    binding.bookEditionNotChanged.visibility = View.VISIBLE
                }
            }

            binding.bookLike.setOnClickListener {
                if (book.favorite) {
                    binding.bookLike.setImageResource(R.drawable.ic_favourite_disabled_in_card)
                } else {
                    binding.bookLike.setImageResource(R.drawable.ic_favourite_enabled_in_card)
                }
                book.favorite=!book.favorite
                interaction?.onLikeSelected(book.bookId, book.favorite)
            }
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