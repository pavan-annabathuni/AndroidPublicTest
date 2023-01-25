package com.waycool.iwap.allservices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentAllServicesBinding
import kotlinx.coroutines.launch


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
        _binding = FragmentAllServicesBinding.inflate(inflater, container, false)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) activity?.let { it.finish()}
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }

        binding.topAppBar.setNavigationOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) activity?.let { it1 -> it1.finish() }
        }
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
        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("str_all_services")
            if(!title.isNullOrEmpty())
            binding.topAppBar.title = title
            else binding.topAppBar.title = "All Services"
        }
        TranslationsManager().loadString("str_explore",binding.tvExplore,"Explore our services")
        TranslationsManager().loadString("str_premimum_explore",binding.tvKrishiServicesPremium,"Explore our premium services")
    }
    private fun freeUser() {
        viewModel.getModuleMaster().observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    val data = it.data?.filter {
                        it.subscription!=1
                    }
                    premiumServiceAdapter = PremiumServiceAdapter(PremiumServiceAdapter.OnClickListener
                    {

                        val eventBundle=Bundle()
                        eventBundle.putString("View_all_services",it.title)
                        EventItemClickHandling.calculateItemClickEvent("View_all_services",eventBundle)

                    },requireContext())
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
        viewModel.getModuleMaster().observe(viewLifecycleOwner) { it ->
            when (it) {
                is Resource.Success -> {
                    val data = it.data?.filter {
                        it.subscription == 1
                    }
                    premiumServiceAdapter = PremiumServiceAdapter(PremiumServiceAdapter.OnClickListener { it ->
                        val eventBundle=Bundle()
                        eventBundle.putString("View_all_services",it.title)
                        EventItemClickHandling.calculateItemClickEvent("View_all_services",eventBundle)

                        val bundle =Bundle()
                        bundle.putString("title",it.title)
                        bundle.putString("desc",it.moduleDesc)
                        bundle.putString("audioUrl",it.audioUrl)
                        bundle.putString("icon",it.moduleIcon)
                        this.findNavController().navigate(R.id.action_allServicesFragment_to_serviceDescFragment,bundle)
                    },requireContext())
                    binding.recyclerviewServicePremium.adapter = premiumServiceAdapter
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
    }
    fun clickes() {
        binding.topAppBar.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }

    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("AllServicesFragment")
    }


}