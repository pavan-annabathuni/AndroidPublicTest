package com.example.profile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.profile.R
import com.example.profile.databinding.FragmentAboutOutgrowBinding
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AboutOutgrowFragment : Fragment() {
    private lateinit var binding:FragmentAboutOutgrowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutOutgrowBinding.inflate(inflater)
        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
        TranslationsManager().loadString("about_outgrow",binding.tvAboutOutgrowDescription,resources.getString(
            R.string.str_about_outGrow
        ))
        GlobalScope.launch {
          binding.topAppBar.title = TranslationsManager().getString("str_about")
        }
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AboutOutgrowFragment")
    }
}