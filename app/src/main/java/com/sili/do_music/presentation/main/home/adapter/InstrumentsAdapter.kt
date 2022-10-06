package com.sili.do_music.presentation.main.home.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.sili.do_music.R
import com.sili.do_music.databinding.CardOfTheoryBinding
import com.sili.do_music.business.model.main.Instrument
import com.sili.do_music.util.Constants.Companion.BASE_URL
//import com.example.do_music.util.dpToPx
import com.sili.do_music.util.shimmerDrawable

class InstrumentsAdapter(
    private val context: Context? = null,
    private val fragmentName: String? = null,
    private val interaction: Interaction_Instrument? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Instrument>() {
        override fun areItemsTheSame(oldItem: Instrument, newItem: Instrument): Boolean {
            return newItem.noteId == oldItem.noteId
        }

        override fun areContentsTheSame(oldItem: Instrument, newItem: Instrument): Boolean {
            return newItem == oldItem
        }
    }


    class InstrumentViewHolder(
        private val context: Context?=null,
        private val fragmentName: String? = null,
        private val interaction: Interaction_Instrument?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(instrument: Instrument) {
            binding.root.setOnClickListener {
                instrument.noteId?.let { it1 -> interaction?.onItemSelected(it1) }
            }

            if (instrument.favorite == true) {
                binding.bookLike.setImageResource(R.drawable.ic_favourite_enabled_in_card)
            } else {
                binding.bookLike.setImageResource(R.drawable.ic_favourite_disabled_in_card)
            }

            binding.bookImage.layoutParams.width = binding.bookImage.layoutParams.height
            Glide.with(binding.root)
                .load(BASE_URL+"api/doc/logo?mini=true&uniqueName=" + instrument.logoId)
                .placeholder(shimmerDrawable)
                .into(binding.bookImage)

            fragmentName?.let {
                binding.bookName.visibility = View.GONE
                val typeface = ResourcesCompat.getFont(context!!, R.font.montserrat_medium)
                binding.bookAuthor.typeface = typeface
                binding.bookAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.toFloat())
                binding.bookAuthor.text = instrument.noteName
            }
            if(fragmentName==null){
                binding.bookAuthor.text = instrument.compositorName
                binding.bookName.text = instrument.noteName
            }
            instrument.opusEdition?.let {
                if (it != "") {
                    binding.bookEditionNotChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.text = instrument.opusEdition
                }
            }
            instrument.instrumentName?.let {
                binding.bookEditionNotChangedInstr.visibility = View.VISIBLE
                binding.bookEditionChangedInstr.visibility = View.VISIBLE
                binding.bookEditionChangedInstr.text = instrument.instrumentName
            }
            binding.bookLike.setOnClickListener {
                if (instrument.favorite) {
                    binding.bookLike.setImageResource(R.drawable.ic_favourite_disabled_in_card)
                } else {
                    binding.bookLike.setImageResource(R.drawable.ic_favourite_enabled_in_card)
                }
                instrument.favorite = !instrument.favorite
                interaction?.onLikeSelected(instrument.noteId, instrument.favorite)

            }

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
        return InstrumentsAdapter.InstrumentViewHolder(context, fragmentName, interaction, binding)
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
    fun onItemSelected(itemId: Int,nameOfCompositor:String = "")
    fun onLikeSelected(itemId: Int, isFav: Boolean)
}