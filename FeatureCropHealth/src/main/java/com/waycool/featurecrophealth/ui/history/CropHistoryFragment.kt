package com.waycool.featurecrophealth.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropHistoryBinding

import com.waycool.featurecrophealth.utils.Constant
import com.waycool.featurecrophealth.utils.NetworkResult
import java.util.*


class CropHistoryFragment : Fragment() {
    private var _binding: FragmentCropHistoryBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE_SPEECH_INPUT = 1
    private lateinit var historyAdapter: AiCropHistoryAdapter
    private val viewModel by lazy { ViewModelProvider(this)[CropHealthViewModel::class.java] }

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

        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.adapter = historyAdapter
        speechToText()
        bindObservers()
        onclick()
        fabButton()
//        clickSearch()

        historyAdapter.onItemClick={
            val bundle=Bundle()
            it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
//            it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
            findNavController().navigate(R.id.action_cropHistoryFragment_to_pestDiseaseDetailsFragment2,bundle)

        }
    }
    private fun speechToText() {
        binding.textToSpeach.setOnClickListener() {
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

    private fun bindObservers() {
        viewModel.getAiCropHistory().observe(viewLifecycleOwner, Observer {

            when (it) {
                is Resource.Success -> {
                    Log.d(Constant.TAG, "bindObserversData:" + it.data.toString())
                    val response = it.data
                    historyAdapter.submitList(response)
//                    cropDetailsList = HistoryResponse(response,"",true)

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Loading -> {

                }
            }
        })

    }


//    private fun onNoteClicked(noteResponse: Data) {
//
//    }
//    private fun clickSearch() {
//
//        binding.searchView.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(
//                charSequence: CharSequence,
//                i: Int,
//                i1: Int,
//                i2: Int
//
//            ) {
//
//            }
//
//            override fun onTextChanged(
//                charSequence: CharSequence,
//                i: Int,
//                i1: Int,
//                i2: Int
//            ) {
//                filteredList.clear()
////                Log.d("TAG", "::::str $charSequence")
//                for (item in cropDetailsList.data.indices) {
//                    Log.d("TAG", "::::stderr $charSequence")
//                    if (cropDetailsList.data[item].crop_name.lowercase().startsWith(charSequence.toString().lowercase())) {
//                        filteredList.add(cropDetailsList.data[item])
//                        Log.d(Constant.TAG, "onTextChangedList:$filteredList")
//                        Log.d("TAG", "::::::::stderr $charSequence")
//                    }
//
//                }
//                historyAdapter.upDateList(filteredList)
////                binding.etSearchItem.getText().clear();
//            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        })
//    }

    private fun fabButton(){
        var isVisible = false
        binding.addFab.setOnClickListener(){
            if(!isVisible){
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            }else{
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener(){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener(){
            FeatureChat.zenDeskInit(requireContext())
        }
    }

}