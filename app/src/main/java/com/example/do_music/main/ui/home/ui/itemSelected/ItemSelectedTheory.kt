package com.example.do_music.main.ui.home.ui.itemSelected

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.do_music.databinding.FragmentItemSelectedBinding
import com.example.do_music.main.ui.home.ui.theory.TheoryViewModel
import com.example.do_music.model.TheoryInfo
import com.example.do_music.util.setgradient

import com.example.do_music.R

import com.example.do_music.main.ui.BaseFragment

private const val TAG = "ItemSelectedFragment"

class ItemSelectedFragment : BaseFragment(), View.OnClickListener {
    private val viewModel: TheoryViewModel by activityViewModels()
    private lateinit var binding: FragmentItemSelectedBinding
    private lateinit var book: TheoryInfo
    private var position: Int = 0
//    var downloadid: Long = 0
//    private var builder: NotificationCompat.Builder? = null
//    private var manager: NotificationManager? = null
//    lateinit var new: BroadcastReceiver
//    @Inject
//    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
//        setupObservers()

//         new = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//                if (id == downloadid) Log.d("DOWNLOAD", "DONE")
//            }
//        }

//        createNotificationChannel(channelId)
    }
//
//    private fun createNotificationChannel(channelId: String) {
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "DoMusic Channel"
//            val channelDescription = "Channel Description"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//
//            val channel = NotificationChannel(channelId, name, importance)
//            channel.apply {
//                description = channelDescription
//            }
//
//            val notificationManager =
//                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

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

//    private fun showDownloadNotification() {
//        try {
//            val selectedUri: Uri =
//                Uri.parse(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                        .toString() + File.separator.toString() + book.bookFileName
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

    private fun setupViews() {
        binding.bookDownload.setOnClickListener(this)
        binding.isFavourite.setOnClickListener(this)
        binding.bookAuthor.text = book.authorName
        binding.bookName.text = book.bookName
        if(book.opusEdition!=""){
            binding.bookEditionNotChanged.visibility=View.VISIBLE
            binding.bookEditionChanged.visibility=View.VISIBLE
            binding.bookEditionChanged.text = book.opusEdition
        }

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

    override fun onClick(bookBtn: View?) {
        if (bookBtn == binding.isFavourite) {
            if (book.isFavourite != false && book.isFavourite != null) {
                binding.isFavourite.setImageResource(R.drawable.ic_item_not_selected)

            } else {
                binding.isFavourite.setImageResource(R.drawable.ic_favourite_item_selected)
            }
            viewModel.isLiked(position = position, true)

        } else {
            download(book.bookFileName,book.bookFileId)
        }
//            manager =
//                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?;
//            builder = activity?.let {
//                NotificationCompat.Builder(it, channelId)
//                    .setContentTitle("Download " + book.bookFileName)
//                    .setContentText("Downloading your file...")
//                    .setSmallIcon(R.drawable.outline_file_download_20)
//                    .setDefaults(0)
//                    .setAutoCancel(true)
//            }
//            manager?.notify(1, builder?.build())
//            viewModel.downloadFile(book.bookFileId, book.bookFileName)
//            val downloadmanager =
//                activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
//            val uri = Uri.parse("https://domusic.uz/api/doc?uniqueName=" + book.bookFileId)
//            var credentials: String = Credentials.basic("Abdinakibova", "88888888")
//            val request = DownloadManager.Request(uri)
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            request.addRequestHeader("Authorization", credentials)
//            request.setTitle(book.bookFileName)
//            request.setDescription("Downloading") //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
//            request.setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS,
//                book.bookFileName
//            )
//            downloadid = downloadmanager!!.enqueue(request)
//
//            activity?.registerReceiver(new, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


    }

}
