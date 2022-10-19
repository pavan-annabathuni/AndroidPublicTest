package com.example.soiltesting.ui.tracker

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentCheckSoilTestBinding
import com.example.soiltesting.databinding.FragmentStatusTrackerBinding
import com.example.soiltesting.model.history.Data
import com.example.soiltesting.model.tracker.DataX
import com.example.soiltesting.ui.history.HistoryViewModel
import com.example.soiltesting.utils.NetworkResult
import java.util.ArrayList


class StatusTrackerFragment : Fragment(), FeedbackListerner {
    private var _binding: FragmentStatusTrackerBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[StatusTrackerViewModel::class.java] }
    private var statusTrackerAdapter = StatusTrackerAdapter(this)
    var id: Int? = null


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatusTrackerBinding.inflate(inflater, container, false)
        val bundle = Bundle()
        if (arguments != null)
            id = arguments?.getInt("id")
        val soil_test_number: String? = arguments?.getString("soil_test_number")

        viewModel.getStatusTracker(id!!)
        Log.d(TAG, "onCreateViewID: $id")
        binding.tvID.text = "Id:" + soil_test_number.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerviewStatusTracker.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewStatusTracker.adapter = statusTrackerAdapter
        initViewBackClick()
        bindObservers()
    }

    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun bindObservers() {
        viewModel.statusTrackerLiveData.observe(viewLifecycleOwner, Observer { model ->
            when (model) {
                is NetworkResult.Success -> {
                    Log.d("TAG", "bindObserversDataStatusTracker:" + model.data.toString())
                    val response =
                        model.data?.data as ArrayList<DataX>
                    statusTrackerAdapter.setMovieList(response)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext(), model.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {

                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun feedbackApiListener(dataX: DataX) {
        binding.btnViewReport. visibility=View.VISIBLE
//        findNavController().navigate(R.id.action_statusTrackerFragment_to_feedbackFragment)
    }


}