package com.example.do_music.ui.main.home.ui.theory

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
import com.example.do_music.R
import com.example.do_music.databinding.FragmentTheoryBinding
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.ui.main.home.adapter.Interaction_Instrument
import com.example.do_music.ui.main.home.adapter.TheoryAdapter
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.ITEM_ID


private const val TAG = "TheoryFragment"

class TheoryFragment : BaseFragment(), TextWatcher, View.OnClickListener, Interaction_Instrument {

    private var theoryAdapter: TheoryAdapter? = null
    private var _binding: FragmentTheoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TheoryViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTheoryBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            BOOK
        )?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {

                viewModel.getPage(update = true)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    BOOK,
                    null
                )
            }
        }
        Log.d(TAG, "onViewCreated: " + viewModel.toString())
        setupObservers()
        setupRecyclerView()
        setupViews()

    }

    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)
        binding.theorySalfedjo.setOnClickListener(this)
        binding.literature.setOnClickListener(this)
        viewModel.state.value?.let {
            if(it.searchText.isNotBlank()){
                binding.searchEt.setText(it.searchText)
            }
        }
    }


    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@TheoryFragment.context)
            theoryAdapter = TheoryAdapter(this@TheoryFragment)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == theoryAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                    ) {
                        viewModel.getPage(true)
                    }

                }
            })
            adapter = theoryAdapter
        }

    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setupObservers: " + it)
            uiCommunicationListener.displayProgressBar(it.isLoading)
//            showProgressBar(it.isLoading)

            theoryAdapter?.apply {
                if(!it.isLoading && it.books.isEmpty() && it.searchText.isNotBlank()){
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                }
                else{
                    binding.noResultsLayout.root.visibility = View.GONE
                }
                submitList(books = it.books)
            }

            it.error?.let {

            }

        })

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer
        {
            if (it) {
                viewModel.getPage(update = true)
            }
        })
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val searchText = "" + p0.toString()
        Log.d(TAG, "onTextChanged: ")
        viewModel.setLoadingToFalse()
        viewModel.setSearchText(searchText)
        viewModel.getPage()

    }

    override fun afterTextChanged(p0: Editable?) {

    }

    fun filtersearch(
        enable: CheckBox,
        disable: CheckBox,
        filter: String
    ) {
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
        viewModel.getPage(false)
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.theorySalfedjo) {
            filtersearch(binding.theorySalfedjo, binding.literature, "SOLFEGGIO_AND_THEORY")
        } else if (p0 == binding.literature) {
            filtersearch(binding.literature, binding.theorySalfedjo, "MUSICAL_LITERATURE")
        }
    }

    override fun onItemSelected(itemId: Int, nameOfCompositor: String) {
        viewModel.state.value?.let { state ->
            val bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to BOOK_ID)
            findNavController().navigate(R.id.action_homeFragment_to_itemSelectedInstrument, bundle)
        }
    }

    override fun onLikeSelected(itemId: Int, isFav: Boolean) {
        viewModel.isLiked(itemId, isFav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        theoryAdapter = null
        _binding = null
    }

}