package com.waycool.iwap.myfarms

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.waycool.addfarm.AddFarmActivity
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentMyFarmBinding
import com.waycool.iwap.premium.Farmdetailslistener
import com.waycool.iwap.premium.ViewDeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyFarmFragment : Fragment(), Farmdetailslistener {
    private var _binding: FragmentMyFarmBinding? = null
    private val binding get() = _binding!!

    private val viewModel:MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val deviceViewModel by lazy { ViewModelProvider(this)[ViewDeviceViewModel::class.java] }
    private val adapter: MyFarmFragmentAdapter by lazy { MyFarmFragmentAdapter(this,requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@MyFarmFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        _binding = FragmentMyFarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.adapter=adapter

        binding.addFarmFab.setOnClickListener {
            val intent=Intent(requireActivity(),AddFarmActivity::class.java)
            startActivity(intent)
        }

        viewModel.getMyFarms().observe(viewLifecycleOwner){
            when(it){
                is Resource.Success ->{
                    if(!it.data.isNullOrEmpty()){
                        adapter.setMovieList(it.data)
                    }
                }
                is Resource.Loading ->{}
                is Resource.Error ->{}
            }
        }

        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            val response = it.data as ArrayList<MyCropDataDomain>
            adapter.updateCropsList(response)
        }

        deviceViewModel.getIotDevice().observe(viewLifecycleOwner){
            if(!it.data.isNullOrEmpty())
            adapter.updateDeviceList(it.data!!)
        }

        translationSoilTesting()
    }

    fun translationSoilTesting() {
//        CoroutineScope(Dispatchers.Main).launch {
//            val title = TranslationsManager().getString("my_farm")
//            binding.toolBar.text = title
//
//        }
        TranslationsManager().loadString("add_farm_top", binding.addFarmFab,"Add Farm")
        TranslationsManager().loadString("add_farm_top", binding.toolBar,"Add Farm")
    }

    override fun onFarmDetailsClicked(farm: MyFarmsDomain) {
        val bundle=Bundle()
        bundle.putParcelable("farm",farm)
        findNavController().navigate(R.id.action_myFarmFragment_to_nav_farmdetails,bundle)
    }


}