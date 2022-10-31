package com.waycool.iwap.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.addcrop.AddCropActivity
import com.example.mandiprice.MandiActivity
import com.example.mandiprice.viewModel.MandiViewModel
import com.example.soiltesting.SoilTestActivity
import com.waycool.data.Network.NetworkModels.CropInfo
import com.waycool.featurecrophealth.CropHealthActivity
import com.waycool.featurecropprotect.CropProtectActivity
import com.waycool.featurecropprotect.R
import com.waycool.iwap.MainViewModel
import com.waycool.iwap.databinding.FragmentHomePagesBinding
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HomePagesFragment : Fragment() {

    private var _binding: FragmentHomePagesBinding? = null
    private val binding get() = _binding!!
    private var orderBy: String = "distance"
    private var cropCategory: String? = null
    private var state: String? = null
    private var crop: String? = null
    private var search: String? = null
    private var sortBy: String = "asc"

    private val viewModel by lazy { ViewModelProvider(requireActivity())[MainViewModel::class.java] }
    private val mandiViewModel by lazy { ViewModelProvider(requireActivity())[MandiViewModel::class.java] }
    private val mandiAdapter = MandiHomePageAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.layoutManager =
            GridLayoutManager(requireActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.recyclerview.adapter = mandiAdapter
        binding.tvAddFromService.setOnClickListener {
            val intent = Intent(activity, SoilTestActivity::class.java)
            startActivity(intent);
        }

        binding.tvAddFromServiceCropHealth.setOnClickListener {
            val intent = Intent(activity, CropHealthActivity::class.java)
            startActivity(intent);
        }

        binding.tvAddFromServiceCropProtect.setOnClickListener {
            val intent = Intent(activity, CropProtectActivity::class.java)
            startActivity(intent)
        }

        binding.clAddFromServiceCropInformation.setOnClickListener {
            val intent = Intent(activity, com.example.cropinformation.CropInfo::class.java)
            startActivity(intent);
        }

        binding.tvAddFrom.setOnClickListener {
            val intent = Intent(activity, AddCropActivity::class.java)
            startActivity(intent)
        }
        binding.tvViewAllMandi.setOnClickListener {
            val intent = Intent(activity, MandiActivity::class.java)
            startActivity(intent)
        }

        mandiViewModel.viewModelScope.launch {
            mandiViewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy, search)
                .observe(viewLifecycleOwner) {
                    mandiAdapter.submitData(lifecycle, it)
                    // binding.viewModel = it
//                adapterMandi.submitData(lifecycle,it)
                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                }
        }
        setVideos()
        setNews()
    }

//    private fun setMandi() {
//        mandiViewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search)
//        mandiViewModel
//    }


    private fun setNews() {
        val newsBinding: GenericLayoutNewsListBinding = binding.layoutNews
        val adapter = NewsGenericAdapter()
        newsBinding.newsListRv.adapter = adapter
        viewModel.getVansNewsList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        newsBinding.viewAllNews.setOnClickListener {
            val intent = Intent(requireActivity(), NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("title", it?.title)
            bundle.putString("content", it?.desc)
            bundle.putString("image", it?.thumbnailUrl)
            bundle.putString("audio", it?.audioUrl)
            bundle.putString("date", it?.startDate)
            bundle.putString("source", it?.sourceName)

            findNavController().navigate(
                R.id.action_pestDiseaseDetailsFragment_to_newsFullviewActivity,
                bundle
            )
        }

    }

    private fun setVideos() {
        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        viewModel.getVansVideosList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        videosBinding.viewAllVideos.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_pestDiseaseDetailsFragment_to_playVideoFragment3,
                bundle
            )
        }
        videosBinding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        videosBinding.videosListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                videosBinding.videosScroll.value =
                    calculateScrollPercentage(videosBinding).toFloat()
            }
        })
    }

    fun calculateScrollPercentage(videosBinding: GenericLayoutVideosListBinding): Int {
        val offset: Int = videosBinding.videosListRv.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.videosListRv.computeHorizontalScrollExtent()
        val range: Int = videosBinding.videosListRv.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }


}