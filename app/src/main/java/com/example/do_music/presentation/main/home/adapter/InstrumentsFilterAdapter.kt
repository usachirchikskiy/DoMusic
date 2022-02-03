package com.example.do_music.presentation.main.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.do_music.databinding.CheckboxCardBinding

private const val TAG = "InstrumentsFilterAdapter"

class InstrumentsFilterAdapter(
    private val interaction: InteractionFilter? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<InstrumentHelper>() {
        override fun areItemsTheSame(
            oldItem: InstrumentHelper,
            newItem: InstrumentHelper
        ): Boolean {
            return newItem.GroupName == oldItem.GroupName
        }

        override fun areContentsTheSame(
            oldItem: InstrumentHelper,
            newItem: InstrumentHelper
        ): Boolean {
            return newItem == oldItem
        }
    }

    internal inner class InstrumentRecyclerChangeCallback(
        private val adapter: InstrumentsFilterAdapter
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
            CheckboxCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InstrumentFilterViewHolder(interaction, binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //First, get data from catList by position.
        when (holder) {
            is InstrumentFilterViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    fun submitList(filterList: List<InstrumentHelper>?) {
        val newList = filterList?.toMutableList()
        differ.submitList(newList)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}

class InstrumentFilterViewHolder(
    private val interaction: InteractionFilter?,
    private val binding: CheckboxCardBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(instrumentHelper: InstrumentHelper) {
        val key = instrumentHelper.name
        binding.instrumentCheckbox.text = key

        if (instrumentHelper.isAnsamble || instrumentHelper.isInstumentId || instrumentHelper.isGroupName) {
            binding.instrumentCheckbox.setChecked(true)
        } else {
            binding.instrumentCheckbox.setChecked(false)
        }

        binding.instrumentCheckbox.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    interaction?.onCheckBoxSelected(position = adapterPosition)
                }
            }
        )

    }
}

interface InteractionFilter {
    fun onCheckBoxSelected(position: Int)
}