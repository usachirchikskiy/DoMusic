package com.example.do_music.main.ui.home.ui.itemSelected

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.FragmentItemSelectedBinding
import com.example.do_music.main.ui.home.ui.theory.TheoryFragment
import com.example.do_music.main.ui.home.ui.theory.TheoryViewModel
import com.example.do_music.model.TheoryInfo
import com.example.do_music.util.setgradient
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "ItemSelectedFragment"

@AndroidEntryPoint
class ItemSelectedFragment : Fragment(), View.OnClickListener {
//        private var changed = false
    private val viewModel: TheoryViewModel by activityViewModels()
    private lateinit var binding: FragmentItemSelectedBinding
    private lateinit var book: TheoryInfo
    private var position: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemSelectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        view.findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)

        position = arguments?.get("position") as Int
        book = viewModel.getBook(position = position)!!
        setupViews()

    }

    private fun setupViews() {
        binding.bookDownload.setOnClickListener(this)
        binding.isFavourite.setOnClickListener(this)
        if (book != null) {
            binding.bookAuthor.text = book.authorName
            binding.bookName.text = book.bookName
            binding.bookEditionChanged.text = book.opusEdition
            setgradient(binding.bookDownload)
            if (book.isFavourite != false && book.isFavourite != null) {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
            }

            Glide.with(binding.root)
                .load("https://domusic.uz/api/doc/logo?uniqueName=" + book.logoId)
                .into(binding.firstPageImg)
        }

    }

    override fun onClick(bookBtn: View?) {
//        changed = changed ==false
        if (bookBtn == binding.isFavourite) {
            Log.d(TAG, "onClick: " + book.isFavourite.toString())
            if (book.isFavourite != false && book.isFavourite != null) {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
//            book.isFavourite = false
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
//            book.isFavourite = true
            }
            viewModel.isLiked(position = position,true)
        }
        else{
            viewModel.downloadFile(book.bookFileId,requireContext(),book.bookFileName)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        if(changed==true) {
//
//            setFragmentResult("KEY", bundleOf("data" to "button clicked"))
//            Log.d(TAG, "onDestroyView: ")
//            findNavController().navigateUp()
////            findNavController().previousBackStackEntry?.savedStateHandle?.set("key", position)
//        }
//            parentFragmentManager.setFragmentResult(
//                TheoryFragment.REQUEST_KEY,
//                bundleOf(TheoryFragment.KEY_NUMBER to position)
//            )

//            Log.d(TAG, "onDestroyView: ")
//            val result = position
//            // Use the Kotlin extension in the fragment-ktx artifact
//            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
    }
}
