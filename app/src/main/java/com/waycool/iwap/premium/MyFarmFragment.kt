package com.waycool.iwap.premium

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.utils.Resource
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.R
import com.waycool.iwap.databinding.FragmentDeviceOneBinding
import com.waycool.iwap.databinding.FragmentDeviceTwoBinding
import com.waycool.iwap.databinding.FragmentMyFarmBinding


class MyFarmFragment : Fragment(),Farmdetailslistener {
    private var _binding: FragmentMyFarmBinding? = null
    private val binding get() = _binding!!

    private val viewModel:MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }
    private val adapter:MyFarmFragmentAdapter by lazy { MyFarmFragmentAdapter(this,requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyFarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerview.adapter=adapter

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
    }

    override fun farmDetails(farm: MyFarmsDomain) {
        val bundle=Bundle()
        bundle.putParcelable("farm",farm)
        findNavController().navigate(R.id.action_myFarmFragment_to_nav_farmdetails,bundle)
    }


}