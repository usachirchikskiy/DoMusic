package com.example.do_music.ui.main.home.ui.itemSelected

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.FragmentItemSelectedInstrumentBinding
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.ITEM_ID
import com.example.do_music.util.Constants.Companion.NOTES
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.setgradient


private const val TAG = "ItemSelectedInstrument"

class ItemSelectedInstrument : BaseFragment(), View.OnClickListener {
    private val viewModel: ItemSelectedViewModel by viewModels()
    private var _binding: FragmentItemSelectedInstrumentBinding?=null
    private val binding get() = _binding!!
    private var itemId: Int = 0
    private var fragment: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemSelectedInstrumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemId = arguments?.get(ITEM_ID) as Int
        fragment = arguments?.get(FRAGMENT) as String
        viewModel.getItem(itemId = itemId, fragmentName = fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.isFavourite.setOnClickListener(this)
        setupObservers()

    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {

            it?.let {
                if (it.vocal != null || it.book != null || it.instrument != null) {
                    setupViews(it)
                }
            }

        })

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.getItem(itemId, fragment)
                var key = ""
                if (fragment == NOTE_ID) {
                    key = NOTES
                } else if (fragment == BOOK_ID) {
                    key = BOOK
                } else {
                    key = VOCALS
                }
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    key,
                    true
                )
            }
        })
    }

    private fun setupViews(item: ItemState) {
        var logo: String = ""
        item.instrument?.let {
            binding.instrumentAuthor.text = it.compositorName
            binding.nameOfItem.text = it.noteName
            logo = it.logoId.toString()
            it.partId?.let {
                binding.instrument2.visibility = View.VISIBLE
                binding.instrumentDownload2.visibility = View.VISIBLE
                binding.instrumentDownload2.setOnClickListener(this)
                setgradient(binding.instrumentDownload2)
            }
            it.clavierId?.let {
                binding.instrument.visibility = View.VISIBLE
                binding.instrumentDownload.visibility = View.VISIBLE
                binding.instrumentDownload.setOnClickListener(this)
                setgradient(binding.instrumentDownload)
            }
            it.opusEdition?.let { opusEdition ->
                if (opusEdition != "") {
                    binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.text = it.opusEdition
                }
            }
            it.instrumentName?.let { instrumentName ->
                binding.instrumentEditionNotChanged2.visibility = View.VISIBLE
                binding.instrumentEditionChanged2.text = it.instrumentName
            }
            if (!viewModel.state.value?.instrument?.favorite!!) {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
            }
        }

        item.book?.let {
            logo = it.logoId
            binding.instrumentAuthor.text = it.authorName
            binding.nameOfItem.text = it.bookName
            binding.bookChangedDownload.visibility = View.VISIBLE
            binding.bookNotChangedDownload.visibility = View.VISIBLE
            setgradient(binding.bookChangedDownload)
            binding.bookChangedDownload.setOnClickListener(this)
            it.opusEdition.let { opusEdition ->
                if (opusEdition != "") {
                    binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.text = it.opusEdition
                }
            }
            if (!viewModel.state.value?.book?.favorite!!) {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
            }
        }

        item.vocal?.let {
            logo = it.logoId
            binding.instrumentAuthor.text = it.compositorName
            binding.nameOfItem.text = it.noteName
            binding.instrument.visibility = View.VISIBLE
            binding.instrumentDownload.visibility = View.VISIBLE
            binding.instrumentDownload.setOnClickListener(this)
            setgradient(binding.instrumentDownload)
            it.opusEdition.let { opusEdition ->
                if (opusEdition != "") {
                    binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.text = it.opusEdition
                }
            }
            if (!viewModel.state.value?.vocal?.favorite!!) {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
            }
        }
        Glide.with(binding.root)
            .load("https://domusic.uz/api/doc/logo?uniqueName=" + logo)
            .into(binding.firstPageImg)
    }

    override fun onClick(v: View?) {
        var itemId: Int = 0
        var isFav: Boolean = false
        if (v == binding.isFavourite) {
            if (fragment == NOTE_ID) {
                viewModel.state.value?.instrument?.let { instrument ->
                    instrument.favorite?.let {
                        isFav = !it
                        if (it) {
                            itemId = instrument.favoriteId!!
                        } else {
                            itemId = instrument.noteId!!
                        }
                    }
                }

            } else if (fragment == VOCALS_ID) {
                viewModel.state.value?.vocal?.let { vocal ->
                    vocal.favorite?.let {
                        isFav = !it
                        if (it) {
                            itemId = vocal.favoriteId!!
                        } else {
                            itemId = vocal.vocalsId
                        }
                    }
                }
            } else if (fragment == BOOK_ID) {
                viewModel.state.value?.book?.let { book ->
                    book.favorite.let {
                        isFav = !it
                        if (it) {
                            itemId = book.favoriteId!!
                        } else {
                            itemId = book.bookId
                        }
                    }
                }
            }

            viewModel.isLiked(itemId, isFav, fragment)


        } else {
            if (v == binding.instrumentDownload) {
                if (fragment == NOTE_ID) {
                    uiCommunicationListener.downloadFile(
                        viewModel.state.value?.instrument?.clavierFileName!!,
                        viewModel.state.value?.instrument?.clavierId!!
                    )
                } else if (fragment == VOCALS_ID) {
                    uiCommunicationListener.downloadFile(
                        viewModel.state.value?.vocal?.clavierFileName!!,
                        viewModel.state.value?.vocal?.clavierId!!
                    )
                }
            } else if (v == binding.instrumentDownload2) {
                uiCommunicationListener.downloadFile(
                    viewModel.state.value?.instrument?.clavierFileName!!,
                    viewModel.state.value?.instrument?.clavierId!!
                )
            } else {
                Log.d(
                    TAG, "onClick: " + viewModel.state.value?.book?.bookFileName!! + "\n" +
                            viewModel.state.value?.book?.bookFileId!!
                )
                uiCommunicationListener.downloadFile(
                    viewModel.state.value?.book?.bookFileName!!,
                    viewModel.state.value?.book?.bookFileId!!
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}