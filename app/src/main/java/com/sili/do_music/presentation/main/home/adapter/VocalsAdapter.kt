package com.sili.do_music.presentation.main.home.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.sili.do_music.R
import com.sili.do_music.business.model.main.Vocal
import com.sili.do_music.databinding.CardOfTheoryBinding
import com.sili.do_music.util.Constants.Companion.BASE_URL
import com.sili.do_music.util.shimmerDrawable

class VocalsAdapter(
    private val context: Context? = null,
    private val fragmentName: String? = null,
    private val interaction: Interaction_Instrument? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Vocal>() {
        override fun areItemsTheSame(oldItem: Vocal, newItem: Vocal): Boolean {
            return newItem.vocalsId == oldItem.vocalsId
        }

        override fun areContentsTheSame(oldItem: Vocal, newItem: Vocal): Boolean {
            return newItem == oldItem
        }
    }


    class InstrumentViewHolder(
        private val context: Context? = null,
        private val fragmentName: String? = null,
        private val interaction: Interaction_Instrument?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vocal: Vocal) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(vocal.vocalsId)
            }

            if (vocal.favorite == true) {
                binding.bookLike.setImageResource(R.drawable.ic_favourite_enabled_in_card)
            } else {
                binding.bookLike.setImageResource(R.drawable.ic_favourite_disabled_in_card)
            }

            Glide.with(binding.root)
                .load(BASE_URL + "api/doc/logo?mini=true&uniqueName=" + vocal.logoId)
                .placeholder(shimmerDrawable)
                .into(binding.bookImage)


            fragmentName?.let {
                binding.bookName.visibility = View.GONE
                val typeface = ResourcesCompat.getFont(context!!, R.font.montserrat_medium)
                binding.bookAuthor.typeface = typeface
                Log.d("InstrumentsAdapter", "bind: ")
                binding.bookAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.toFloat())
                binding.bookAuthor.text = vocal.noteName
            }
            if (fragmentName == null) {
                binding.bookAuthor.text = vocal.compositorName
                binding.bookName.text = vocal.noteName
            }

            vocal.opusEdition.let {
                if (it != "") {
                    binding.bookEditionNotChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.text = vocal.opusEdition
                }
            }

            binding.bookLike.setOnClickListener {
                if (vocal.favorite) {
                    binding.bookLike.setImageResource(R.drawable.ic_favourite_disabled_in_card)
                } else {
                    binding.bookLike.setImageResource(R.drawable.ic_favourite_enabled_in_card)

                }
                vocal.favorite = !vocal.favorite
                interaction?.onLikeSelected(vocal.vocalsId, vocal.favorite)
            }

        }
    }


    internal inner class InstrumentRecyclerChangeCallback(
        private val adapter: VocalsAdapter
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
        return VocalsAdapter.InstrumentViewHolder(context, fragmentName, interaction, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InstrumentViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }


    fun submitList(vocal: List<Vocal>?) {
        val newList = vocal?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
//
//interface Interaction_Vocal {
//    fun onItemSelected(position: Int)
//    fun onLikeSelected(position: Int)
//}