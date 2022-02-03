package com.example.do_music.presentation.main.home.ui.compositors


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.presentation.BaseFragment
import com.example.do_music.R
import com.example.do_music.databinding.FragmentHomeCompositorsBinding

import com.example.do_music.presentation.main.home.adapter.CompositorsAdapter
import com.example.do_music.presentation.main.home.adapter.Interaction_Instrument
import com.example.do_music.util.noInternet
import com.example.do_music.util.Constants.Companion.COMPOSITOR_ID
import com.example.do_music.util.Constants.Companion.FILTER_FOREIGN
import com.example.do_music.util.Constants.Companion.FILTER_RUSSIAN
import com.example.do_music.util.Constants.Companion.FILTER_UZB
import com.example.do_music.util.Constants.Companion.NAME_OF_COMPOSITOR

private const val TAG = "HomeCompositors"

class HomeCompositors : BaseFragment(), TextWatcher,
    View.OnClickListener, Interaction_Instrument {
    private var homeAdapter: CompositorsAdapter?=null
    private var _binding: FragmentHomeCompositorsBinding?=null
    private val binding get() = _binding!!

    private val viewModel: HomeCompositorViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: " + viewModel.toString())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCompositorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        setupRecyclerView()
    }

    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)
        binding.uzbekBtn.setOnClickListener(this)
        binding.foreignBtn.setOnClickListener(this)
        binding.russianBtn.setOnClickListener(this)
        viewModel.state.value?.let {
            if(it.searchText.isNotBlank()){
                binding.searchEt.setText(it.searchText)
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@HomeCompositors.context)
            homeAdapter = CompositorsAdapter(
                interaction = this@HomeCompositors
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == homeAdapter!!.itemCount.minus(1)
                        && viewModel.state.value?.isLoading == false
                    ) {
                        viewModel.getPage(true)
                    }

                }
            })
            adapter = homeAdapter
        }

    }


    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setupObservers: " + it)
            uiCommunicationListener.displayProgressBar(it.isLoading)

            homeAdapter?.apply {
                if(!it.isLoading && it.compositors.isEmpty() && it.searchText.isNotBlank()){
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                }
                else{
                    binding.noResultsLayout.root.visibility = View.GONE
                }
                submitList(compositorList = it.compositors)
            }
            it.error?.let { error->
                if(error.localizedMessage.contains("failed to connect to XXXX")){
                 noInternet(context)
                    Log.d(TAG, "setupObservers: " + error.localizedMessage)
                }
            }
        })
    }

    private fun showError(it: Throwable) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val searchText = "" + p0.toString()
        Log.d(TAG, "onTextChanged: " + searchText)
//        viewModel.setLoadingToFalse()
        viewModel.setSearchText(searchText)
        viewModel.getPage()
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    private fun filtersearch(
        enable: CheckBox,
        disable_first: CheckBox,
        disable_second: CheckBox,
        filter: String
    ) {
        if (enable.isChecked == true) {
            enable.setChecked(true)
            disable_first.setChecked(false)
            disable_second.setChecked(false)
            setCountryFilter(filter)
        } else {
            setCountryFilter("")
        }
//        else {
//            enable.setChecked(true)
//            disable_first.setChecked(false)
//            disable_second.setChecked(false)
//            setCountryFilter(filter)
//        }

    }

    fun setCountryFilter(filter: String) {
        viewModel.setCountryFilter(filter)
        viewModel.getPage(false)
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.uzbekBtn) {
            filtersearch(binding.uzbekBtn, binding.foreignBtn, binding.russianBtn, FILTER_UZB)
        } else if (p0 == binding.foreignBtn) {
            filtersearch(binding.foreignBtn, binding.uzbekBtn, binding.russianBtn, FILTER_FOREIGN)
        } else {
            filtersearch(binding.russianBtn, binding.foreignBtn, binding.uzbekBtn, FILTER_RUSSIAN)
        }
    }

    override fun onItemSelected(itemId: Int, nameOfCompositor:String) {
        val bundle = bundleOf(COMPOSITOR_ID to itemId,NAME_OF_COMPOSITOR to nameOfCompositor)
        findNavController().navigate(
            R.id.action_homeFragment_to_homeCompositorSelectedFragment,
            bundle
        )
    }

    override fun onLikeSelected(itemId: Int,isFav:Boolean) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        homeAdapter = null
        _binding = null
    }

}



