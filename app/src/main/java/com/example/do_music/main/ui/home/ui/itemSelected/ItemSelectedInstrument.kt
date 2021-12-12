package com.example.do_music.main.ui.home.ui.itemSelected

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.FragmentItemSelectedInstrumentBinding
import com.example.do_music.main.ui.BaseFragment
import com.example.do_music.main.ui.home.ui.instruments.InstrumentsViewModel
import com.example.do_music.main.ui.home.ui.vocals.VocalsViewModel
import com.example.do_music.model.Instrument
import com.example.do_music.model.Vocal
import com.example.do_music.util.setgradient
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.lang.Exception


private const val TAG = "ItemSelectedInstrument"

@AndroidEntryPoint
class ItemSelectedInstrument : BaseFragment(), View.OnClickListener {
    private val viewModel: InstrumentsViewModel by activityViewModels()
    private val viewModelVocal: VocalsViewModel by activityViewModels()
    private lateinit var binding: FragmentItemSelectedInstrumentBinding
    private lateinit var instrument: Instrument
    private lateinit var vocal: Vocal
    private var position: Int = 0
    private var fragment: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemSelectedInstrumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        view.findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        instrument = Instrument(

        )
        position = arguments?.get("position") as Int
        fragment = arguments?.get("fragment") as String
        if(arguments?.get("fragment") as String == "vocal")
        {
            vocal = viewModelVocal.state.value!!.instruments[position]
            instrument = instrument.copy(
                clavierFileName = vocal.clavierFileName,
                clavierId = vocal.clavierId,
                compositorId = vocal.compositorId,
                compositorName = vocal.compositorName,
                logoId = vocal.logoId,
                noteName= vocal.noteName,
                opusEdition = vocal.opusEdition,
                isFavourite = vocal.isFavourite
            )
        }
        else if(arguments?.get("fragment") as String == "instrument"){
            instrument = viewModel.getInstrument(position = position)!!
        }
        else{
            instrument = viewModel.getCompositorNoteInstrument(position = position)!!
        }

        setupViews()
//        setupObservers()
//        createNotificationChannel(channelId)
    }
//
//    private fun setupObservers() {
//        viewModel.progress.observe(
//            viewLifecycleOwner, Observer {
//                if (it != 100) {
//                    builder?.setProgress(100, it, false);
//                    builder?.setContentText("Downloaded: " + it + " %");
//                    manager?.notify(1, builder?.build());
//                } else if (it == 100) {
//                    showDownloadNotification()
//                }
//            }
//        )
//    }
//
//
//    private fun showDownloadNotification() {
//        try {
//              selectedUri: Uri =
//                Uri.parse(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                        .toString() + File.separator.toString() + instrument.clavierFileName
//                )
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(selectedUri, "application/pdf")
//
//            val pendingIntent =
//                PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
//
//            builder?.setContentText("Download completed")
//                ?.setContentIntent(pendingIntent)
//                ?.setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//            // notificationId is a unique int for each notification that you must define
//            manager?.notify(1, builder?.build())
//        } catch (e: Exception) {
//            Log.e(TAG, "Notification $e")
//        }
//    }
//

    private fun setupViews() {
        instrument.partId?.let {
            binding.instrument2.visibility = View.VISIBLE
            binding.instrumentDownload2.visibility = View.VISIBLE
            binding.instrumentDownload2.setOnClickListener(this)
            setgradient(binding.instrumentDownload2)
        }
        instrument.clavierId?.let {
            binding.instrument.visibility = View.VISIBLE
            binding.instrumentDownload.visibility = View.VISIBLE
            binding.instrumentDownload.setOnClickListener(this)
            setgradient(binding.instrumentDownload)
        }
        binding.isFavourite.setOnClickListener(this)
        binding.instrumentAuthor.text = instrument.compositorName
        binding.instrumentName.text = instrument.noteName

        instrument.opusEdition?.let {
            if (it != "") {
                binding.instrumentEditionNotChanged.visibility = View.VISIBLE
                binding.instrumentEditionChanged.visibility = View.VISIBLE
                binding.instrumentEditionChanged.text = instrument.opusEdition
            }
        }

        binding.instrumentEditionChanged2.text = instrument.instrumentName

        if (instrument.isFavourite != false && instrument.isFavourite != null) {
            binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
        } else {
            binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
        }

        Glide.with(binding.root)
            .load("https://domusic.uz/api/doc/logo?uniqueName=" + instrument.logoId)
            .into(binding.firstPageImg)
    }

    override fun onClick(v: View?) {
        if (v == binding.isFavourite) {
            if (instrument.isFavourite != false && instrument.isFavourite != null) {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)
            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
            }
            Log.d(TAG, "onClick: " + fragment)
            if(fragment == "instrument"){
                viewModel.isLiked(position = position, true)
            }
            else if(fragment == "vocal"){
                viewModelVocal.isLikedVocalsNotes(position,true)
            }
            else if(fragment == "compositor"){
                viewModel.isLikedCompositorsNotes(position,true)
            }
        } else {
            if (v == binding.instrumentDownload) {
                download(instrument.clavierFileName!!, instrument.clavierId!!)
            } else if (v == binding.instrumentDownload2) {
                download(instrument.partFileName!!, instrument.partId!!)
            }
        }
    }
}