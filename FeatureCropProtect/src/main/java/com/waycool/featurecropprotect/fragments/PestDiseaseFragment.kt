package com.waycool.cropprotect.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecropprotect.Adapter.AdsAdapter
import com.waycool.featurecropprotect.Adapter.DiseasesParentAdapter
import com.waycool.featurecropprotect.CropProtectViewModel
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.FragmentPestDiseaseBinding
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding


class PestDiseaseFragment : Fragment() {
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private var selectedCategory: CropCategoryMasterDomain? = null
    private lateinit var binding: FragmentPestDiseaseBinding
    private val viewModel: CropProtectViewModel by lazy {
        ViewModelProvider(requireActivity())[CropProtectViewModel::class.java]
    }
    private var cropId: Int? = null
    private var cropName: String? = null

    //banners
    private val adapter: DiseasesParentAdapter by lazy { DiseasesParentAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPestDiseaseBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiErrorHandlingBinding=binding.errorState
        arguments?.let {
            cropId = it.getInt("cropid")
            cropName = it.getString("cropname")
        }

        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbarTitle.text = cropName

        binding.diseasesRv.adapter = adapter



        pestDiseaseApiCall()
        setBanners()
        fabButton()
    }


    private fun pestDiseaseApiCall() {
        cropId?.let { cropId ->
            viewModel.getPestDiseaseListForCrop(cropId).observe(requireActivity()) {
                when (it) {
                    is Resource.Success -> {
                        if (it.data == null)
                            adapter.submitList(emptyList())
                        else
                            adapter.submitList(it.data)
                    }
                    is Resource.Loading -> {
                        ToastStateHandling.toastWarning(requireContext(), "Loading.", Toast.LENGTH_SHORT)

                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)

                    }
                }
            }
        }
        adapter.onItemClick = {
            val args = Bundle()
            it?.cropId?.let { it1 -> args.putInt("cropId", it1) }
            it?.diseaseId?.let { it1 -> args.putInt("diseaseid", it1) }
            it?.diseaseName?.let { it1 -> args.putString("diseasename", it1) }
            it?.audioUrl?.let { it1 -> args.putString("audioUrl", it1) }
            findNavController().navigate(
                R.id.action_pestDiseaseFragment_to_pestDiseaseDetailsFragment,
                args
            )
        }
    }

    private fun networkCall() {
        if(NetworkUtil.getConnectivityStatusString(context)==0){
            binding.diseasesRv.visibility=View.GONE
            binding.clInclude.visibility=View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility=View.VISIBLE
            binding.addFab.visibility=View.GONE
            context?.let { ToastStateHandling.toastError(it,"Please check your internet connectivity",Toast.LENGTH_SHORT) }


        }else{
            binding.clInclude.visibility=View.GONE
            apiErrorHandlingBinding.clInternetError.visibility=View.GONE
            binding.addFab.visibility=View.VISIBLE
            binding.diseasesRv.visibility=View.VISIBLE
            pestDiseaseApiCall()
            setBanners()
        }

    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter()
        viewModel.getVansAdsList().observe(viewLifecycleOwner) {

            bannerAdapter.submitData(lifecycle, it)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.snapshot().size}"
            }.attach()
        }
        binding.bannerViewpager.adapter = bannerAdapter
//        TabLayoutMediator(
//            binding.bannerIndicators, binding.bannerViewpager
//        ) { tab: TabLayout.Tab, position: Int ->
//            tab.text = "${position + 1} / ${bannerImageList.size}"
//        }.attach()

        binding.bannerViewpager.clipToPadding = false
        binding.bannerViewpager.clipChildren = false
        binding.bannerViewpager.offscreenPageLimit = 3
        binding.bannerViewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)
    }

    private fun fabButton(){
        var isVisible = false
        binding.addFab.setOnClickListener(){
            if(!isVisible){
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            }else{
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener(){
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener(){
            FeatureChat.zenDeskInit(requireContext())
        }
    }
}