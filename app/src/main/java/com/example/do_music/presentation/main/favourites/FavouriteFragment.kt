package com.example.do_music.presentation.main.favourites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentFavouriteBinding
import com.example.do_music.presentation.BaseFragment
import com.example.do_music.presentation.main.home.adapter.*
import com.example.do_music.util.*
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.ITEM_ID
import com.example.do_music.util.Constants.Companion.NOTES
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FavouriteFragment"

@AndroidEntryPoint
class FavouriteFragment : BaseFragment(), TextWatcher,
    Interaction_Favourite, View.OnClickListener {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavouriteViewModel by viewModels()
    private var favouriteAdapter: FavouriteAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val docType = viewModel.state.value?.docType
        setupViews(docType!!)
        updateFavourite()
        setupObservers()
        setupRecyclerView()
    }

    private fun updateFavourite() {
        if(uiMainUpdate.getFavouriteUpdate()){
            viewModel.getPage()
            uiMainUpdate.setFavouriteUpdate(false)
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: ")
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@FavouriteFragment.context)
            favouriteAdapter = FavouriteAdapter(interaction = this@FavouriteFragment)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        (lastPosition == favouriteAdapter?.itemCount?.minus(1)
                                && viewModel.state.value?.isLoading == false) && !viewModel.isLastPage
                    ) {
                        viewModel.getPage(true)
//                        setPadding(0, 0, 0, 0)
                    }

                }
            })
            adapter = favouriteAdapter
        }

    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, {

            uiCommunicationListener.displayProgressBar(it.isLoading)
            Log.d(TAG, "setupObservers: $it")
            favouriteAdapter?.apply {
                submitList(favourites = it.favouriteItems)
                if (it.favouriteItems.isEmpty() && !it.isLoading && it.searchText.isBlank()) {
                    binding.notAddedLayout.root.visibility = View.VISIBLE
                } else {
                    binding.notAddedLayout.root.visibility = View.GONE
                }

                if (it.favouriteItems.isEmpty() && !it.isLoading && it.searchText.isNotBlank()) {
                    binding.noResultsLayout.root.visibility = View.VISIBLE
                } else {
                    binding.noResultsLayout.root.visibility = View.GONE
                }
            }

            it.error?.let { error ->
                when (error.localizedMessage) {
                    Constants.ERROR_ADD_TO_FAVOURITES -> {
                        operationErrorDialog(context)
                        viewModel.setErrorNull()
                    }
                }
            }

        })

    }


    private fun setupViews(docType: String) {
        binding.searchEt.hide {
            uiCommunicationListener.hideKeyboard()
        }
        when (docType) {
            BOOK -> {
                binding.books.isChecked = true
            }
            VOCALS -> {
                binding.vocal.isChecked = true
            }
            NOTES -> {
                binding.notes.isChecked = true
            }
        }
        binding.books.setOnClickListener(this)
        binding.vocal.setOnClickListener(this)
        binding.notes.setOnClickListener(this)
        binding.searchEt.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        Log.d(TAG, "onTextChanged: " + s.toString())
        viewModel.setSearchText(searchText)
        viewModel.getPage()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onItemSelected(position: Int) {
        viewModel.state.value?.let { state ->
            var bundle: Bundle? = null
            when (state.docType) {
                NOTES -> {
                    val itemId = state.favouriteItems[position].noteId
                    bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to NOTE_ID)
                }
                VOCALS -> {
                    val itemId = state.favouriteItems[position].vocalsId
                    bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to VOCALS_ID)
                }
                BOOK -> {
                    val itemId = state.favouriteItems[position].bookId
                    bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to BOOK_ID)
                }
            }

            findNavController().navigate(
                R.id.action_favouriteFragment_to_itemSelectedInstrument,
                bundle
            )
        }
    }

    override fun onDeleteSelected(itemId: Int, isFavourite: Boolean, compositorName: String) {
        deleteNote(context,
            compositorName = compositorName,
            stateMessageCallback = object : StateMessageCallback {
                override fun yes() {
                    viewModel.isLiked(itemId = itemId, isFavourite = isFavourite)
                    setUpdate()
                }
            })
    }

    private fun setUpdate(){
        when(viewModel.state.value?.docType){
                NOTES -> {
                    uiMainUpdate.setInstrumentsUpdate(true)
                }
                BOOK -> {
                    uiMainUpdate.setTheoryUpdate(true)
                }
                else -> {
                    uiMainUpdate.setVocalsUpdate(true)
                }
        }
    }
    override fun onClick(v: View?) {
        when (v) {
            binding.notes -> {
                viewModel.setDocType(NOTES)
            }
            binding.books -> {
                viewModel.setDocType(BOOK)
            }
            else -> {
                viewModel.setDocType(VOCALS)
            }
        }
        viewModel.getPage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favouriteAdapter = null
        _binding = null

    }

}