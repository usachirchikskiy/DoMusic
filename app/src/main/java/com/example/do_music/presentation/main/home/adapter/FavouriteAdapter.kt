package com.example.do_music.presentation.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.CardOfTheoryBinding
import com.example.do_music.business.model.main.Favourite
import com.example.do_music.util.shimmerDrawable

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
        RecyclerView.ViewHolder(binding.root)
//        CompoundButton.OnCheckedChangeListener
    {


//        private fun changeState(
//            checkBox: CompoundButton? = null,
//            isfavClass: Boolean = false,
//            position: Int = -1
//        ) {
//            var arrayList = arrayListOf<CheckBox>()
//            arrayList.add(binding.one)
//            arrayList.add(binding.two)
//            arrayList.add(binding.three)
//            arrayList.add(binding.four)
//            arrayList.add(binding.five)
//            arrayList.add(binding.six)
//
//            if (isfavClass) {
//                arrayList[position].isChecked = true
//            } else {
//                for (i in arrayList) {
//                    if (checkBox != i) {
//                        i.isChecked = false
//                    }
//                }
//            }
//        }

        fun bind(favourite: Favourite) {
//            var position = -1
//            favourite.bookId?.let {
//                if(favourite.bookClass!="UNKNOWN"){
//                    val arrayOfResults = favourite.bookClass?.split("CLASS_")
//                    position = arrayOfResults!![1].toInt()
//                }
//            }
//            favourite.noteId?.let {
//                if(favourite.notesClass!="UNKNOWN"){
//                    val arrayOfResults = favourite.notesClass?.split("CLASS_")
//                    position = arrayOfResults!![1].toInt()
//                }
//            }
//            favourite.vocalsId?.let {
//                if(favourite.vocalsClass!="UNKNOWN"){
//                    val arrayOfResults = favourite.vocalsClass?.split("CLASS_")
//                    position = arrayOfResults!![1].toInt()
//                }
//            }
//            if(position==67){
//                position = 6
//            }
//
//            if(position!=-1){
//                changeState(isfavClass = true,position = position-1)
//            }
            binding.root.setOnClickListener {
                interaction?.onItemSelected(adapterPosition)
            }
//            binding.cardOfClasses.visibility = View.VISIBLE
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

//            binding.one.setOnCheckedChangeListener(this)
//            binding.two.setOnCheckedChangeListener(this)
//            binding.three.setOnCheckedChangeListener(this)
//            binding.four.setOnCheckedChangeListener(this)
//            binding.five.setOnCheckedChangeListener(this)
//            binding.six.setOnCheckedChangeListener(this)

            binding.bookLike.setOnClickListener {
                interaction?.onDeleteSelected(
                    favourite.favoriteId!!, false,
                    favourite.compositorName!!
                )
            }

        }

//        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//            var favClass = "UNKNOWN"
//            if (buttonView!!.isChecked) {
//                changeState(buttonView)
//                favClass = buttonView.text.toString()
//                if(favClass.length>1){
//                    favClass = favClass.replace("-","")
//                }
//                favClass = "CLASS_" + favClass
//            }
//            interaction!!.onClassSelected(favClass, adapterPosition)
//        }
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