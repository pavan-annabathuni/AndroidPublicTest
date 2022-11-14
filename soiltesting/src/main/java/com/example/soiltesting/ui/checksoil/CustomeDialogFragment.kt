package com.example.soiltesting.ui.checksoil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentCustomeDialogBinding
import com.example.soiltesting.databinding.FragmentSoilTestingHomeBinding


class CustomeDialogFragment : DialogFragment() {
    private var _binding: FragmentCustomeDialogBinding? = null
    private val binding get() = _binding!!
    companion object {
        const val TAG = "SimpleDialog"
        fun newInstance(): CustomeDialogFragment {
            val fragment = CustomeDialogFragment()
            return fragment
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.bg_custome_dialog);
        _binding = FragmentCustomeDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }
    private fun setupClickListeners() {
        binding.tvOk .setOnClickListener {
            dismiss()
        }
    }


}