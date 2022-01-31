package com.example.do_music.ui.main.home.ui.vocals

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentVocalsBinding
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.ui.main.home.adapter.Interaction_Instrument
import com.example.do_music.ui.main.home.adapter.VocalsAdapter
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.ITEM_ID
import com.example.do_music.util.Constants.Companion.VOCALS
import com.example.do_music.util.Constants.Companion.VOCALS_ID

private const val TAG = "VocalsFragment"

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
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(VOCALS)
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                shouldRefresh?.run {
                    viewModel.getPage(update = true)
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        VOCALS,
                        null
                    )
                }
            }
        setupViews()
        setupObservers()
        setupRecyclerView()
    }

    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)

        viewModel.state.value?.let {
            if(it.searchText.isNotBlank()){
                binding.searchEt.setText(it.searchText)
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setupObservers: " + it)

            uiCommunicationListener.displayProgressBar(it.isLoading)
//            showProgressBar(it.isLoading)
            vocalsAdapter?.apply {
                if(!it.isLoading && it.instruments.isEmpty() && it.searchText.isNotBlank()){
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                }
                else{
                    binding.noResultsLayout.root.visibility = View.GONE
                }
                submitList(it.instruments)
            }

            it.error?.let {
                //Todo
            }

        })

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.getPage(update = true)
            }
        })


    }

//    private fun showProgressBar(isLoading: Boolean) {
//        if (isLoading) {
//            binding.paginationProgressBar.visibility = View.VISIBLE
//        } else {
//            binding.paginationProgressBar.visibility = View.INVISIBLE
//        }
//
//    }

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
                    ) {
                        viewModel.getPage(true)
//                        setPadding(0, 0, 0, 0)
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
        Log.d(TAG, "onTextChanged: ")
        viewModel.setLoadingToFalse()
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
        viewModel.isLikedVocalsNotes(itemId, isFav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vocalsAdapter = null
        _binding = null
    }
}