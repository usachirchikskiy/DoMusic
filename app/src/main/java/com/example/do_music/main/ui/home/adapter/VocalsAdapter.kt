package com.example.do_music.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.CardOfTheoryBinding
import com.example.do_music.model.Vocal

class VocalsAdapter (
    private val interaction: Interaction_Instrument? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Vocal>() {
        override fun areItemsTheSame(oldItem: Vocal, newItem: Vocal): Boolean {
            return newItem.vocalsId == oldItem.vocalsId
        }

        override fun areContentsTheSame(oldItem: Vocal, newItem: Vocal): Boolean {
            return newItem==oldItem
        }
    }


    class InstrumentViewHolder(
        private val interaction: Interaction_Instrument?,
        private val binding: CardOfTheoryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vocal: Vocal) {
            binding.root.setOnClickListener {
                interaction?.onItemSelected(vocal.vocalsId)
            }

            if (vocal.favorite==true) {
                binding.bookLike.setImageResource(R.drawable.ic_selected_in_card)
            } else {
                binding.bookLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            Glide.with(binding.root)
                .load("https://domusic.uz/api/doc/logo?mini=true&uniqueName=" + vocal.logoId)
                .into(binding.bookImage)

            binding.bookAuthor.text = vocal.compositorName
            binding.bookName.text = vocal.noteName
            vocal.opusEdition.let{
                if (it!="") {
                    binding.bookEditionNotChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.visibility = View.VISIBLE
                    binding.bookEditionChanged.text = vocal.opusEdition
                }
            }

            binding.bookLike.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {

//                    if (vocal.favorite==false) {
//                        binding.bookLike.setImageResource(R.drawable.ic_selected_in_card)
//                    } else {
//                        binding.bookLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
//                    }

                    if(vocal.favorite!!) {
                        interaction?.onLikeSelected(vocal.favoriteId!!, !vocal.favorite)
                    }
                    else{
                        interaction?.onLikeSelected(vocal.vocalsId,!vocal.favorite)
                    }
                }

            })

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
        return VocalsAdapter.InstrumentViewHolder(interaction, binding)
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