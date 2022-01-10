package com.example.do_music.main.ui.home.ui.compositors

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentHomeCompositorSelectedBinding
import com.example.do_music.main.ui.home.adapter.InstrumentsAdapter
import com.example.do_music.main.ui.home.adapter.Interaction_Instrument
import com.example.do_music.main.ui.home.ui.instruments.InstrumentsViewModel
import com.example.do_music.util.Constants
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "CompositorSelected"

@AndroidEntryPoint
class HomeCompositorSelectedFragment : Fragment(), TextWatcher, View.OnClickListener,
    Interaction_Instrument {
    private lateinit var _binding: FragmentHomeCompositorSelectedBinding
    private val binding get() = _binding
    private val viewModel: InstrumentsViewModel by viewModels()
    private var instrumentsAdapter: InstrumentsAdapter? = null
    private var compositorId: Int = 0
    private var nameOfCompositor: String = ""

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
        compositorId = arguments?.get("id") as Int
        nameOfCompositor = arguments?.get("nameOfCompositor") as String
        viewModel.setCompositorId(compositorId)
        viewModel.getNotesByCompositor()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.title = nameOfCompositor
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constants.SHOULD_REFRESH)?.observe(viewLifecycleOwner) { shouldRefresh ->
            shouldRefresh?.run {
                viewModel.getpage(update = true)
                findNavController().currentBackStackEntry?.savedStateHandle?.set(Constants.SHOULD_REFRESH, null)
            }
        }
        setupViews()
        setupObservers()
        setupRecyclerView()
    }

    private fun setupObservers() {
        viewModel.notesByCompositor.observe(viewLifecycleOwner, Observer {
            instrumentsAdapter?.apply {
                submitList(instruments = it.instruments)
            }
        })
        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.getpage(update = true)
                viewModel.getNotesByCompositor(update = true)
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == instrumentsAdapter?.itemCount?.minus(1)
                        && viewModel.notesByCompositor.value?.isLoading == false
                    ) {
                        viewModel.getNotesByCompositor(true)
//                        setPadding(0, 0, 0, 0)
                    }

                }
            })
            adapter = instrumentsAdapter
        }
    }


    private fun setupViews() {
        binding.searchEt.addTextChangedListener(this)
        binding.sonata.setOnClickListener(this)
        binding.concerts.setOnClickListener(this)
        viewModel.notesByCompositor.value?.let {
            if(it.searchText!=""){
                binding.searchEt.setText(it.searchText)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        viewModel.setLoadingNotesByCompositorToFalse()
        viewModel.setSearchTextCompositorNotes(searchText)
        viewModel.getNotesByCompositor()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onClick(v: View?) {
        if (v == binding.sonata) {
            filterSearch(binding.sonata,binding.concerts,"SONATAS")
        } else if (v == binding.concerts) {
            filterSearch(binding.concerts,binding.sonata,"CONCERTS_AND_FANTASIES")
        }
        viewModel.getNotesByCompositor()
    }

    override fun onItemSelected(itemId: Int,nameOfCompositor:String) {
        viewModel.notesByCompositor.value?.let { state ->
            val bundle = bundleOf("itemId" to itemId,"fragment" to "noteId")
            findNavController().navigate(R.id.action_homeCompositorSelectedFragment_to_itemSelectedInstrument, bundle)
        }
    }

    override fun onLikeSelected(itemId: Int,isFav:Boolean) {
        viewModel.isLiked(itemId,isFav)
    }

    fun filterSearch(
        enable: CheckBox,
        disable: CheckBox,
        noteGroupType: String
    ) {
        if (enable.isChecked == true) {
            enable.setChecked(true)
            disable.setChecked(false)
            viewModel.noteGroupTypeCompositor(noteGroupType)
        } else {
            viewModel.noteGroupTypeCompositor("")
        }

    }
}
