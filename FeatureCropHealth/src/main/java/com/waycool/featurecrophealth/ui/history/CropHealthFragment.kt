package com.waycool.featurecrophealth.ui.history

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
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropHealthBinding
import com.waycool.featurecrophealth.model.historydata.Data
import com.waycool.featurecrophealth.utils.Constant.TAG
import com.waycool.featurecrophealth.utils.NetworkResult
import com.waycool.featurecrophealth.viewmodel.HistoryViewModel


class CropHealthFragment : Fragment() {
    private var _binding: FragmentCropHealthBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: NoteAdapter
    private val data = ArrayList<Data>()
    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCropHealthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllHistory(13,requireContext())
//        initView()
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        historyAdapter = NoteAdapter(requireContext())
        binding.recyclerview.adapter = historyAdapter
        binding.cardCheckHealth.setOnClickListener {
            findNavController().navigate(R.id.action_cropHealthFragment_to_cropSelectFragment)
        }
        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_cropHealthFragment_to_cropHistoryFragment)
        }


        bindObservers()


    }
//    private fun initView() {
//        binding.recyclerview.layoutManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
//
//    }

    private fun bindObservers() {

        viewModel.historyLiveData.observe(viewLifecycleOwner, Observer { model ->
            if (model.data?.data?.isEmpty() == true) {
                binding.takeGuide.visibility = View.VISIBLE
            } else
                when (model) {
                    is NetworkResult.Success -> {
                        Log.d(TAG, "bindObserversData:" + model.data.toString())
                        val response = model.data?.data as ArrayList<Data>

                        if (response.size <=2) {

                            historyAdapter.submitList(response)

                        }
                        else{
                            val arrayList = ArrayList<Data>()
                            arrayList.add(response[0])
                            arrayList.add(response[1])

                            historyAdapter.submitList(arrayList)

                        }
//                        else if (response.size==2){
//                            historyAdapter = NoteAdapter(2)
//                            historyAdapter.submitList(response)
//
//                        }
//                        else{
//                            historyAdapter= NoteAdapter(2)
//                            historyAdapter.submitList(response)
//
//                        }
                    }is NetworkResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            model.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    is NetworkResult.Loading -> {

                    }
                }
        })

    }

    private fun onNoteClicked(noteResponse: Data) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}