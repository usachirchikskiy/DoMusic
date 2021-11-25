package com.example.do_music.main.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.CardOfTheoryBinding
import com.example.do_music.model.Instrument

class InstrumentsAdapter (
    private val interaction: Interaction_Instrument? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Instrument>() {
        override fun areItemsTheSame(oldItem: Instrument, newItem: Instrument): Boolean {
            return newItem.noteId == oldItem.noteId
        }

        override fun areContentsTheSame(oldItem: Instrument, newItem: Instrument): Boolean {
            return newItem==oldItem
        }
    }


    class InstrumentViewHolder(
        private val interaction: Interaction_Instrument?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(instrument: Instrument) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, instrument)
            }
            if (instrument.isFavourite != false && instrument.isFavourite != null) {
                binding.bookLike.setImageResource(R.drawable.ic_selected_in_card)
            } else {
                binding.bookLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            Glide.with(binding.root)
                .load("https://domusic.uz/api/doc/logo?mini=true&uniqueName=" + instrument.logoId)
                .into(binding.bookImage)

            binding.bookAuthor.text = instrument.compositorName
            binding.bookName.text = instrument.instrumentName
            binding.bookEditionChanged.text = instrument.opusEdition

            binding.bookLike.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    interaction?.onLikeSelected(position = adapterPosition)
                    if (instrument.isFavourite != false) {
                        binding.bookLike.setImageResource(R.drawable.ic_selected_in_card)
                    } else {
                        binding.bookLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                }

            })

        }
    }


    internal inner class InstrumentRecyclerChangeCallback(
        private val adapter: InstrumentsAdapter
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
            InstrumentRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(differCallback).build()
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            CardOfTheoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstrumentsAdapter.InstrumentViewHolder(interaction, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InstrumentViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }


    fun submitList(instruments: List<Instrument>?) {
        val newList = instruments?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

interface Interaction_Instrument {
    fun onItemSelected(position: Int, item: Instrument)
    fun onLikeSelected(position: Int)
}