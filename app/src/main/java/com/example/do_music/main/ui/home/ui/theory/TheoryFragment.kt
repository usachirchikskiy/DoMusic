package com.example.do_music.main.ui.home.ui.theory

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.core.util.Preconditions
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentTheoryBinding
import com.example.do_music.main.ui.home.adapter.Interaction
import com.example.do_music.main.ui.home.adapter.TheoryAdapter
import com.example.do_music.model.TheoryInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log


private const val TAG = "TheoryFragment"

@AndroidEntryPoint
class TheoryFragment : Fragment(), TextWatcher, View.OnClickListener, Interaction {

    private var theoryAdapter: TheoryAdapter? = null
    private var _binding: FragmentTheoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TheoryViewModel by activityViewModels()
//
//    companion object {
//        const val REQUEST_KEY = "result-listener-request-key"
//        const val KEY_NUMBER = "key-number"
//    }

//
//    @SuppressLint("RestrictedApi")
//    private fun onFragmentResult(requestKey: String, result: Bundle) {
//        Preconditions.checkState(REQUEST_KEY == requestKey)
//
//        val number = result.getInt(KEY_NUMBER)
//        Log.d(TAG, "onFragmentResult: " + number)
//        viewModel.isLiked(number)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theoryAdapter = TheoryAdapter(this@TheoryFragment)
//        parentFragmentManager.setFragmentResultListener(
//            REQUEST_KEY,
//            this,
//            FragmentResultListener { requestKey, result ->
//                onFragmentResult(requestKey, result)
//            })



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTheoryBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setFragmentResultListener("KEY") { key, bundle ->
//            // read from the bundle
//            Log.d(TAG, "onViewCreated: " + bundle)
//        }
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>("key")?.observe(viewLifecycleOwner) {result ->
//            Log.d(TAG, "onViewCreated: " + result)           // Do something with the result.
//        }

        setupObservers()
        setupRecyclerView()
        setupViews()
//        setFragmentResultListener("requestKey") { key, bundle ->
//            // We use a String here, but any type that can be put in a Bundle is supported
//            val position = bundle.getInt("bundleKey")
//
//            Log.d(TAG, "onCreate: " + position)
//            viewModel.isLiked(position)
//
//        }
//        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("SHOULD_REFRESH")?.observe(viewLifecycleOwner) { shouldRefresh ->
//            shouldRefresh?.run {
//                Log.d(TAG, "onViewCreated: " + this.toString())
//                viewModel.isLiked(0)
//                findNavController().currentBackStackEntry?.savedStateHandle?.set("SHOULD_REFRESH", null)
//            }
//        }

    }

    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)
        binding.theorySalfedjo.setOnClickListener(this)
        binding.literature.setOnClickListener(this)
    }


    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@TheoryFragment.context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == theoryAdapter?.itemCount?.minus(1)
                    ) {
                        viewModel.getpage(true)
//                        setPadding(0, 0, 0, 0)
                    }

                }
            })
            adapter = theoryAdapter
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
//            Log.d(TAG, "setupObservers: " + it.compositors.toString())
            if (it.isFavourite) {
                theoryAdapter?.notifyItemChanged(it.position)
            }

            theoryAdapter?.apply {
                submitList(books = it.books)
            }

        })
    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val searchText = "" + p0.toString()
        viewModel.setSearchText(searchText)
        viewModel.getpage()
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    fun filtersearch(
        enable: CheckBox,
        disable: CheckBox,
        filter: String
    ) {
        Log.d(TAG, "filtersearch: " + enable.isChecked.toString())
        if (enable.isChecked == true) {
            enable.setChecked(true)
            disable.setChecked(false)
            setBookType(filter)
        } else {
            setBookType("")
        }

    }

    fun setBookType(filter: String) {
        viewModel.setBookType(filter)
        viewModel.getpage(false)
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.theorySalfedjo) {
            filtersearch(binding.theorySalfedjo, binding.literature, "SOLFEGGIO_AND_THEORY")
        } else if (p0 == binding.literature) {
            filtersearch(binding.literature, binding.theorySalfedjo, "MUSICAL_LITERATURE")
        }
    }

    override fun onItemSelected(position: Int, item: TheoryInfo) {

        viewModel.state.value?.let { state ->
            val bundle = bundleOf("position" to position)
            findNavController().navigate(R.id.action_homeFragment_to_itemSelectedFragment, bundle)
        }
    }

    override fun onLikeSelected(position: Int) {
        viewModel.isLiked(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        theoryAdapter = null
        _binding = null
    }

}