package com.waycool.videos.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.VansCategoryDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.DOMAIN_URI_PREFIX
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.uicomponents.utils.Constants
import com.waycool.videos.R
import com.waycool.videos.VideoViewModel
import com.waycool.videos.adapter.AdsAdapter
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.adapter.itemClick
import com.waycool.videos.databinding.FragmentVideosListBinding
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.abs


class VideosListFragment : Fragment(), itemClick {

    private var runnable: Runnable? = null
    private lateinit var binding: FragmentVideosListBinding
    private var selectedCategory: VansCategoryDomain? = null
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding


    private val videoViewModel: VideoViewModel by lazy { ViewModelProvider(this)[VideoViewModel::class.java] }

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = null


    var searchTag = ""
    val moduleId = "4"

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
        apiErrorHandlingBinding = binding.errorState
        networkErrorStateTranslations(apiErrorHandlingBinding)
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()

        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )


        videoViewModel.viewModelScope.launch {
            binding.toolbarTitle.text = TranslationsManager().getString("videos")
            binding.search.hint = TranslationsManager().getString("search")
        }
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
        fabButton()

        binding.videosVideoListRv.layoutManager = LinearLayoutManager(requireContext())
        adapterVideo = VideosPagerAdapter(requireContext(), this)
        binding.videosVideoListRv.adapter = adapterVideo



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

    }


    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.addFab.visibility = View.GONE
            binding.materialCardView.visibility = View.GONE
            AppUtils.translatedToastCheckInternet(context)



        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.addFab.visibility = View.VISIBLE
            binding.materialCardView.visibility = View.VISIBLE
            getVideoCategories()
            setBanners()
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
        tags: String? = "",
        categoryId: Int? = null
    ) {
        adapterVideo = VideosPagerAdapter(requireContext(), this)
        binding.videosVideoListRv.adapter = adapterVideo
        videoViewModel.getVansVideosList(tags, categoryId).observe(requireActivity()) {
            adapterVideo.submitData(lifecycle, it)
        }
    }

    private fun setBanners() {
        val bannerAdapter = AdsAdapter(activity ?: requireContext(), binding.bannerViewpager)
        runnable = Runnable {
            if((bannerAdapter.itemCount -1) == binding.bannerViewpager.currentItem)
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
        videoViewModel.getVansAdsList(moduleId).observe(viewLifecycleOwner) {
            bannerAdapter.submitList(it.data)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
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
        bannerAdapter.onItemClick = {
            EventClickHandling.calculateClickEvent("Video_Adbanner")
        }
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
        videoViewModel.viewModelScope.launch {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "")
        }
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

    @Deprecated("Deprecated in Java")
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

    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener() {
            if (!isVisible) {
                binding.addFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        com.waycool.uicomponents.R.drawable.ic_cross
                    )
                )
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        com.waycool.uicomponents.R.drawable.ic_chat_call
                    )
                )
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener() {
            EventClickHandling.calculateClickEvent("call_icon")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener() {
            EventClickHandling.calculateClickEvent("chat_icon")

            FeatureChat.zenDeskInit(requireContext())
        }
    }

    companion object {
        private const val REQUEST_CODE_SPEECH_INPUT = 1
    }

    override fun onItemClick(van: VansFeederListDomain?) {
        val eventBundle = Bundle()
        eventBundle.putString("VideoTitle", van?.title)
        if (selectedCategory != null) {
            eventBundle.putString("selectedCategory", "video_$selectedCategory")
        }
        EventItemClickHandling.calculateItemClickEvent("video_landing", eventBundle)
        val bundle = Bundle()
        bundle.putParcelable("video", van)
        if (selectedCategory != null) {
            bundle.putString("selectedCategory", selectedCategory.toString())
        }
        this.findNavController().navigate(
            R.id.action_videosListFragment_to_playVideoFragment, bundle

        )
    }

    override fun onShareItemClick(it: VansFeederListDomain?, view: View?) {
        binding.clShareProgress.visibility = View.VISIBLE
        val thumbnail = if (!it?.thumbnailUrl.isNullOrEmpty()) {
            it?.thumbnailUrl
        } else {
            DeepLinkNavigator.DEFAULT_IMAGE_URL
        }
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://app.outgrowdigital.com/videoshare?id=${it?.id}"))
            .setDomainUriPrefix(DOMAIN_URI_PREFIX)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse(Constants.PLAY_STORE_LINK))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setImageUrl(Uri.parse(thumbnail))
                    .setTitle("Outgrow - Hi, Checkout the video  ${it?.title}.")
                    .setDescription("Watch more videos and learn with Outgrow")
                    .build()
            )
            .buildShortDynamicLink().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.clShareProgress.visibility = View.GONE
                    Handler().postDelayed({view?.isEnabled = true
                    },1000)
                    view?.isEnabled = true
                    val shortLink: Uri? = task.result.shortLink
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, "choose one"))


                }
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
        EventScreenTimeHandling.calculateScreenTime("VideosListFragment")
    }

}