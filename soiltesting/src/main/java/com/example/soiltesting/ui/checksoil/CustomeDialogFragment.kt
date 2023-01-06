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
    private lateinit var binding: FragmentCustomeDialogBinding

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
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.bg_custome_dialog)
        binding = FragmentCustomeDialogBinding.inflate(inflater, container, false)
        binding.tvOk .setOnClickListener {
            dialog!!.cancel()
            dialog!!.dismiss()
        }
        return binding.root
    }






}