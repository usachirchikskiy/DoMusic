package com.sili.do_music.presentation.main.home.ui.theory

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
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentTheoryBinding
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.presentation.main.home.adapter.Interaction_Instrument
import com.sili.do_music.presentation.main.home.adapter.TheoryAdapter
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.BOOK_ID
import com.sili.do_music.util.Constants.Companion.FRAGMENT
import com.sili.do_music.util.Constants.Companion.ITEM_ID
import com.sili.do_music.util.operationErrorDialog
import com.sili.do_music.util.hide


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
        setupObservers()
        setupRecyclerView()
        setupViews()
    }

    private fun setupViews() {
        binding.searchEt.hide {
            uiCommunicationListener.hideKeyboard()
        }
        binding.searchEt.addTextChangedListener(this)
        binding.theorySalfedjo.setOnClickListener(this)
        binding.literature.setOnClickListener(this)
        binding.choreography.setOnClickListener(this)
        viewModel.state.value?.let {
            if (it.searchText.isNotBlank()) {
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
                        && viewModel.state.value?.isLastPage == false
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
            uiCommunicationListener.displayProgressBar(it.isLoading)

            theoryAdapter?.apply {
                if (!it.isLoading && it.books.isEmpty() && it.searchText.isNotBlank()) {
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                } else {
                    binding.noResultsLayout.root.visibility = View.GONE
                }
                submitList(books = it.books)
            }

            it.error?.let { error ->
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
        disable1: CheckBox,
        disable2: CheckBox,
        filter: String
    ) {
        if (enable.isChecked) {
            enable.isChecked = true
            disable1.isChecked = false
            disable2.isChecked = false
            setBookType(filter)
        } else {
            setBookType("")
        }

    }

    private fun setBookType(filter: String) {
        viewModel.setBookType(filter)
        viewModel.getPage()
    }

    override fun onClick(p0: View?) {
        if (p0 == binding.theorySalfedjo) {
            filterSearch(
                binding.theorySalfedjo,
                binding.literature,
                binding.choreography,
                "SOLFEGGIO_AND_THEORY"
            )
        } else if (p0 == binding.literature) {
            filterSearch(
                binding.literature,
                binding.theorySalfedjo,
                binding.choreography,
                "MUSICAL_LITERATURE"
            )
        } else if (p0 == binding.choreography) {
            filterSearch(
                binding.choreography,
                binding.literature,
                binding.theorySalfedjo,
                "CHOREOGRAPHY"
            )
        }
    }

    override fun onItemSelected(itemId: Int, nameOfCompositor: String) {
        viewModel.state.value?.let {
            val bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to BOOK_ID)
            findNavController().navigate(R.id.action_homeFragment_to_itemSelectedInstrument, bundle)
        }
    }

    override fun onLikeSelected(itemId: Int, isFav: Boolean) {
        uiMainUpdate.isLiked(itemId, isFav, BOOK_ID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        theoryAdapter = null
        _binding = null
    }

}