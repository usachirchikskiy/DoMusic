package com.example.do_music.main.ui.favourites

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.do_music.R
import com.example.do_music.databinding.FragmentFavouriteBinding
import com.example.do_music.main.ui.home.adapter.*
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "FavouriteFragment"


@AndroidEntryPoint
class FavouriteFragment : Fragment(), TextWatcher, RadioGroup.OnCheckedChangeListener,
    Interaction_Favourite, CompoundButton.OnCheckedChangeListener {
    private var arrayList = arrayListOf<CheckBox>()
    private lateinit var _binding: FragmentFavouriteBinding
    private val binding get() = _binding
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
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(docType!!)
            ?.observe(viewLifecycleOwner) { shouldRefresh ->
                shouldRefresh?.run {
                    viewModel.getPage(update = true)
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        docType,
                        true
                    )
                }
            }

        setupViews(docType!!)
        setupObservers()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recv.apply {
            layoutManager = LinearLayoutManager(this@FavouriteFragment.context)
            favouriteAdapter = FavouriteAdapter(interaction = this@FavouriteFragment)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (
                        lastPosition == favouriteAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
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

            showProgressBar(it.isLoading)

            favouriteAdapter?.apply {

                submitList(favourites = it.favouriteItems)
            }

            it.error?.let {
                showError(it)
            }

        })

        viewModel.isUpdated.observe(viewLifecycleOwner, {
            if (it) {
                viewModel.getPage(update = true)
                val docType = viewModel.state.value?.docType

                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    docType!!,
                    true
                )
            }
        })
    }

    private fun showError(it: Throwable) {

    }

    private fun showProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.paginationProgressBar.visibility = View.VISIBLE
        } else {
            binding.paginationProgressBar.visibility = View.INVISIBLE
        }

    }

    private fun setupViews(docType: String) {
        arrayList.add(binding.one)
        arrayList.add(binding.two)
        arrayList.add(binding.three)
        arrayList.add(binding.four)
        arrayList.add(binding.five)
        arrayList.add(binding.six)

        when (docType) {
            "BOOK" -> {
                binding.books.isChecked = true
            }
            "VOCALS" -> {
                binding.vocal.isChecked = true
            }
            else -> {
                binding.vocal.isChecked = true
            }
        }
        binding.searchEt.addTextChangedListener(this)
        binding.groupDocType.setOnCheckedChangeListener(this)
        binding.one.setOnCheckedChangeListener(this)
        binding.two.setOnCheckedChangeListener(this)
        binding.three.setOnCheckedChangeListener(this)
        binding.four.setOnCheckedChangeListener(this)
        binding.five.setOnCheckedChangeListener(this)
        binding.six.setOnCheckedChangeListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val searchText = "" + s.toString()
        viewModel.setSearchText(searchText)
        viewModel.getPage()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    private fun changeState(checkBox: CompoundButton) {
        for (i in arrayList) {
            if (checkBox != i) {
                i.isChecked = false
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

        val radio: RadioButton = group!!.findViewById(checkedId)
        when (radio) {
            binding.notes -> {
                viewModel.setDocType("NOTES")
            }
            binding.books -> {
                viewModel.setDocType("BOOK")
            }
            else -> {
                viewModel.setDocType("VOCALS")
            }
        }

        viewModel.getPage()
    }

    override fun onItemSelected(position: Int) {
        viewModel.state.value?.let { state ->
            var bundle: Bundle? = null
            when (state.docType) {
                "NOTES" -> {
                    val itemId = state.favouriteItems[position].noteId
                    bundle = bundleOf("itemId" to itemId, "fragment" to "noteId")
                }
                "VOCALS" -> {
                    val itemId = state.favouriteItems[position].vocalsId
                    bundle = bundleOf("itemId" to itemId, "fragment" to "vocalsId")
                }
                "BOOK" -> {
                    val itemId = state.favouriteItems[position].bookId
                    bundle = bundleOf("itemId" to itemId, "fragment" to "bookId")
                }
            }

            findNavController().navigate(
                R.id.action_favouriteFragment_to_itemSelectedInstrument2,
                bundle
            )
        }
    }

    override fun onClassSelected(classText: String, position: Int) {
        val favItem = viewModel.state.value!!.favouriteItems[position]
        val favouriteId = favItem.favoriteId
        favouriteId?.let { viewModel.addFavClass(it, classText) }
        Log.d(TAG, "onClassSelected: " + classText)
    }

    override fun onDeleteSelected(itemId: Int, isFav: Boolean, compositorName: String) {

        val dialog = MaterialDialog(this@FavouriteFragment.requireContext())
            .customView(R.layout.are_u_sure_dialog)
        dialog.findViewById<TextView>(R.id.body).text = compositorName
        dialog.findViewById<TextView>(R.id.positive_button).setOnClickListener {
            viewModel.isLiked(favId = itemId, isFav = isFav)
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        var favClass = "UNKNOWN"
        if (buttonView!!.isChecked) {
            changeState(buttonView)
            val btn_txt = buttonView.text.toString()
            if(btn_txt.length>1){
                favClass = "CLASS_" + btn_txt.replace("-","")
            }
            else{
                favClass = "CLASS_" + btn_txt
            }
        }
        viewModel.setFavClass(favClass)
        viewModel.getPage()

    }


}