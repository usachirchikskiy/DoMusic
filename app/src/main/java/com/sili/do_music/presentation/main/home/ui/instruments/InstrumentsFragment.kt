package com.sili.do_music.presentation.main.home.ui.instruments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentInstrumentsBinding

import com.sili.do_music.presentation.main.home.adapter.*
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.FRAGMENT
import com.sili.do_music.util.Constants.Companion.ITEM_ID
import com.sili.do_music.util.Constants.Companion.NOTE_ID
import com.sili.do_music.util.operationErrorDialog
import com.sili.do_music.util.hide
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager

class InstrumentsFragment : BaseFragment(), Interaction_Instrument, InteractionFilter, TextWatcher {
    private var instrumentsAdapter: InstrumentsAdapter? = null
    private var instrumentsFilterAdapter: InstrumentsFilterAdapter? = null
    private var _binding: FragmentInstrumentsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstrumentsViewModel by viewModels()
    private lateinit var lang:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lang = uiCommunicationListener.getLocale()
        viewModel.setFiltersInit(lang)
        viewModel.getPage()
    }
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


    private fun setupViews() {
        binding.searchEt.hide {
            uiCommunicationListener.hideKeyboard()
        }
        binding.searchEt.addTextChangedListener(this)
        viewModel.state.value?.let {
            if(it.searchText.isNotBlank()){
                binding.searchEt.setText(it.searchText)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@InstrumentsFragment.context)
            instrumentsAdapter = InstrumentsAdapter(
                interaction = this@InstrumentsFragment
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == instrumentsAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isLastPage == false
                    ) {
                        viewModel.getPage(true)
                    }

                }
            })
            adapter = instrumentsAdapter
        }

        binding.recvCheckboxes.apply {
            layoutManager = FlowLayoutManager()
            layoutManager?.apply {
                isAutoMeasureEnabled = true
            }
            instrumentsFilterAdapter = InstrumentsFilterAdapter(this@InstrumentsFragment)
            adapter = instrumentsFilterAdapter
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)
            instrumentsFilterAdapter?.apply {
                submitList(filterList = it.instrumentsGroup)
            }

            instrumentsAdapter?.apply {
                if(!it.isLoading && it.instruments.isEmpty() && it.searchText.isNotBlank()){
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                }
                else{
                    binding.noResultsLayout.root.visibility = View.GONE
                }
                submitList(instruments = it.instruments)
            }

            it.error?.let { error->
                when (error.localizedMessage) {
                    Constants.NO_INTERNET -> {
                        uiCommunicationListener.showNoInternetDialog()
                        viewModel.setErrorNull()
                    }
                    Constants.AUTH_ERROR -> {
                        viewModel.clearSessionValues()
                        uiCommunicationListener.onAuthActivity()
                    }
                    Constants.ERROR_ADD_TO_FAVOURITES -> {
                        operationErrorDialog(context)
                        viewModel.setErrorNull()
                    }
                }
            }

        })

    }

    override fun onItemSelected(itemId: Int, nameOfCompositor: String) {
        viewModel.state.value?.let { state ->
            val bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to NOTE_ID)
            findNavController().navigate(R.id.action_homeFragment_to_itemSelectedInstrument, bundle)
        }
    }

    override fun onLikeSelected(itemId: Int, isFav: Boolean) {
        uiMainUpdate.isLiked(itemId,isFav, NOTE_ID)
    }

    override fun onCheckBoxSelected(position: Int) {
        val instrumentHelper = viewModel.getInstrumentHelper(position)
        instrumentHelper?.let { viewModel.filterSelected(it,lang) }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        viewModel.setSearchText(searchText)
        viewModel.getPage()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recvCheckboxes.layoutManager = null
        instrumentsAdapter = null
        instrumentsFilterAdapter = null
        _binding = null

    }
}




