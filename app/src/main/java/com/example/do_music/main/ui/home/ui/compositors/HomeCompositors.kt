package com.example.do_music.main.ui.home.ui.compositors


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentHomeCompositorsBinding
import com.example.do_music.main.ui.home.adapter.CompositorsAdapter
import com.example.do_music.main.ui.home.adapter.Interaction_Instrument
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HomeCompositors"

@AndroidEntryPoint
class HomeCompositors : Fragment(), TextWatcher,
    View.OnClickListener, Interaction_Instrument {
    private var homeAdapter: CompositorsAdapter?=null
    private lateinit var binding: FragmentHomeCompositorsBinding
    private val viewModel: HomeCompositorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeCompositorsBinding.inflate(inflater, container, false)
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
                        viewModel.getpage(true)
//                        setPadding(0, 0, 0, 0)
                    }

                }
            })
            adapter = homeAdapter
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
            homeAdapter?.apply {
                submitList(compositorList = it.compositors)
            }
            it.error?.let {
                showError(it)
            }
        })
    }

    private fun showError(it: Throwable) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val searchText = "" + p0.toString()
//        if(searchText==""){
//            binding.searchEt.setFocusable(false)
//        }
        viewModel.setSearchText(searchText)
        viewModel.getpage()
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    fun filtersearch(
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
        viewModel.getpage(false)
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.uzbekBtn) {
            filtersearch(binding.uzbekBtn, binding.foreignBtn, binding.russianBtn, "UZB")
        } else if (p0 == binding.foreignBtn) {
            filtersearch(binding.foreignBtn, binding.uzbekBtn, binding.russianBtn, "FOREIGN")
        } else {
            filtersearch(binding.russianBtn, binding.foreignBtn, binding.uzbekBtn, "RUSSIAN")
        }
    }

    override fun onItemSelected(compositorId: Int,nameOfCompositor:String) {
        Log.d(TAG, "onItemSelected: " + compositorId)
        val bundle = bundleOf("id" to compositorId,"nameOfCompositor" to nameOfCompositor)
        findNavController().navigate(
            R.id.action_homeFragment_to_homeCompositorSelectedFragment,
            bundle
        )
    }

    override fun onLikeSelected(itemId: Int,isFav:Boolean) {

    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
////        homeAdapter = null
////        _binding=null
//        viewModel.setCountryFilter("")
//    }
//

}



