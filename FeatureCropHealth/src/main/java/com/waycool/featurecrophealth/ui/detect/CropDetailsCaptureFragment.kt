package com.waycool.featurecrophealth.ui.detect


import android.content.Intent

import android.net.Uri
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropDetailsCaptureBinding
import com.waycool.featurecrophealth.utils.Constant.TAG
import com.waycool.squarecamera.SquareCamera
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class CropDetailsCaptureFragment : Fragment() {

    private var _binding: FragmentCropDetailsCaptureBinding? = null
    private val binding get() = _binding!!
    var crop_id: Int? = null
    var crop_name: String? = null
    var crop_logo: String? = null
    private val FILE_NAME = "photo.jpg"
    private val REQUEST_CODE = 42
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
            crop_logo = arguments?.getString("crop_logo").toString()
        }

        // bundle.getInt("project_id").toString()
        Log.wtf("received:", "$crop_logo")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickes()
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
    }
    fun translationSoilTesting() {
        TranslationsManager().loadString("affected_region", binding.tvCropEffect)
        TranslationsManager().loadString("leaf", binding.rb1)
        TranslationsManager().loadString("add_image", binding.addPhotoTxt)
        TranslationsManager().loadString("how_to_capture", binding.howTo)
        TranslationsManager().loadString("capture_image", binding.camptureImage)
        TranslationsManager().loadString("upload_image", binding.camptureImageCamera)
        TranslationsManager().loadString("detect", binding.tvCheckCrop)
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //     super.onActivityResult(requestCode, resultCode, data)
        binding.closeImage?.setOnClickListener {
            binding.previewImage.visibility = View.GONE
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            val selectedImage: Uri? = data?.data
            val pic = File(requireActivity().externalCacheDir, "pest.jpg")
            selecteduri = selectedImage
            val options: UCrop.Options = UCrop.Options()
            options.setCompressionQuality(100)
            options.setMaxBitmapSize(10000)

            if (selectedImage != null)
                UCrop.of(selectedImage, Uri.fromFile(pic))
                    .withAspectRatio(1F, 1F)
                    .withMaxResultSize(1000, 1000)
                    .withOptions(options)
                    .start(requireActivity())
            binding.previewImage.visibility = View.VISIBLE
            binding.closeImage?.visibility = View.VISIBLE
            binding.uploadedImg.setImageURI(selecteduri)
            binding.cardCheckHealth.setOnClickListener {
                Log.d(TAG, "onViewCreatedStringPrint: $crop_name")
                Log.d(TAG, "onViewCreatedStringPrint: $crop_id")
                binding.closeImage?.visibility = View.GONE
//            val userRequest = AiCropPostResponse()
                binding.uploadedImg.isEnabled = true
                val file:File=File(selecteduri?.path)
//                val file=selecteduri?.toFile()

                val requestFile: RequestBody =
                    RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
                val profileImage: RequestBody = RequestBody.create(
                    "image/jpg".toMediaTypeOrNull(),
                    file
                )

                val profileImageBody: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "image_url",
                        file.name, profileImage
                    )
                binding.progressBar?.visibility = View.VISIBLE
                binding.cardCheckHealth.visibility = View.GONE


//                val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.getName(), requestFile)
//                Log.d(TAG, "onViewCreatedStringPrintBody: ${body}")

                postImage(
                    crop_id!!,
                    crop_name!!,
                    profileImageBody
                )
                Log.d(TAG, "onViewCreatedStringPrint: $file")
                Log.d(TAG, "onViewCreatedStringPrint: $profileImage")
                Log.d(TAG, "onViewCreatedStringPrint: $file")
            }
        }
        else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == SquareCamera.REQUEST_CODE) {
            val uri: Uri? = data?.data
            selecteduri = uri!!
            Log.d("crophealth", "onActivityResultDataResultURi:$selecteduri ")
//            Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()

            Log.d(TAG, "onActivityResultUri: $uri")
            binding.closeImage?.visibility = View.VISIBLE
            binding.previewImage.visibility = View.VISIBLE
            binding.uploadedImg.setImageURI(uri)
            binding.cardCheckHealth.setOnClickListener {
                binding.closeImage?.visibility = View.GONE
                Log.d(TAG, "onViewCreatedStringPrint: $crop_name")
                Log.d(TAG, "onViewCreatedStringPrint: $crop_id")
//            val userRequest = AiCropPostResponse()
                val file = selecteduri?.toFile()
                binding.uploadedImg.isEnabled = true
                val requestFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file!!)

                val profileImage: RequestBody = RequestBody.create(
                    "image/jpg".toMediaTypeOrNull(),
                    file
                )

                val profileImageBody: MultipartBody.Part =
                    MultipartBody.Part.createFormData(
                        "image_url",
                        file.name, profileImage
                    )

                Log.d("aidetect", selecteduri.toString())
                Log.d("aidetect", file.name.toString())
                Log.d("aidetect", profileImage.toString())
                binding.progressBar?.visibility = View.VISIBLE
                binding.cardCheckHealth.visibility = View.GONE
//                val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.getName(), requestFile)
//                Log.d(TAG, "onViewCreatedStringPrintBody: ${body}")


                postImage(
                    crop_id!!,
                    crop_name!!,
                    profileImageBody
                )

//                viewModel.aiResponse.observe(requireActivity()) {
//                    Toast.makeText(requireContext(), "Check your crop type", Toast.LENGTH_SHORT)
//                        .show()
//
//
//                    if (it?.data?.message != null)
//                        Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
//                            .show()
//                }
//                if (crop_name.equals("The image has invalid image dimensions.")){
//                    Toast.makeText(requireContext(),"The image has invalid image dimensions.", Toast.LENGTH_SHORT).show()
//                }

            }

        }
        else if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val uri: Uri? = UCrop.getOutput(data!!)
            binding.uploadedImg.setImageURI(uri)
            selecteduri = uri!!

        }
    }

    private fun postImage(crop_id: Int?, crop_name: String?, profileImageBody: MultipartBody.Part) {
        viewModel.postAiImage(
            crop_id!!,
            crop_name!!,
            profileImageBody
        ).observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    binding.progressBar?.visibility = View.GONE
                    val data = it.data
                    data?.diseaseId
                    if (data?.diseaseId == null) {
//                        Toast.makeText(requireContext(),data?.message.toString() , Toast.LENGTH_SHORT).show()
                        MaterialAlertDialogBuilder(requireContext()).setTitle(" Alert ")
                            .setMessage(it.data?.message)
//                            .setNeutralButton("Remder later") { dialog, which ->
//                                showSnackbar("")
//                            }
//                            .setNegativeButton("No") { dialog, which ->
//                                showSnackbar("")
//                            }
                            .setPositiveButton("Ok") { dialog, which ->
                                showSnackbar("")
                            }.show()
//                            .setNeutralButton("later"){dialog,which->
//                                showSneak
//
//                            }
//                        val bundle=Bundle()
//                        bundle.putString("message",data?.message.toString())
//                        CustomeDialogFragment.newInstance().show(
//                            requireActivity().supportFragmentManager,
//                            CustomeDialogFragment.TAG,bundle
//                        )

                        binding.progressBar?.visibility = View.GONE
                        binding.cardCheckHealth.visibility = View.VISIBLE
                    } else {
                        val bundle = Bundle()
                        data?.diseaseId?.let { it1 -> bundle.putInt("diseaseid", it1) }
                        findNavController().navigate(
                            R.id.action_cropDetailsCaptureFragment_to_pestDiseaseDetailsFragment2,
                            bundle
                        )
                    }


                }
                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Currently We are Facing Server Error",
                        Toast.LENGTH_SHORT
                    ).show()
                    ToastStateHandling.toastError(requireContext(), "Currently We are Facing Server Error", Toast.LENGTH_SHORT)
                    binding.progressBar?.visibility = View.GONE
                    binding.cardCheckHealth.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showSnackbar(msg: String) {


    }

    fun clickes() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

    }


}

