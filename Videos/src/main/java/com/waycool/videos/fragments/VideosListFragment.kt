package com.waycool.videos.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.Repository.DomainModels.VansCategoryDomain
import com.waycool.data.utils.Resource
import com.waycool.videos.R
import com.waycool.videos.adapter.BannerAdapter
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.databinding.FragmentVideosListBinding
import com.waycool.videos.VideoViewModel
import java.util.*


class VideosListFragment : Fragment() {

    private lateinit var binding: FragmentVideosListBinding
    private var selectedCategory: VansCategoryDomain? = null

    private val videoViewModel: VideoViewModel by lazy { ViewModelProvider(this)[VideoViewModel::class.java] }

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = null

    //banners
    var bannerImageList: MutableList<AdBannerImage> = ArrayList()

    var searchTag = ""
    var tagsAndKeywordsList = ArrayList<String>()
    var isAllFabsVisible = false
    private lateinit var adapterVideo: VideosPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVideosListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarTitle.text = "Videos"
        binding.toolbar.setNavigationOnClickListener {
            activity?.let {
                it.finish()
            }
        }

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getVideos(searchCharSequence.toString())
            }


        setBanners()
        getVideoCategories()

        binding.videosVideoListRv.layoutManager = LinearLayoutManager(requireContext())
        adapterVideo = VideosPagerAdapter(requireContext())
        binding.videosVideoListRv.adapter = adapterVideo


        adapterVideo.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)

            findNavController().navigate(
                R.id.action_videosListFragment_to_playVideoFragment,
                bundle
            )

        }


        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
            }

            override fun afterTextChanged(editable: Editable) {}
        })


        binding.micBtn.setOnClickListener { speechToText() }
        binding.cropProtectAddFab.setOnClickListener {
            if (!isAllFabsVisible) {
                binding.cropProtectCallFab.visibility = View.VISIBLE
                binding.cropProtectChatFab.visibility = View.VISIBLE
                binding.cropProtectAddFab.backgroundTintList =
                    ColorStateList.valueOf(Color.BLACK)
                binding.cropProtectAddFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_chatcall
                    )
                )
                isAllFabsVisible = true
            } else {
                binding.cropProtectCallFab.visibility = View.GONE
                binding.cropProtectChatFab.visibility = View.GONE
                binding.cropProtectAddFab.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(), com.waycool.uicomponents.R.color.primaryColor
                    )
                )
                binding.cropProtectAddFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.ic_chatcall
                    )
                )
                isAllFabsVisible = false
            }
        }

    }

    private fun getVideoCategories() {

        videoViewModel.getVansVideosCat().observe(requireActivity()) {
            if (it is Resource.Success) {
                binding.videoCategoryChipGroup.removeAllViews()
                selectedCategory = null
                val categoryList = it.data
                val allCategory = VansCategoryDomain(categoryName = "All")
                createChip(allCategory)
                if (categoryList != null) {
                    for (category in categoryList) {
                        if (category.vansType == "videos")
                            createChip(category)
                    }
                }
            }
        }
    }

    private fun createChip(category: VansCategoryDomain) {
        val chip = Chip(requireContext())
        chip.text = category.categoryName
        chip.isCheckable = true
        chip.isClickable = true
        chip.isCheckedIconVisible = false
        chip.setTextColor(
            AppCompatResources.getColorStateList(
                requireContext(),
                com.waycool.uicomponents.R.color.bg_chip_text
            )
        )
        chip.setChipBackgroundColorResource(com.waycool.uicomponents.R.color.chip_bg_selector)
        chip.chipStrokeWidth = 1f
        chip.chipStrokeColor = AppCompatResources.getColorStateList(
            requireContext(),
            com.waycool.uicomponents.R.color.bg_chip_text
        )

        if (selectedCategory == null) {
            chip.isChecked = true
            selectedCategory = category
            getVideos(categoryId = category.id)
        }


        chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                selectedCategory = category
                getVideos(categoryId = category.id)
            }
        }
        binding.videoCategoryChipGroup.addView(chip)
    }

    private fun getVideos(
        tags: String? = null,
        categoryId: Int? = null
    ) {
        videoViewModel.getVansVideosList(tags, categoryId).observe(requireActivity()) {
            adapterVideo.submitData(lifecycle, it)
        }
    }

    private fun setBanners() {
        bannerImageList.clear()

        val adBannerImage =
            AdBannerImage("https://www.digitrac.in/pub/media/magefan_blog/Wheat_crop.jpg", "1", "0")
        bannerImageList.add(adBannerImage)
        val adBannerImage2 = AdBannerImage(
            "https://cdn.telanganatoday.com/wp-content/uploads/2020/10/Paddy.jpg",
            "2",
            "1"
        )

        bannerImageList.add(adBannerImage2)

        val bannerAdapter = BannerAdapter(requireContext(), bannerImageList)
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


    private fun speechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast
                .makeText(
                    requireActivity(), " " + e.message,
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                searchTag = result?.get(0).toString()
                binding.search.setText(searchTag)
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 1
    }
}