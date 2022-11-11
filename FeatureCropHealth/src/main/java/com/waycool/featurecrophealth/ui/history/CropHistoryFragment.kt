package com.waycool.featurecrophealth.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.ZendeskChat
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropHistoryBinding

import com.waycool.featurecrophealth.utils.Constant
import com.waycool.featurecrophealth.utils.NetworkResult


class CropHistoryFragment : Fragment() {
    private var _binding: FragmentCropHistoryBinding? = null
    private val binding get() = _binding!!
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
            ZendeskChat.zenDesk(requireContext())
        }
    }

}