package com.sili.do_music.presentation.common.itemSelected

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.sili.do_music.R
import com.sili.do_music.databinding.FragmentItemSelectedInstrumentBinding
import com.sili.do_music.presentation.BaseFragment
import com.sili.do_music.util.Constants
import com.sili.do_music.util.Constants.Companion.BOOK_ID
import com.sili.do_music.util.Constants.Companion.FRAGMENT
import com.sili.do_music.util.Constants.Companion.GLIDE_LOGO
import com.sili.do_music.util.Constants.Companion.ITEM_ID
import com.sili.do_music.util.Constants.Companion.NOTE_ID
import com.sili.do_music.util.Constants.Companion.VOCALS_ID
import com.sili.do_music.util.operationErrorDialog
import com.sili.do_music.util.setGradient
import com.sili.do_music.util.shimmerDrawable

class ItemSelectedInstrument : BaseFragment(), View.OnClickListener {
    private val viewModel: ItemSelectedViewModel by viewModels()
    private var _binding: FragmentItemSelectedInstrumentBinding? = null
    private val binding get() = _binding!!

    // Change
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
        setupListener()
        setupObservers()
    }

    private fun setupListener() {
        binding.isFavourite.setOnClickListener(this)
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {

            it?.let {
                if (it.vocal != null || it.book != null || it.instrument != null) {
                    setupViews(it)
                }
            }
        })

        viewModel.errorState.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                when (error.localizedMessage) {
                    Constants.ERROR_ADD_TO_FAVOURITES -> {
                        operationErrorDialog(context)
                    }
                }
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
                        instrument.favorite = !instrument.favorite
                        itemId = instrument.noteId!!
                        isFav = instrument.favorite
                    }
                }
                VOCALS_ID -> {
                    viewModel.state.value?.vocal?.let { vocal ->
                        vocal.favorite = !vocal.favorite
                        itemId = vocal.vocalsId
                        isFav = vocal.favorite
                    }
                }
                BOOK_ID -> {
                    viewModel.state.value?.book?.let { book ->
                        book.favorite = !book.favorite
                        itemId = book.bookId
                        isFav = book.favorite
                    }
                }
            }
            isLiked(itemId,isFav,fragment)
            changeIcon(isFav)
        } else {
            if (v == binding.instrumentDownload) {
                if (fragment == NOTE_ID) {
                    downloadInstrumentClavier()
                } else if (fragment == VOCALS_ID) {
                    downloadVocal()
                }
            } else if (v == binding.instrumentDownload2) {
                downloadInstrumentPart()

            } else {
                downloadBook()
            }
        }
    }

    private fun changeIcon(isFav: Boolean) {
        if (isFav) {
            binding.isFavourite.setImageResource(R.drawable.ic_favourite_enabled_in_card)
        } else {
            binding.isFavourite.setImageResource(R.drawable.ic_favourite_disabled_in_card)
        }
    }

    private fun downloadBook() {
        uiMainCommunicationListener.downloadFile(
            viewModel.state.value?.book?.bookFileId!!,
            viewModel.state.value?.book?.bookFileName!!
        )
    }

    private fun downloadInstrumentPart() {
        uiMainCommunicationListener.downloadFile(
            viewModel.state.value?.instrument?.partId!!,
            viewModel.state.value?.instrument?.partFileName!!
        )
    }

    private fun downloadVocal() {
        uiMainCommunicationListener.downloadFile(
            viewModel.state.value?.vocal?.clavierId!!,
            viewModel.state.value?.vocal?.clavierFileName!!
        )
    }

    private fun downloadInstrumentClavier() {
        uiMainCommunicationListener.downloadFile(
            viewModel.state.value?.instrument?.clavierId!!,
            viewModel.state.value?.instrument?.clavierFileName!!,
        )
    }

    private fun isLiked(favId: Int, isFav: Boolean, property: String){
        uiMainUpdate.isLiked(favId,isFav,property)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}