package com.waycool.featurecrophealth.ui.detect


import android.app.Activity
import android.content.Intent

import android.net.Uri
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.Resource
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropDetailsCaptureBinding
import com.waycool.squarecamera.SquareCamera
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class CropDetailsCaptureFragment : Fragment() {

    private var _binding: FragmentCropDetailsCaptureBinding? = null
    private val binding get() = _binding!!
    var crop_id: Int? = null
    var crop_name: String? = null
    var crop_Tag_Name: String? = null
    var crop_logo: String? = null
    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    private lateinit var photoFile: File
    private var selecteduri: Uri? = null
    private val viewModel by lazy { ViewModelProvider(this)[CropHealthViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentCropDetailsCaptureBinding.inflate(inflater, container, false)
        val bundle = Bundle()
        if (arguments != null) {
            crop_id = arguments?.getInt("crop_id")
            crop_name = arguments?.getString("name").toString()
            crop_Tag_Name = arguments?.getString("TagCrop").toString()
            crop_logo = arguments?.getString("crop_logo").toString()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    val isSuccess = activity?.let { findNavController().popBackStack() }
//                    if (!isSuccess) activity?.let { NavUtils.navigateUpFromSameTask(it) }
                }
            }
        activity?.let {
            activity?.onBackPressedDispatcher?.addCallback(
                it,
                callback
            )
        }

        // bundle.getInt("project_id").toString()
        Log.wtf("received:", "$crop_logo")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClicks()
        binding.tvRequest.text = crop_name
        Glide.with(requireContext()).load(crop_logo!!).into(binding.cropImg)
//        binding.cropImg.loadUrl(crop_logo!!)
        binding.howTo.setOnClickListener {
            findNavController().navigate(R.id.action_cropDetailsCaptureFragment_to_howToClickFragment)
        }

        binding.galleryButton.setOnClickListener {
            SquareCamera.Builder().with(requireActivity()).launch()
        }
        binding.clCameraButton.setOnClickListener {
            selectImageInAlbum()
        }
        translationSoilTesting()
        binding.camptureImage.isSelected = true
        binding.camptureImageCamera.isSelected = true
        viewModel.downloadPestAndDiseases()
    }

    fun translationSoilTesting() {
        TranslationsManager().loadString("affected_region", binding.tvCropEffect,"Please click the affected leaf region \n" +
                "of your crop")
        TranslationsManager().loadString("leaf", binding.rb1,"Leaf")
        TranslationsManager().loadString("add_image", binding.addPhotoTxt,"Add Image")
        TranslationsManager().loadString("or", binding.orTxt,"Or")
        TranslationsManager().loadString("how_to_capture", binding.howTo,"How to capture image?")
        TranslationsManager().loadString("capture_image", binding.camptureImage,"Capture Image")
        TranslationsManager().loadString("upload_image", binding.camptureImageCamera,"Upload Image")
        TranslationsManager().loadString("detect", binding.tvCheckCrop,"Detect")
    }

    fun selectImageInAlbum() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        if (intent.resolveActivity(requireActivity().packageManager) != null) {
//            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
//        }
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //     super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            val selectedImage: Uri? = data?.data
            val pic = File(requireActivity().externalCacheDir, "pest.jpg")
            selecteduri = selectedImage
            val options: UCrop.Options = UCrop.Options()
            options.setCompressionQuality(80)
            options.setMaxBitmapSize(9000)

            if (selectedImage != null)
                UCrop.of(selectedImage, Uri.fromFile(pic))
                    .withAspectRatio(1F, 1F)
                    .withMaxResultSize(1000, 1000)
                    .withOptions(options)
                    .start(requireActivity())
            binding.previewImage.visibility = View.VISIBLE
            binding.closeImage?.visibility = View.VISIBLE
            binding.uploadedImg.setImageURI(selecteduri)
        }

//            if (selectedImage != null)
//                UCrop.of(selectedImage, Uri.fromFile(pic))
//                    .withAspectRatio(1F, 1F)
//                    .withMaxResultSize(1000, 1000)
//                    .withOptions(options)
//                    .start(requireActivity())
//            binding.previewImage.visibility = View.VISIBLE
//            binding.closeImage?.visibility = View.VISIBLE
//            binding.uploadedImg.setImageURI(selecteduri)

        else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == SquareCamera.REQUEST_CODE) {
            val uri: Uri? = data?.data
            selecteduri = uri!!
            binding.previewImage.visibility = View.VISIBLE
            binding.closeImage?.visibility = View.VISIBLE
            binding.uploadedImg.setImageURI(uri)

        } else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val uri: Uri? = UCrop.getOutput(data!!)
            selecteduri = uri!!
            binding.previewImage.visibility = View.VISIBLE
            binding.closeImage?.visibility = View.VISIBLE
            binding.uploadedImg.setImageURI(uri)

        }
        else {
            // User cancelled the image picker, reset state or clear UI elements
            // For example:
            selecteduri = null
            binding.previewImage.visibility = View.GONE
            binding.closeImage?.visibility = View.GONE
            binding.uploadedImg.setImageDrawable(null)
        }
    }

    private fun postImage(crop_id: Int?, ta: String?, profileImageBody: MultipartBody.Part) {
        viewModel.postAiImage(
            crop_id!!,
            crop_Tag_Name!!,
            profileImageBody
        ).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar?.visibility = View.GONE
                    val data = it.data
                    data?.diseaseId


                    if (data?.diseaseId == null) {
                        MaterialAlertDialogBuilder(requireContext()).setTitle("Incorrect Image")
                            .setMessage(it.data?.message)
                            .setPositiveButton("Ok") { dialog, which ->
                            }.show()
                        binding.progressBar?.visibility = View.GONE
                        binding.cardCheckHealth.visibility = View.VISIBLE
                        binding.closeImage?.visibility=View.VISIBLE
                    } else {
                        selecteduri=null
                        val bundle = Bundle()
                        data.diseaseId?.let { it1 -> bundle.putInt("diseaseid", it1) }
                        findNavController().navigate(
                            R.id.action_cropDetailsCaptureFragment_to_navigation_pest_and_disease_details,
                            bundle
                        )
                    }


                }
                is Resource.Error -> {
                    AppUtils.translatedToastServerErrorOccurred(context)

                    binding.progressBar?.visibility = View.GONE
                    binding.cardCheckHealth.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun onClicks() {
        binding.backBtn.setOnClickListener {
            val isSuccess = activity?.let { findNavController().popBackStack() }
//            if (!isSuccess) requireActivity().onBackPressed()
        }

        binding.closeImage?.setOnClickListener {
            binding.previewImage.visibility = View.GONE
            selecteduri = null
        }

        binding.cardCheckHealth.setOnClickListener {

            if (selecteduri == null) {
              viewModel.viewModelScope.launch {
                    val toastSelectImage = TranslationsManager().getString("select_image")
                    if(!toastSelectImage.isNullOrEmpty()){
                        context?.let { it1 -> ToastStateHandling.toastError(it1,toastSelectImage,
                            Toast.LENGTH_SHORT
                        ) }}
                    else {context?.let { it1 -> ToastStateHandling.toastError(it1,"Select Image",
                        Toast.LENGTH_SHORT
                    ) }}}
            } else {
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                val image = InputImage.fromFilePath(requireContext(), selecteduri!!)
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        labels.sortByDescending { it.confidence }


                        if (!labels.any { it.text.lowercase() == "plant" || it.text.lowercase() == "petal" || it.text.lowercase() == "fruit" || it.text.lowercase() == "flower" || it.text.lowercase() == "vegetable" || it.text.lowercase() == "insect" } && !labels.isNullOrEmpty()) {
                            MaterialAlertDialogBuilder(requireContext()).setTitle("Incorrect Image")
                                .setMessage("Image does not look correct. Upload correct image.")
                                .setPositiveButton("ok") { dialog, which ->
                                }.show()
                        } else {
                            uploadImage()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("Plant Health", "Error")
                        uploadImage()
                    }
            }
        }


    }
    private fun uploadImage() {
        binding.closeImage?.visibility = View.GONE
        val inputStream = context?.contentResolver?.openInputStream(selecteduri!!)
        val tempFile = createTempFile("temp", null, context?.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream?.copyTo(outputStream)
        }
        val requestFile: RequestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())

        val profileImage: RequestBody = tempFile
            .asRequestBody("image/jpg".toMediaTypeOrNull())

        val profileImageBody: MultipartBody.Part =
            MultipartBody.Part.createFormData(
                "image_url",
                tempFile.name, profileImage
            )

        binding.progressBar?.visibility = View.VISIBLE
        binding.cardCheckHealth.visibility = View.GONE

        postImage(
            crop_id!!,
            crop_Tag_Name!!,
            profileImageBody
        )
    }


    //    private fun uploadImage() {
//        binding.closeImage?.visibility = View.GONE
//        val file = selecteduri?.toFile()
//        binding.uploadedImg.isEnabled = true
//        val requestFile: RequestBody = file!!.asRequestBody("image/*".toMediaTypeOrNull())
//
//        val profileImage: RequestBody = file
//            .asRequestBody("image/jpg".toMediaTypeOrNull())
//
//        val profileImageBody: MultipartBody.Part =
//            MultipartBody.Part.createFormData(
//                "image_url",
//                file.name, profileImage
//            )
//
//        binding.progressBar?.visibility = View.VISIBLE
//        binding.cardCheckHealth.visibility = View.GONE
//
//        postImage(
//            crop_id!!,
//            crop_Tag_Name!!,
//            profileImageBody
//        )
//
//    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropDetailsCaptureFragment")
    }

}

