package com.waycool.cropprotect.fragments

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
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.Repository.DomainModels.CropCategoryMasterDomain
import com.waycool.data.utils.Resource
import com.waycool.featurecropprotect.Adapter.BannerAdapter
import com.waycool.featurecropprotect.Adapter.DiseasesParentAdapter
import com.waycool.featurecropprotect.CropProtectViewModel
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.FragmentPestDiseaseBinding
import java.util.ArrayList


class PestDiseaseFragment : Fragment() {

    private var selectedCategory: CropCategoryMasterDomain? = null
    private lateinit var binding: FragmentPestDiseaseBinding
    private val viewModel: CropProtectViewModel by lazy {
        ViewModelProvider(requireActivity())[CropProtectViewModel::class.java]
    }
    private var cropId: Int? = null
    private var cropName: String? = null

    //banners
    private var bannerImageList: MutableList<AdBannerImage> = ArrayList()
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
        arguments?.let {
            cropId = it.getInt("cropid")
            cropName = it.getString("cropname")
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbarTitle.text = cropName

        binding.diseasesRv.adapter = adapter

        cropId?.let { cropId ->
            viewModel.getPestDiseaseListForCrop(cropId).observe(requireActivity()) {
                when (it) {
                    is Resource.Success -> {
//                        Toast.makeText(requireContext(),"Success: ${it.data?.size}",Toast.LENGTH_SHORT).show()
                        if (it.data == null)
                            adapter.submitList(emptyList())
                        else
                            adapter.submitList(it.data)
                    }
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Loadind..", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            }
        }
        adapter.onItemClick = {
            val args = Bundle()
            it?.diseaseId?.let { it1 -> args.putInt("diseaseid", it1) }
            it?.diseaseName?.let { it1 -> args.putString("diseasename", it1) }
            findNavController().navigate(
                R.id.action_pestDiseaseFragment_to_pestDiseaseDetailsFragment,
                args
            )
        }
        setBanners()
    }

    private fun setBanners() {
        val adBannerImage =
            AdBannerImage("https://www.digitrac.in/pub/media/magefan_blog/Wheat_crop.jpg", "1", "0")
        bannerImageList.add(adBannerImage)
        val adBannerImage2 = AdBannerImage(
            "https://cdn.telanganatoday.com/wp-content/uploads/2020/10/Paddy.jpg",
            "2",
            "1"
        )
        bannerImageList.add(adBannerImage2)

        val bannerAdapter = BannerAdapter(requireActivity())
        bannerAdapter.submitList(bannerImageList)
        binding.bannerViewpager.adapter = bannerAdapter
        TabLayoutMediator(
            binding.bannerIndicators, binding.bannerViewpager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = "${position + 1} / ${bannerImageList.size}"
        }.attach()

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


}