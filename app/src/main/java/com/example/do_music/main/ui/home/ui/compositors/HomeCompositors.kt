package com.example.do_music.main.ui.home.ui.compositors


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.databinding.FragmentHomeCompositorsBinding
import com.example.do_music.main.ui.home.adapter.CompositorsAdapter
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "HomeCompositors"

@AndroidEntryPoint
class HomeCompositors : Fragment(), TextWatcher,
    View.OnClickListener {
    private lateinit var homeadapter: CompositorsAdapter
    private lateinit var binding: FragmentHomeCompositorsBinding
//    private val binding get() = _binding

    private val viewModel: HomeCompositorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeCompositorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupViews()

    }

    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)
        binding.uzbekBtn.setOnClickListener(this)
        binding.foreignBtn.setOnClickListener(this)
        binding.russianBtn.setOnClickListener(this)
    }


    private fun setupRecyclerView() {
        homeadapter = CompositorsAdapter()
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@HomeCompositors.context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == homeadapter?.itemCount?.minus(1)
                    ) {
                        viewModel.getpage(true)
//                        setPadding(0, 0, 0, 0)
                    }

                }
            })
            adapter = homeadapter
        }

    }


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE

    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE

    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
//            if (it.isLoading==true){
//                showProgressBar()
//            }
//            else{
//                hideProgressBar()
//            }
            Log.d(TAG, "setupObservers: " + it.compositors.toString())
            homeadapter?.submitList(compositorList = it.compositors)

        })
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
        Log.d(TAG, "filtersearch: " + enable.isChecked.toString())
        if (enable.isChecked == true) {
            enable.setChecked(true)
            disable_first.setChecked(false)
            disable_second.setChecked(false)
            setCountryFilter(filter)
        }
        else{
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
            filtersearch(binding.russianBtn, binding.foreignBtn, binding.uzbekBtn ,"RUSSIAN")
        }
    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
////        homeadapter = null
////        _binding=null
//        viewModel.setCountryFilter("")
//    }
//

}



