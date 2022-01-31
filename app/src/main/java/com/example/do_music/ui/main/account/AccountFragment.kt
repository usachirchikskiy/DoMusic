package com.example.do_music.ui.main.account

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.do_music.R
import com.example.do_music.databinding.ExpandAboutUsBinding
import com.example.do_music.databinding.ExpandPersonalBinding
import com.example.do_music.databinding.ExpandTechSupportBinding
import com.example.do_music.databinding.FragmentAccountBinding
import com.example.do_music.ui.StateMessageCallback
import com.example.do_music.ui.logoutDialog
import com.example.do_music.ui.main.BaseFragment
import com.example.do_music.util.Constants.Companion.AUTH_ERROR
import com.example.do_music.util.setgradient


private const val TAG = "AccountFragment"

class AccountFragment : BaseFragment(), View.OnClickListener {

    private lateinit var radioGroup: RadioGroup
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()
    private var content: ExpandPersonalBinding? = null
    private var aboutUs: ExpandAboutUsBinding? = null
    private var feedBack: ExpandTechSupportBinding? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    private val filesUploadActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return Intent(Intent.ACTION_PICK).setType("*/*")

        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    private lateinit var fileUploadActivityResultLauncher: ActivityResultLauncher<Any?>
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileUploadActivityResultLauncher =
            registerForActivityResult(filesUploadActivityResultContract) { uri ->
                uri?.let {

                    uiCommunicationListener.uploadPhotoToServer(uri,
                        stateMessageCallback = object : StateMessageCallback {
                            override fun yes() {

                            }

                            override fun uploadPhoto(selectedFilePath: String) {
                                viewModel.addToUploadFiles(selectedFilePath)
                            }
                        }
                    )
                }
            }

        cropActivityResultLauncher =
            registerForActivityResult(cropActivityResultContract) { uri ->
                uri?.let {
                    Glide.with(binding.root)
                        .load(uri)
                        .into(binding.profilePhoto)

                    uiCommunicationListener.uploadPhotoToServer(uri,
                        stateMessageCallback = object : StateMessageCallback {
                            override fun yes() {

                            }

                            override fun uploadPhoto(selectedImagePath: String) {
                                viewModel.uploadPhoto(selectedImagePath)
                            }
                        }
                    )
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiCommunicationListener.showBottomNavigation(true)
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner, {
            it.userAccount?.let { userAccount ->
                Log.d(TAG, "setupObservers: " + userAccount)
                userAccount.avatarId?.let { avatar ->
                    if (avatar.isNotBlank()) {
                        Glide.with(binding.root)
                            .load("https://domusic.uz/api/doc/logo?uniqueName=" + avatar)
                            .into(binding.profilePhoto)
                    } else {
                        binding.profilePhoto.setImageResource(R.drawable.avatar_empty)
                    }
                }
                content?.userLogin?.setText(userAccount.username)
                content?.fio?.setText(userAccount.fio)
                content?.telephonNumber?.setText(userAccount.phone)

                userAccount.email?.let { email ->
                    val sb = StringBuilder(email)
                    val index = sb.indexOf("@")

                    if (sb.length > 20 && index != -1) {
                        sb.replace(index - 6, index, "")
                        sb.insertRange(index - 6, "...", 0, 3)
                    }
                    content?.email?.setText(sb.toString())
                }
                userAccount.regionId?.let {
                    content?.region?.setText(userAccount.regionId)
                }
                userAccount.cityId?.let {
                    content?.town?.setText(userAccount.cityId.toString())
                }
                userAccount.schoolId?.let {
                    content?.school?.setText(userAccount.schoolId.toString())
                }

            }
            if (it.completed) {
                uiCommunicationListener.onAuthActivity()
            }

            it.error?.let { error ->
                //TODO
                if (error.localizedMessage.toString().contains(AUTH_ERROR)) {
                    Log.d(TAG, "setupObservers: ERROR")

                }
            }

        })

        viewModel.update.observe(viewLifecycleOwner, {
            if (it != 0) {
                val text = getString(R.string.attached) + it.toString()
                binding.contentTechSupport.sizeOfFileUpload.text = text
                binding.contentTechSupport.sizeOfFileUpload.visibility = View.VISIBLE
            } else {
                binding.contentTechSupport.sizeOfFileUpload.visibility = View.GONE
            }
        }
        )
    }


    private fun setupViews() {
//        val cardUserInfo  = binding.cardPersonalInfo
//        val expandableUserInfo = binding.expandableUserInfo
        content = binding.contentPersonal
        content?.changePassword?.setOnClickListener(this)
        content?.changeEmail?.setOnClickListener(this)
        content?.changeNumber?.setOnClickListener(this)
        aboutUs = binding.contentAboutUs
        aboutUs?.policy?.setOnClickListener(this)
        aboutUs?.publicOffert?.setOnClickListener(this)
        aboutUs?.rightsOwner?.setOnClickListener(this)
        feedBack = binding.contentTechSupport
        feedBack?.clipBtn?.setOnClickListener(this)
        feedBack?.sendMessage?.setOnClickListener(this)
        radioGroup = feedBack?.btnGroup!!

//        feedBack?.sendMessageField.addTextChangedListener(this)
        setgradient(binding.logoutTxt)
        setgradient(aboutUs?.publicOffert!!)
        setgradient(aboutUs?.policy!!)
        setgradient(aboutUs?.rightsOwner!!)
        setgradient(content?.changePassword!!)
        setgradient(content?.changeEmail!!)
        setgradient(content?.changeNumber!!)
        setgradient(binding.personalDataText)
        setgradient(binding.languageText)
        setgradient(binding.devicesText)
        setgradient(binding.aboutUsText)
        setgradient(binding.technicalSupportText)
        binding.profilePhotoBtn.setOnClickListener(this)
        binding.logoutTxt.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        Log.d(TAG, "onClick: " + v.toString())
        when (v) {
            content?.changePassword -> {
                if (viewModel.state.value?.userAccount?.email.isNullOrEmpty()) {
                    //TODO
                    content?.email?.hint = getString(R.string.input_email)
                    content?.email?.setHintTextColor(Color.RED)
                } else {
                    viewModel.preparePassword()
                    findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
                }
            }

            content?.changeEmail -> {

                if (content?.email?.text.isNullOrBlank()) {
                    content?.email?.hint = getString(R.string.input_email)
                    content?.email?.setHintTextColor(Color.RED)
                } else {
                    findNavController().navigate(R.id.action_accountFragment_to_changeEmailFragment)
                }
            }

            content?.changeNumber -> {
                Log.d(TAG, "onClick: ChangeNumber" + content?.changeNumber?.text.toString())
                if (content?.telephonNumber?.text.toString().isNotBlank())
                    viewModel.changeNumber(content?.telephonNumber?.text.toString())
                else {
                    content?.telephonNumber?.hint = getString(R.string.input_phone)
                    content?.telephonNumber?.setHintTextColor(Color.RED)
                }
            }
            binding.profilePhotoBtn -> {
                cropActivityResultLauncher.launch(null)
            }

            binding.logoutTxt -> {
                Log.d(TAG, "onClick: Logout")
                logoutDialog(
                    context,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun yes() {
                            viewModel.logout()
                        }

                        override fun uploadPhoto(selectedImagePath: String) {

                        }
                    }
                )
            }

            feedBack?.clipBtn -> {
                fileUploadActivityResultLauncher.launch(null)
            }

            feedBack?.sendMessage -> {
                if (!feedBack?.sendMessageField?.text.toString().isNullOrBlank()) {
                    val selectedRadioButton: Int = radioGroup.checkedRadioButtonId
                    val radioButton = feedBack?.root?.findViewById<RadioButton>(selectedRadioButton)
                    viewModel.setData(radioButton?.text.toString()+feedBack?.sendMessageField?.text.toString())
                    viewModel.uploadButtonClicked()
                } else {
                    feedBack?.sendMessageField?.hint = getString(R.string.input_here)
                    feedBack?.sendMessageField?.setHintTextColor(Color.RED)
                }
            }

            aboutUs?.policy -> {
                val fileUrl =
                    "http://docs.google.com/viewer?url=https://domusic.uz/static/docs/confidential_ru.pdf"
                val uri = Uri.parse(fileUrl)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "text/html")
                startActivity(intent)
            }
            aboutUs?.publicOffert -> {
                val fileUrl =
                    "http://docs.google.com/viewer?url=https://domusic.uz/static/docs/offer_ru.pdf"
                val uri = Uri.parse(fileUrl)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "text/html")
                startActivity(intent)
            }
            aboutUs?.rightsOwner -> {
                val fileUrl =
                    "http://docs.google.com/viewer?url=https://domusic.uz/static/docs/copyright_ru.pdf"
                val uri = Uri.parse(fileUrl)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "text/html")
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        content = null
        aboutUs = null
        feedBack = null
    }


}