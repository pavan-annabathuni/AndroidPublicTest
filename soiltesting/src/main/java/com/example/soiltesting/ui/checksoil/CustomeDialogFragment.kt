package com.example.soiltesting.ui.checksoil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentCustomeDialogBinding
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager


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
        translationSoilTesting()
        binding.tvOk .setOnClickListener {
            dialog!!.cancel()
            dialog!!.dismiss()
        }
        translationSoilTesting()
        return binding.root
    }
    fun translationSoilTesting() {
        TranslationsManager().loadString("info",binding.tvInfo,"Information")
        TranslationsManager().loadString("thank_you", binding.tvDescription,"Thank you for showing interest.\n" +
                "Currently, we are not available in your \n" +
                "location, we look forward to serve you \n" +
                "shortly.")
        TranslationsManager().loadString("ok",binding.tvOk,"OK")
    }




    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CustomeDialogFragment")
    }


}