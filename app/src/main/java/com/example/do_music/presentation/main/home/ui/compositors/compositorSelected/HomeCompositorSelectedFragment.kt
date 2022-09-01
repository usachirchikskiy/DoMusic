package com.example.do_music.presentation.main.home.ui.compositors.compositorSelected

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
import com.example.do_music.databinding.FragmentHomeCompositorSelectedBinding
import com.example.do_music.presentation.BaseFragment
import com.example.do_music.presentation.main.home.adapter.InstrumentsAdapter
import com.example.do_music.presentation.main.home.adapter.Interaction_Instrument
import com.example.do_music.presentation.main.home.adapter.VocalsAdapter
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.COMPOSITOR_ID
import com.example.do_music.util.Constants.Companion.ERROR_ADD_TO_FAVOURITES
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.INSTRUMENTAL_GROUP
import com.example.do_music.util.Constants.Companion.ITEM_ID
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.Constants.Companion.VOCAL_GROUP
import com.example.do_music.util.hide
import com.example.do_music.util.operationErrorDialog


private const val TAG = "CompositorSelected"

class HomeCompositorSelectedFragment : BaseFragment(), TextWatcher, View.OnClickListener,
    Interaction_Instrument {
    private var _binding: FragmentHomeCompositorSelectedBinding? = null
    private val binding get() = _binding!!
    private var instrumentsAdapter: InstrumentsAdapter? = null
    private var vocalsAdapter: VocalsAdapter? = null
    private var compositorId: Int = 0
    private val viewModel: HomeCompositorSelectedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeCompositorSelectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositorId = arguments?.get(COMPOSITOR_ID) as Int
        if (viewModel.compositorSelectedState.value?.compositorId == -1) {
            viewModel.setCompositorId(compositorId)
            viewModel.getNotesGroupTypes(compositorId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateData()
        setupViews()
        setupObservers()
        setupRecyclerView()
    }

    private fun updateData() {
        if (uiMainUpdate.getInstrumentsUpdate()) {
            viewModel.getNotesByCompositorSelected(update = true)
            uiMainUpdate.setInstrumentsUpdate(false)
        } else if (uiMainUpdate.getVocalsUpdate()) {
            viewModel.getNotesByCompositorSelected(update = true)
            uiMainUpdate.setVocalsUpdate(false)
        }
    }

    

    private fun setupObservers() {
        viewModel.compositorSelectedState.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setupObservers: " + it)

            uiCommunicationListener.displayProgressBar(it.isLoading)

            if (it.groupFilters.size == 2) {
                if (binding.instrumentalNotes.text.isBlank()) {
                    binding.instrumentalNotes.text = getString(R.string.instrumental)
                    binding.instrumentalNotes.isChecked = true
                    binding.instrumentalNotes.visibility = View.VISIBLE
                    binding.vocalNotes.text = getString(R.string.vocal)
                    binding.vocalNotes.visibility = View.VISIBLE
                }
            } else if (it.groupFilters.size == 1) {
                if (it.groupFilters.contains(INSTRUMENTAL_GROUP) &&
                    binding.instrumentalNotes.text.isBlank()
                ) {
                    binding.instrumentalNotes.text = getString(R.string.instrumental)
                    binding.instrumentalNotes.isChecked = true
                    binding.instrumentalNotes.visibility = View.VISIBLE
                } else if (it.groupFilters.contains(VOCAL_GROUP) &&
                    binding.instrumentalNotes.text.isBlank()
                ) {
                    binding.instrumentalNotes.text = getString(R.string.vocal)
                    binding.instrumentalNotes.isChecked = true
                    binding.instrumentalNotes.visibility = View.VISIBLE
                }
            }

            if (it.filterSelected == INSTRUMENTAL_GROUP) {
                instrumentsAdapter?.apply {
                    binding.noResultsLayout.root.visibility = View.GONE
                    if (binding.recv.adapter == null || binding.recv.adapter == vocalsAdapter) {
                        binding.recv.adapter = this
                    }
                    submitList(instruments = it.instrumentalCompositions)
                }
            } else {
                vocalsAdapter?.apply {
                    binding.noResultsLayout.root.visibility = View.GONE
                    if (binding.recv.adapter == null || binding.recv.adapter == instrumentsAdapter) {
                        binding.recv.adapter = this
                    }
                    submitList(vocal = it.vocalCompositions)
                }
            }
            if (!it.isLoading && it.vocalCompositions.isEmpty() && it.instrumentalCompositions.isEmpty() && it.searchText.isNotBlank()) {
                binding.noResultsLayout.root.visibility = View.VISIBLE
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
                    ERROR_ADD_TO_FAVOURITES -> {
                        operationErrorDialog(context)
                        viewModel.setErrorNull()
                    }
                }
            }

        })

//        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
//            Log.d(TAG, "setupObservers: UPDATE $it")
//            if (it) {
//                viewModel.getNotesByCompositorSelected(update = true)
//                viewModel.isUpdated.value = false
//            }
//        })
    }

    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@HomeCompositorSelectedFragment.context)
            instrumentsAdapter = InstrumentsAdapter(
                context = requireContext(),
                interaction = this@HomeCompositorSelectedFragment,
                fragmentName = "HomeCompositorSelected"
            )
            vocalsAdapter = VocalsAdapter(
                context = requireContext(),
                interaction = this@HomeCompositorSelectedFragment,
                fragmentName = "HomeCompositorSelected"
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (viewModel.compositorSelectedState.value?.filterSelected == INSTRUMENTAL_GROUP) {
                        if (
                            lastPosition == instrumentsAdapter?.itemCount?.minus(1)
                            && viewModel.compositorSelectedState.value?.isLoading == false
                            && viewModel.isLastPage.value == false
                        ) {
                            viewModel.getNotesByCompositorSelected(next = true)
                        }
                    } else {
                        if (lastPosition == vocalsAdapter?.itemCount?.minus(1)
                            && viewModel.compositorSelectedState.value?.isLoading == false
                            && viewModel.isLastPage.value == false
                        ) {
                            viewModel.getNotesByCompositorSelected(next = true)
                        }
                    }
                }
            })
        }
    }


    private fun setupViews() {
        binding.searchEt.hide {
            uiCommunicationListener.hideKeyboard()
        }
        binding.searchEt.addTextChangedListener(this)
        binding.vocalNotes.setOnClickListener(this)
        binding.instrumentalNotes.setOnClickListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
//        viewModel.setLoadingToFalse()
        Log.d(TAG, "onTextChanged: ")
        viewModel.setSearchText(searchText)
        viewModel.getNotesByCompositorSelected()
    }


    override fun afterTextChanged(s: Editable?) {

    }

    override fun onClick(v: View?) {
        if (v == binding.instrumentalNotes) {
            viewModel.setFilterSelected(INSTRUMENTAL_GROUP)
        } else if (v == binding.vocalNotes) {
            viewModel.setFilterSelected(VOCAL_GROUP)
        }
        viewModel.getNotesByCompositorSelected()
    }

    override fun onItemSelected(itemId: Int, nameOfCompositor: String) {
        var bundle: Bundle? = null
        bundle =
            if (viewModel.compositorSelectedState.value?.filterSelected == INSTRUMENTAL_GROUP) {
                bundleOf(ITEM_ID to itemId, FRAGMENT to NOTE_ID)
            } else {
                bundleOf(ITEM_ID to itemId, FRAGMENT to VOCALS_ID)
            }
        findNavController().navigate(
            R.id.action_homeCompositorSelectedFragment_to_itemSelectedInstrument,
            bundle
        )
    }

    override fun onLikeSelected(itemId: Int, isFav: Boolean) {
        viewModel.isLiked(itemId, isFav)
        uiMainUpdate.setFavouriteUpdate(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        instrumentsAdapter = null
        vocalsAdapter = null
        _binding = null
    }

}
