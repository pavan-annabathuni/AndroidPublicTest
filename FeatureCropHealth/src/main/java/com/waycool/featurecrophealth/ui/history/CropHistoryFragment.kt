package com.waycool.featurecrophealth.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soiltesting.ui.history.HistoryDataAdapter
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropHistoryBinding
import com.waycool.featurecrophealth.utils.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class CropHistoryFragment : Fragment() {
    private var _binding: FragmentCropHistoryBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var searchCharSequence: CharSequence? = ""
    private var handler: Handler? = null

    //    private lateinit var historyAdapter: AiCropHistoryAdapter
    private lateinit var historyAdapter: AiCropHistoryAdapter
    private val viewModel by lazy { ViewModelProvider(this)[CropHealthViewModel::class.java] }
    var filteredList = ArrayList<AiCropHistoryDomain>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCropHistoryBinding.inflate(inflater, container, false)
        historyAdapter = AiCropHistoryAdapter(requireContext())
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        clickSearch()
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.adapter = historyAdapter

        speechToText()
        bindObservers()
        onclick()
        fabButton()
        translationSoilTesting()


        historyAdapter.onItemClick = {
            if (it?.disease_id == null) {
                ToastStateHandling.toastError(requireContext(), "Please upload quality image", Toast.LENGTH_SHORT)
            } else {
                val bundle = Bundle()
                it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
                findNavController().navigate(
                    R.id.action_cropHistoryFragment_to_navigation_pest_and_disease_details,
                    bundle
                )
            }


        }

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                bindObservers  (
                    searchQuery = searchCharSequence.toString()
                )
            }
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
//                    activity?.finish()

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
    }

    private fun speechToText() {
        binding.textToSpeach.setOnClickListener {
            binding.searchView.text.clear()
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

            // on below line we are passing language model
            // and model free form in our intent
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )

            // on below line we are passing our
            // language as a default language.
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE, "en-IN"
            )

            // on below line we are specifying a prompt
            // message as speak to text on below line.
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            // on below line we are specifying a try catch block.
            // in this block we are calling a start activity
            // for result method and passing our result code.
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
//                Toast
//                    .makeText(
//                        context, " " + e.message,
//                        Toast.LENGTH_SHORT
//                    )
//                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: java.util.ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as java.util.ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                binding.searchView.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }

    private fun onclick() {
        binding.cardCheckHealth.setOnClickListener {
            findNavController().navigate(R.id.action_cropHistoryFragment_to_cropSelectFragment)
        }
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun bindObservers( searchQuery: String? = "") {
        viewModel.getAiCropHistoryFromLocal(searchQuery).observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {
                    Log.d(Constant.TAG, "bindObserversData:" + it.data.toString())
                    val response = it.data
                    historyAdapter.submitList(response)
                    val responseSearh = it.data as ArrayList<AiCropHistoryDomain>
                    filteredList.addAll(responseSearh)
                    Log.d("TAG", "bindObserversObserveData:$filteredList")

//                    cropDetailsList = HistoryResponse(response,"",true)

                }
                is Resource.Error -> {
                    ToastStateHandling.toastError(
                        requireContext(),
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                }
                is Resource.Loading -> {

                }
                else -> {}
            }
        }
    }
    fun translationSoilTesting() {
        CoroutineScope(Dispatchers.Main).launch {
            val search = TranslationsManager().getString("peast_diease")
            binding.searchView.hint = search
        }
        TranslationsManager().loadString("request_history", binding.tvToolBarTest)
//        TranslationsManager().loadString("soil_sample_n_collection", binding.tvCheckCrop)
    }

//    private fun clickSearch() {
//        binding.searchView.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(
//                charSequence: CharSequence,
//                i: Int,
//                i1: Int,
//                i2: Int
//
//            ) {
//            }
//
//            override fun onTextChanged(
//                charSequence: CharSequence,
//                i: Int,
//                i1: Int,
//                i2: Int
//            ) {
//
//                val temp = ArrayList<AiCropHistoryDomain>()
////                filteredList.clear()
//                Log.d("TAG", "onTextChangedListShow: $temp")
//                if (charSequence.isNotEmpty()) {
//                    filteredList.forEach {
//                        if (it.cropdata.cropName.toString().lowercase()
//                                .contains(charSequence.toString().lowercase())
//                        ) {
//                            if (!temp.contains(it)) {
//                                temp.add(it)
//                            }
//                        }
//                    }
//                    historyAdapter.upDateList(temp)
////                    historyAdapter.submitList(temp)
//                    Log.d("TAG", "::::stderr  $temp")
//                }
////                if (temp.isEmpty()){
////                    soilHistoryAdapter.upDateList(filteredList)
////                }
//            }
////                filteredList.forEach {
////                 if (   it.soil_test_number?.lowercase()!!.startsWith(charSequence.toString().lowercase())){
////                     filteredList.add(filteredList)
////                 }
////                }
//
////                for (item in filteredList[].soil_test_number!!.indices) {
////                    Log.d("TAG", "::::stderr $charSequence")
////                    if (filteredList[0].soil_test_number!!.lowercase()
////                            .startsWith(charSequence.toString().lowercase())
////                    ) {
//////                        filteredList.add(filteredList)
////                        Log.d(TAG, "onTextChangedList:$filteredList")
////                        Log.d("TAG", "::::::::stderr $charSequence")
////                    }
////
////                }
//
////                binding.etSearchItem.getText().clear();
////            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        })
//    }

    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener {
            FeatureChat.zenDeskInit(requireContext())
        }
    }

}