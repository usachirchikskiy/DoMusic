package com.example.do_music.presentation.common.itemSelected

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.FragmentItemSelectedInstrumentBinding
import com.example.do_music.presentation.BaseFragment
import com.example.do_music.util.Constants
import com.example.do_music.util.Constants.Companion.BOOK
import com.example.do_music.util.Constants.Companion.BOOK_ID
import com.example.do_music.util.Constants.Companion.FRAGMENT
import com.example.do_music.util.Constants.Companion.GLIDE_LOGO
import com.example.do_music.util.Constants.Companion.ITEM_ID
import com.example.do_music.util.Constants.Companion.NOTES
import com.example.do_music.util.Constants.Companion.NOTE_ID
import com.example.do_music.util.Constants.Companion.VOCALS
import com.example.do_music.util.Constants.Companion.VOCALS_ID
import com.example.do_music.util.addToFavErrorDialog
import com.example.do_music.util.setGradient
import com.example.do_music.util.shimmerDrawable


private const val TAG = "ItemSelectedInstrument"

class ItemSelectedInstrument : BaseFragment(), View.OnClickListener {
    private val viewModel: ItemSelectedViewModel by viewModels()
    private var _binding: FragmentItemSelectedInstrumentBinding? = null
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

//            it.body?.let {
//                Log.d(TAG, "setupObservers: Done")
//                viewModel.saveFile(context)
//            }
            it.error?.let { error ->
                when (error.localizedMessage) {
                    Constants.ERROR_ADD_TO_FAVOURITES -> {
                        addToFavErrorDialog(context)
                        viewModel.setErrorNull()
                    }
                }
            }

        })

        viewModel.isUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.getItem(itemId, fragment)
                var key = ""
                key = when (fragment) {
                    NOTE_ID -> {
                        NOTES
                    }
                    BOOK_ID -> {
                        BOOK
                    }
                    else -> {
                        VOCALS
                    }
                }
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    key,
                    true
                )
            }
        })
    }

    private fun setupViews(item: ItemState) {
        var logo = ""
        item.instrument?.let {
            binding.instrumentAuthor.text = it.compositorName
            binding.nameOfItem.text = it.noteName
            logo = it.logoId.toString()
            it.partId?.let {
                binding.instrument2.visibility = View.VISIBLE
                binding.instrumentDownload2.visibility = View.VISIBLE
                binding.instrumentDownload2.setOnClickListener(this)
                setGradient(binding.instrumentDownload2)
            }
            it.clavierId?.let {
                binding.instrument.visibility = View.VISIBLE
                binding.instrumentDownload.visibility = View.VISIBLE
                binding.instrumentDownload.setOnClickListener(this)
                setGradient(binding.instrumentDownload)
            }
            it.opusEdition?.let { opusEdition ->
                if (opusEdition != "") {
                    binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.text = it.opusEdition
                }
            }
            it.instrumentName?.let { instrumentName ->
                if (instrumentName != "") {
                    binding.instrumentEditionNotChanged2.visibility = View.VISIBLE
                    binding.instrumentEditionChanged2.text = it.instrumentName
                }
            }
            if (!viewModel.state.value?.instrument?.favorite!!) {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_disabled_in_card)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_enabled_in_card)
            }
        }

        item.book?.let {
            logo = it.logoId
            binding.instrumentAuthor.text = it.authorName
            binding.nameOfItem.text = it.bookName
            binding.bookChangedDownload.visibility = View.VISIBLE
            binding.bookNotChangedDownload.visibility = View.VISIBLE
            setGradient(binding.bookChangedDownload)
            binding.bookChangedDownload.setOnClickListener(this)
            it.opusEdition.let { opusEdition ->
                if (opusEdition != "") {
                    binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.text = it.opusEdition
                }
            }
            if (!viewModel.state.value?.book?.favorite!!) {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_disabled_in_card)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_enabled_in_card)
            }
        }

        item.vocal?.let {
            logo = it.logoId
            binding.instrumentAuthor.text = it.compositorName
            binding.nameOfItem.text = it.noteName
            binding.instrument.visibility = View.VISIBLE
            binding.instrumentDownload.visibility = View.VISIBLE
            binding.instrumentDownload.setOnClickListener(this)
            setGradient(binding.instrumentDownload)
            it.opusEdition.let { opusEdition ->
                if (opusEdition != "") {
                    binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.visibility = View.VISIBLE
                    binding.instrumentEditionChanged.text = it.opusEdition
                }
            }
            if (!viewModel.state.value?.vocal?.favorite!!) {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_disabled_in_card)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_enabled_in_card)
            }
        }
        Glide.with(binding.root)
            .load(GLIDE_LOGO + logo)
            .placeholder(shimmerDrawable)
            .into(binding.firstPageImg)
    }

    override fun onClick(v: View?) {
        var itemId = 0
        var isFav = false
        if (v == binding.isFavourite) {
            when (fragment) {
                NOTE_ID -> {
                    viewModel.state.value?.instrument?.let { instrument ->
                        instrument.favorite?.let {
                            isFav = !it
                            itemId = if (it) {
                                instrument.favoriteId!!
                            } else {
                                instrument.noteId!!
                            }
                        }
                    }

                }
                VOCALS_ID -> {
                    viewModel.state.value?.vocal?.let { vocal ->
                        vocal.favorite?.let {
                            isFav = !it
                            itemId = if (it) {
                                vocal.favoriteId!!
                            } else {
                                vocal.vocalsId
                            }
                        }
                    }
                }
                BOOK_ID -> {
                    viewModel.state.value?.book?.let { book ->
                        book.favorite.let {
                            isFav = !it
                            itemId = if (it) {
                                book.favoriteId!!
                            } else {
                                book.bookId
                            }
                        }
                    }
                }
            }
            viewModel.isLiked(itemId, isFav, fragment)
        } else {
            if (v == binding.instrumentDownload) {
                if (fragment == NOTE_ID) {
                    viewModel.state.value!!.instrument?.let { instrument ->
                        Log.d(TAG, "onClick: ${instrument.clavierFileName}")
                        uiMainCommunicationListener.downloadFile(
                            viewModel.state.value?.instrument?.clavierId!!,
                            viewModel.state.value?.instrument?.clavierFileName!!,
                        )
                    }
                } else if (fragment == VOCALS_ID) {
                    uiMainCommunicationListener.downloadFile(
                        viewModel.state.value?.vocal?.clavierFileName!!,
                        viewModel.state.value?.vocal?.clavierId!!
                    )
                }
            } else if (v == binding.instrumentDownload2) {
                uiMainCommunicationListener.downloadFile(
                    viewModel.state.value?.instrument?.partId!!,
                    viewModel.state.value?.instrument?.partFileName!!
                )
            } else {
                uiMainCommunicationListener.downloadFile(
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