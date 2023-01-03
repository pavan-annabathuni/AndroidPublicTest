package com.waycool.iwap.allservices

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentAllServicesBinding


class AllServicesFragment : Fragment() {
    private var _binding: FragmentAllServicesBinding? = null
    private val binding get() = _binding!!
    lateinit var allServiceAdapter: SoilTestingLabsAdapter
    private lateinit var premiumServiceAdapter: PremiumServiceAdapter
    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@AllServicesFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        _binding = FragmentAllServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allServiceAdapter= SoilTestingLabsAdapter(requireContext())
//        binding.recyclerviewService.layoutManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewService.adapter = allServiceAdapter
        binding.recyclerviewServicePremium.adapter = allServiceAdapter
        freeUser()
        premiumUser()
        clickes()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               findNavController().navigateUp()
            }

        })
    }
    private fun freeUser() {
        viewModel.getModuleMaster().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val data = it.data?.filter {
                        it.subscription!=1
                    }
                    premiumServiceAdapter = PremiumServiceAdapter(PremiumServiceAdapter.OnClickListener {
                    })
                    binding.recyclerviewService.adapter = premiumServiceAdapter
                    premiumServiceAdapter.submitList(data)

                }

                is Resource.Loading -> {
                    ToastStateHandling.toastWarning(requireContext(), "Loading", Toast.LENGTH_SHORT)

                }
                is Resource.Error -> {
                    ToastStateHandling.toastError(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)

                }
                else -> {}
            }

        }
        viewModel.getUserDetails().observe(viewLifecycleOwner){
            binding.tvKrishiServices.text = it.data?.name+" Services"
        }
    }
    private fun premiumUser() {
        viewModel.getModuleMaster().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val data = it.data?.filter {
                        it.subscription == 1
                    }
                    Log.d("premium", "premiumUser: $data")
                    premiumServiceAdapter = PremiumServiceAdapter(PremiumServiceAdapter.OnClickListener {
                        val bundle =Bundle()
                        bundle.putString("title",it.title)
                        bundle.putString("desc",it.moduleDesc)
                        bundle.putString("audioUrl",it.audioUrl)
                        bundle.putString("icon",it.moduleIcon)
                        findNavController().navigate(R.id.action_allServicesFragment_to_serviceDescFragment,bundle)
                    })
                    binding.recyclerviewServicePremium.adapter = premiumServiceAdapter
                    premiumServiceAdapter.submitList(data)
//                    Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    ToastStateHandling.toastWarning(requireContext(), "Loading", Toast.LENGTH_SHORT)

                }
                is Resource.Error -> {
                    ToastStateHandling.toastError(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)

                }
                else -> {}
            }

        }
    }
    fun clickes() {
        binding.topAppBar.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

    }

}