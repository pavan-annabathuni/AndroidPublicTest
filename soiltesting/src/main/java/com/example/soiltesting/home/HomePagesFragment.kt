package com.example.soiltesting.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.soiltesting.databinding.FragmentHomePagesBinding

class HomePagesFragment : Fragment() {

    private var _binding: FragmentHomePagesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomePagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setSupportActionBar(toolbar)
//       binding.collapsingToolbarLayout.setTitle("Test Title")
//        binding.collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.BottomSheet)
//        binding.collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.BottomSheet)
//        binding.collapsingToolbarLayout.setContentScrimColor(Color.GREEN)
    }


}