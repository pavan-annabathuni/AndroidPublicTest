package com.example.soiltesting.ui.tracker

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentAllHistoryBinding
import com.example.soiltesting.databinding.FragmentFeedbackBinding
import com.example.soiltesting.model.feedback.FeedbackRequest
import com.example.soiltesting.ui.checksoil.CheckSoilRTestViewModel
import com.example.soiltesting.ui.history.HistoryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FeedbackFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[CheckSoilRTestViewModel::class.java] }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clYes.setOnClickListener {
            val feedbackRequest = FeedbackRequest(
                1,
                "Status Tracker",
                "soil testing",
                binding.tvYes.text.toString(),
                ""
            )
            viewModel.postFeedback(feedbackRequest)
            Log.d(TAG, "onViewCreatedPostAPIYes: $feedbackRequest")
            dismiss()
        }
        binding.clNo.setOnClickListener {
            val feedbackRequest = FeedbackRequest(
                1,
                "Status Tracker",
                "soil testing",
                binding.tvNo.text.toString(),
                ""
            )
            viewModel.postFeedback(feedbackRequest)
            Log.d(TAG, "onViewCreatedPostAPINO: $feedbackRequest")
            dismiss()
        }
    }


}