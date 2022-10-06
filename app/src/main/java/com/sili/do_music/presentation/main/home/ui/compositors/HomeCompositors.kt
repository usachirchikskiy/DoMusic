package com.sili.do_music.presentation.main.home.ui.compositors


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentHomeCompositorsBinding
import com.sili.do_music.presentation.main.home.adapter.CompositorsAdapter
import com.sili.do_music.presentation.main.home.adapter.Interaction_Instrument
import com.sili.do_music.util.Constants.Companion.AUTH_ERROR
import com.sili.do_music.util.Constants.Companion.COMPOSITOR_ID
import com.sili.do_music.util.Constants.Companion.FILTER_FOREIGN
import com.sili.do_music.util.Constants.Companion.FILTER_RUSSIAN
import com.sili.do_music.util.Constants.Companion.FILTER_UZB
import com.sili.do_music.util.Constants.Companion.NAME_OF_COMPOSITOR
import com.sili.do_music.util.Constants.Companion.NO_INTERNET
import com.sili.do_music.util.hide


class HomeCompositors : BaseFragment(), TextWatcher,
    View.OnClickListener, Interaction_Instrument {
    private var homeAdapter: CompositorsAdapter?=null
    private var _binding: FragmentHomeCompositorsBinding?=null
    private val binding get() = _binding!!
    private val viewModel: HomeCompositorViewModel by viewModels()

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
        binding.searchEt.hide {
            uiCommunicationListener.hideKeyboard()
        }
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
                        && viewModel.state.value?.isLastPage == false
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
                if(error.localizedMessage == NO_INTERNET) {
                    uiCommunicationListener.showNoInternetDialog()
                    viewModel.setErrorNull()
                }
                else if(error.localizedMessage == AUTH_ERROR) {
                    viewModel.clearSessionValues()
                    uiCommunicationListener.onAuthActivity()
                }
            }
        })
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val searchText = "" + p0.toString()
        viewModel.setSearchText(searchText)
        viewModel.getPage()
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    private fun filterSearch(
        enable: CheckBox,
        disable_first: CheckBox,
        disable_second: CheckBox,
        filter: String
    ) {
        if (enable.isChecked) {
            enable.isChecked = true
            disable_first.isChecked = false
            disable_second.isChecked = false
            setCountryFilter(filter)
        } else {
            setCountryFilter("")
        }

    }

    private fun setCountryFilter(filter: String) {
        viewModel.setCountryFilter(filter)
        viewModel.getPage(false)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.uzbekBtn -> {
                filterSearch(binding.uzbekBtn, binding.foreignBtn, binding.russianBtn, FILTER_UZB)
            }
            binding.foreignBtn -> {
                filterSearch(binding.foreignBtn, binding.uzbekBtn, binding.russianBtn, FILTER_FOREIGN)
            }
            else -> {
                filterSearch(binding.russianBtn, binding.foreignBtn, binding.uzbekBtn, FILTER_RUSSIAN)
            }
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



