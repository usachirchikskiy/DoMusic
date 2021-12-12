package com.example.do_music.main.ui.home.ui.instruments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentInstrumentsBinding
import com.example.do_music.main.ui.home.adapter.*
import com.example.do_music.model.Instrument
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "InstrumentsFragment"
@AndroidEntryPoint
class InstrumentsFragment : Fragment(), Interaction_Instrument, InteractionFilter, TextWatcher {
    private var instrumentsAdapter: InstrumentsAdapter? = null
    private var instrumentsFilterAdapter: InstrumentsFilterAdapter? = null
    private var _binding: FragmentInstrumentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstrumentsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstrumentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        setupRecyclerView()
    }

    private fun setupViews(){
        binding.searchEt.addTextChangedListener(this)
    }

    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@InstrumentsFragment.context)
            instrumentsAdapter = InstrumentsAdapter(context = requireContext(),
                interaction = this@InstrumentsFragment
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == instrumentsAdapter?.itemCount?.minus(1)
                    ) {
                        viewModel.getpage(true)
//                        setPadding(0, 0, 0, 0)
                    }

                }
            })
            adapter = instrumentsAdapter
        }
        binding.recvCheckboxes.apply {
            layoutManager = FlowLayoutManager()
            layoutManager?.apply {
                setAutoMeasureEnabled(true)
            }
            instrumentsFilterAdapter = InstrumentsFilterAdapter(this@InstrumentsFragment)
            adapter = instrumentsFilterAdapter

        }
    }


    private fun showProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        } else {
            binding.paginationProgressBar.visibility = View.INVISIBLE
        }

    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {

            showProgressBar(it.isLoading)

            instrumentsFilterAdapter?.apply {
                if (it.instrumentsGroup.isNotEmpty()) {
                    submitList(filterList = it.instrumentsGroup)
                }
            }

            instrumentsAdapter?.apply {
                submitList(instruments = it.instruments)
            }

            it.error?.let {
                showError(it)
            }

        })

        viewModel.favourite.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.state.value?.let { it1 ->
                    Log.d(TAG, "setupObservers: " + it1)
                    instrumentsAdapter?.notifyItemChanged(it1.position)
                }

            }
        })

    }

    private fun showError(it: Throwable) {

    }

    override fun onItemSelected(position: Int) {
        viewModel.state.value?.let { state ->
            val bundle = bundleOf("position" to position,"fragment" to "instrument")
            findNavController().navigate(R.id.action_homeFragment_to_itemSelectedInstrument, bundle)
        }
    }

    override fun onLikeSelected(position: Int) {
        viewModel.isLiked(position)
    }

    override fun onCheckBoxSelected(position: Int) {

        val instrumentHelper = viewModel.getInstrumentHelper(position)
        instrumentHelper?.let { viewModel.filterSelected(it) }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        viewModel.setSearchText(searchText)
        viewModel.getpage()
    }

    override fun afterTextChanged(s: Editable?) {
    }

}



