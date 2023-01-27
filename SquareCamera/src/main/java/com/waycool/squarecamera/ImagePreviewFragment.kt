package com.waycool.squarecamera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.waycool.squarecamera.databinding.FragmentImagePreviewBinding

class ImagePreviewFragment : Fragment() {
    private var firebaseAnalytics: FirebaseAnalytics?=null


    private lateinit var uri: Uri
    lateinit var binding: FragmentImagePreviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            uri = arguments?.getParcelable("uri")!!
        }
        firebaseAnalytics = Firebase.analytics

        binding.imageView.invalidate()
        binding.imageView.setImageURI(uri)

        binding.backImageview.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }

        binding.imageSaveButton.setOnClickListener {
            val intent = Intent()
            intent.data = uri
            Log.d("crophealth","$uri")
//            Toast.makeText(requireContext(),"Save called",Toast.LENGTH_SHORT).show()
            requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "ImagePreviewFragment")
        }
    }

}