package com.example.do_music.ui.main.home.ui.compositors.compositorSelected

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
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentHomeCompositorSelectedBinding
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.ui.main.home.adapter.InstrumentsAdapter
import com.example.do_music.ui.main.home.adapter.Interaction_Instrument
import com.example.do_music.ui.main.home.adapter.VocalsAdapter
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.COMPOSITOR_ID
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.INSTRUMENTAL_GROUP
import com.example.do_music.util.Constants.Companion.ITEM_ID
import com.example.do_music.util.Constants.Companion.NAME_OF_COMPOSITOR
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.Constants.Companion.VOCAL_GROUP


private const val TAG = "CompositorSelected"

class HomeCompositorSelectedFragment : BaseFragment(), TextWatcher, View.OnClickListener,
    Interaction_Instrument {
    private var _binding: FragmentHomeCompositorSelectedBinding? = null
    private val binding get() = _binding!!
    private var instrumentsAdapter: InstrumentsAdapter? = null
    private var vocalsAdapter: VocalsAdapter? = null
    private var compositorId: Int = 0
    private var nameOfCompositor: String = ""
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
        nameOfCompositor = arguments?.get(NAME_OF_COMPOSITOR) as String
        if (viewModel.compositorSelectedState.value?.compositorId == -1) {
            Log.d(TAG, "onCreate: ")
            viewModel.setCompositorId(compositorId)
            viewModel.getNotesGroupTypes(compositorId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.title = nameOfCompositor
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constants.SHOULD_REFRESH)
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                shouldRefresh?.run {
                    viewModel.getNotesByCompositorSelected(update = true)
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        Constants.SHOULD_REFRESH,
                        null
                    )
                }
            }
        setupViews()
        setupObservers()
        setupRecyclerView()
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
            if(!it.isLoading && it.vocalCompositions.isEmpty() && it.instrumentalCompositions.isEmpty() && it.searchText.isNotBlank()){
                binding.noResultsLayout.root.visibility = View.VISIBLE
            }

            it.error?.let {
//                TODO
            }

        })

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d(TAG, "setupObservers: " + "HERE")
                viewModel.getNotesByCompositorSelected(update = true)
            }
        })
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
                        ) {
                            viewModel.getNotesByCompositorSelected(next = true)
                        }
                    } else {
                        if (lastPosition == vocalsAdapter?.itemCount?.minus(1)
                            && viewModel.compositorSelectedState.value?.isLoading == false
                        ) {
                            viewModel.getNotesByCompositorSelected(next = true)
                        }
                    }
                }
            })
        }
    }


    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)
        binding.vocalNotes.setOnClickListener(this)
        binding.instrumentalNotes.setOnClickListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        viewModel.setLoadingToFalse()
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
        if (viewModel.compositorSelectedState.value?.filterSelected == INSTRUMENTAL_GROUP) {
            bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to NOTE_ID)
        } else {
            bundle = bundleOf(ITEM_ID to itemId, FRAGMENT to VOCALS_ID)
        }
        findNavController().navigate(
            R.id.action_homeCompositorSelectedFragment_to_itemSelectedInstrument,
            bundle
        )
    }

    override fun onLikeSelected(itemId: Int, isFav: Boolean) {
        viewModel.isLiked(itemId, isFav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        instrumentsAdapter = null
        vocalsAdapter = null
        _binding = null
    }

//    fun filterSearch(
//        enable: CheckBox,
//        disable: CheckBox,
//        noteGroupType: String
//    ) {
//        if (enable.isChecked == true) {
//            enable.setChecked(true)
//            disable.setChecked(false)
////            viewModel.noteGroupTypeCompositor(noteGroupType)
//        } else {
////            viewModel.noteGroupTypeCompositor("")
//        }
//
//    }
}
