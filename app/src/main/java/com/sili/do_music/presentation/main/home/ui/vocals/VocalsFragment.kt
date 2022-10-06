package com.sili.do_music.presentation.main.home.ui.vocals

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
import com.sili.do_music.databinding.FragmentVocalsBinding
import com.sili.do_music.presentation.main.home.adapter.Interaction_Instrument
import com.sili.do_music.presentation.main.home.adapter.VocalsAdapter
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.FRAGMENT
import com.sili.do_music.util.Constants.Companion.ITEM_ID
import com.sili.do_music.util.Constants.Companion.VOCALS_ID
import com.sili.do_music.util.operationErrorDialog
import com.sili.do_music.util.hide

class VocalsFragment : BaseFragment(), TextWatcher, Interaction_Instrument {
    private var vocalsAdapter: VocalsAdapter? = null
    private var _binding: FragmentVocalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VocalsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVocalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        setupRecyclerView()
    }

//    private fun updateVocals(){
//        if(uiMainUpdate.getVocalsUpdate()){
//            viewModel.getPage(update = true)
//            uiMainUpdate.setVocalsUpdate(false)
//        }
//    }

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

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(it.isLoading)
            vocalsAdapter?.apply {
                if(!it.isLoading && it.instruments.isEmpty() && it.searchText.isNotBlank()){
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                }
                else{
                    binding.noResultsLayout.root.visibility = View.GONE
                }
                submitList(it.instruments)
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

    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@VocalsFragment.context)
            vocalsAdapter = VocalsAdapter(
                interaction = this@VocalsFragment
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == vocalsAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isLastPage == false
                    ) {
                        viewModel.getPage(true)
                    }
                }
            })
            adapter = vocalsAdapter
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }


    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        viewModel.setSearchTextVocalsNotes(searchText)
        viewModel.getPage()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onItemSelected(itemId: Int, nameOfCompositor: String) {
        viewModel.state.value?.let { state ->
            val bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to VOCALS_ID)
            findNavController().navigate(R.id.action_homeFragment_to_itemSelectedInstrument, bundle)
        }
    }

    override fun onLikeSelected(itemId: Int, isFav: Boolean) {
        uiMainUpdate.isLiked(itemId,isFav, VOCALS_ID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vocalsAdapter = null
        _binding = null
    }
}