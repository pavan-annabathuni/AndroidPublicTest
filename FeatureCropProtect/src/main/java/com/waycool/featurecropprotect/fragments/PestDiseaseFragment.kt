package com.waycool.cropprotect.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecropprotect.Adapter.DiseasesParentAdapter
import com.waycool.featurecropprotect.CropProtectViewModel
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.FragmentPestDiseaseBinding
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.videos.adapter.AdsAdapter
import kotlin.math.abs


class PestDiseaseFragment : Fragment() {
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private lateinit var binding: FragmentPestDiseaseBinding
    private val viewModel: CropProtectViewModel by lazy {
        ViewModelProvider(requireActivity())[CropProtectViewModel::class.java]
    }
    private var handler: Handler? = null
    private var runnable: Runnable?=null
    private var cropId: Int? = null
    private var cropName: String? = null
    var selectedCategory:String?=null
    private var moduleId = "2"

    //banners
    private val adapter: DiseasesParentAdapter by lazy { DiseasesParentAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPestDiseaseBinding.inflate(inflater)
        binding.toolbar.setOnClickListener {
            val isSuccess = findNavController().popBackStack()
            if (!isSuccess) NavUtils.navigateUpFromSameTask(requireActivity())
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().popBackStack()
                    if (!isSuccess) NavUtils.navigateUpFromSameTask(requireActivity())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.myLooper()!!)
        apiErrorHandlingBinding=binding.errorState
        arguments?.let {
            cropId = it.getInt("cropid")
            cropName = it.getString("cropname")
            selectedCategory=it.getString("selectedCategory")

        }

        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }
        fabButton()
        binding.toolbarTitle.text = cropName


    }

    private fun pestDiseaseApiCall() {
        binding.progressBar.visibility=View.VISIBLE
        cropId?.let { cropId ->
            viewModel.getPestDiseaseListForCrop(cropId).observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        if(!it.data.isNullOrEmpty()){
                            binding.progressBar.visibility=View.GONE
                        }
                        if(!it.data.isNullOrEmpty()){
                            adapter.submitList(it.data)
                            binding.diseasesRv.adapter = adapter
                        }
                        else{
                            adapter.submitList(emptyList())
                        }
                    }
                    is Resource.Loading -> {
                        ToastStateHandling.toastWarning(requireContext(), "Loading.", Toast.LENGTH_SHORT)

                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(requireContext(), "Server Error", Toast.LENGTH_SHORT)

                    }
                }
            }
        }
        adapter.onItemClick = {
            val eventBundle=Bundle()
            eventBundle.putString("cropName",cropName)
            eventBundle.putString("diseaseName",it?.diseaseName)
            if(selectedCategory!=null){
                eventBundle.putString("selectedCategory","CropProtect_$selectedCategory")
            }
            EventItemClickHandling.calculateItemClickEvent("cropprotection_landing",eventBundle)
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
        val bannerAdapter = AdsAdapter(activity?:requireContext(), binding.bannerViewpager)
        runnable =Runnable {
            if ((bannerAdapter.itemCount - 1) == binding.bannerViewpager.currentItem)
                binding.bannerViewpager.currentItem = 0
            else
                binding.bannerViewpager.currentItem += 1
        }
        binding.bannerViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (runnable != null) {
                    AppUtil.handlerSet(handler!!,runnable!!,3000)
                }
            }
        })
        viewModel.getVansAdsList(moduleId).observe(viewLifecycleOwner) {
            bannerAdapter.submitList( it.data)
            TabLayoutMediator(binding.bannerIndicators, binding.bannerViewpager) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.itemCount}"
            }.attach()
        }
        binding.bannerViewpager.adapter = bannerAdapter
        binding.bannerViewpager.clipToPadding = false
        binding.bannerViewpager.clipChildren = false
        binding.bannerViewpager.offscreenPageLimit = 3
        binding.bannerViewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)
        bannerAdapter.onItemClick={EventClickHandling.calculateClickEvent("Crop_disease_Adbanner")}
    }

    private fun fabButton(){
        var isVisible = false
        binding.addFab.setOnClickListener {
            if(!isVisible){
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            }else{
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener {
            EventClickHandling.calculateClickEvent("call_icon")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener {
            EventClickHandling.calculateClickEvent("chat_icon")
            FeatureChat.zenDeskInit(requireContext())
        }
    }
    override fun onPause() {
        super.onPause()
        if (runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
    }
    override fun onResume() {
        super.onResume()
        if (runnable != null) {
            handler?.postDelayed(runnable!!, 3000)
        }
        EventScreenTimeHandling.calculateScreenTime("PestDiseaseFragment")
    }
}