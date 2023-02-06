package com.example.soiltesting.ui.tracker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentFeedbackBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FeedbackFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

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
//        binding.clYes.setOnClickListener {
//            val feedbackRequest = FeedbackRequest(
//                1,
//                "Status Tracker",
//                "soil testing",
//                binding.tvYes.text.toString(),
//                ""
//            )
//            viewModel.postFeedback(feedbackRequest)
//            Log.d(TAG, "onViewCreatedPostAPIYes: $feedbackRequest")
//            dismiss()
//        }
//        binding.clNo.setOnClickListener {
//            val feedbackRequest = FeedbackRequest(
//                1,
//                "Status Tracker",
//                "soil testing",
//                binding.tvNo.text.toString(),
//                ""
//            )
//            viewModel.postFeedback(feedbackRequest)
//            Log.d(TAG, "onViewCreatedPostAPINO: $feedbackRequest")
//            dismiss()
//        }
    }


}