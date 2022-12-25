package com.waycool.squarecamera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_TYPE_ONE_SHOT_FLASH
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.waycool.squarecamera.databinding.FragmentCaptureImageBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CaptureImageFragment : Fragment() {

    lateinit var binding: FragmentCaptureImageBinding
    private var flashMode: Int = ImageCapture.FLASH_MODE_OFF
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCaptureImageBinding.inflate(layoutInflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.infoIv.setOnClickListener {
            findNavController().navigate(R.id.action_captureImageFragment_to_dialogFragment)
        }

        binding.viewFinder.layoutParams.height =
            Resources.getSystem().displayMetrics.widthPixels


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.imageViewFrame.visibility = View.INVISIBLE;
        val animZoomOut = loadAnimation(requireActivity(), R.anim.zoom_out_with_fade)
        val animZoomIn = loadAnimation(requireActivity(), R.anim.zoom_in_with_fade)
        binding.infoCv.startAnimation(animZoomOut)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.myLooper()!!).postDelayed(Runnable {
                binding.infoCv.startAnimation(animZoomIn).also {
                    binding.infoCv.visibility = View.GONE
                    binding.imageViewFrame.visibility = View.VISIBLE;

                }

            }, 2500)
        }

        binding.galleryIv.setOnClickListener { getImageFromGallery() }

        binding.captureimageBack.setOnClickListener { requireActivity().finish() }

        binding.flashIv.setOnClickListener {
           when (flashMode) {
                ImageCapture.FLASH_MODE_OFF -> {
                    flashMode = ImageCapture.FLASH_MODE_ON
//                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
                    binding.flashIv.setImageResource(R.drawable.ic_baseline_flash_on_24)
                }
                ImageCapture.FLASH_MODE_ON -> {
                    flashMode = ImageCapture.FLASH_MODE_AUTO
//                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_AUTO
                    binding.flashIv.setImageResource(R.drawable.ic_baseline_flash_auto_24)
                }
                ImageCapture.FLASH_MODE_AUTO -> {
                    flashMode = ImageCapture.FLASH_MODE_OFF
//                    imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
                    binding.flashIv.setImageResource(R.drawable.ic_baseline_flash_off_24)
                }
            }
        }
    }


    private var mGetContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri.let {
            val selectedImage: Uri? = it
            val pic = File(requireActivity().externalCacheDir, "pest.jpg")

            val options: UCrop.Options = UCrop.Options()
            options.setCompressionQuality(100)
            options.setMaxBitmapSize(10000)

            if (selectedImage != null)
                UCrop.of(selectedImage, Uri.fromFile(pic))
                    .withAspectRatio(1F, 1F)
                    .withMaxResultSize(1000, 1000)
                    .withOptions(options)
                    .start(requireActivity())
        }

    }

    private fun getImageFromGallery() {
        mGetContent.launch("image/*")
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return


        val fileName = "JPEG_Image.jpg"
        val file = File(requireActivity().externalCacheDir, fileName)
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(file)
            .build()

        imageCapture.flashMode = flashMode
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    passUri(output.savedUri)
                    Log.d(TAG, msg)
                }
            }
        )
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetResolution(Size(1000, 1000))
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }


//            bindCameraUseCases()

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1000, 1000))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG)
                .setFlashMode(flashMode)
                .build()


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

//                if (camera.cameraInfo.hasFlashUnit())
//                    camera.cameraControl.enableTorch(true)

//                camera.cameraControl.enableTorch(true)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }


    private fun passUri(uri: Uri?) {
//        Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()
        var bundle = Bundle()
        bundle.putParcelable("uri", uri)

        Navigation.findNavController(binding.root)
            .navigate(R.id.action_captureImageFragment_to_imagePreviewFragment, bundle);

//        val intent = Intent(this, ImageSelectedActivity::class.java)
//        intent.data = uri
//        startActivity(intent)
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val uri: Uri? = UCrop.getOutput(data!!)

            Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()

            passUri(uri)

        }
    }
}