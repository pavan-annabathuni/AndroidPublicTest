package com.waycool.iwap.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soiltesting.R
import com.example.soiltesting.ui.history.HistoryDataAdapter
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.databinding.FragmentAllServicesBinding
import com.waycool.iwap.databinding.FragmentHomePagesBinding


class AllServicesFragment : Fragment() {
    private var _binding: FragmentAllServicesBinding? = null
    private val binding get() = _binding!!
    lateinit var allServiceAdapter:SoilTestingLabsAdapter
//    private var allServiceAdapter=SoilTestingLabsAdapter(requireContext())
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allServiceAdapter=SoilTestingLabsAdapter(requireContext())
//        binding.recyclerviewService.layoutManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewService.adapter = allServiceAdapter
        binding.recyclerviewServicePremium.adapter = allServiceAdapter
        freeUser()
        premiumUser()
        clickes()
    }
    private fun freeUser() {
        viewModel.getModuleMaster().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val response = it.data as ArrayList<ModuleMasterDomain>
                    allServiceAdapter.setMovieList(response)
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            }

        }
    }
    private fun premiumUser() {
        viewModel.getModuleMaster().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val response = it.data as ArrayList<ModuleMasterDomain>
                    allServiceAdapter.setMovieList(response)
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            }

        }
    }
    fun clickes() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

    }

}