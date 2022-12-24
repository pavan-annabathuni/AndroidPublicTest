package com.waycool.newsandarticles.view

import androidx.appcompat.app.AppCompatActivity
import com.waycool.newsandarticles.viewmodel.NewsAndArticlesViewModel
import android.os.Bundle
import android.text.TextWatcher
import android.text.Editable
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.net.Uri
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.utils.NetworkUtil
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.newsandarticles.Util.AppUtil
import com.waycool.newsandarticles.adapter.AdsAdapter
import com.waycool.newsandarticles.adapter.BannerAdapter
import com.waycool.newsandarticles.adapter.NewsPagerAdapter
import com.waycool.newsandarticles.databinding.ActivityNewsAndArticlesBinding
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class NewsAndArticlesActivity : AppCompatActivity() {
    private var searchTag: CharSequence? = ""
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding


    private var selectedCategory: String? = null

    private var handler: Handler? = null
    private var searchCharSequence: CharSequence? = null


    private lateinit var newsAdapter: NewsPagerAdapter

    val binding: ActivityNewsAndArticlesBinding by lazy {
        ActivityNewsAndArticlesBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy { ViewModelProvider(this)[NewsAndArticlesViewModel::class.java] }

    private val newsCategoryList = listOf("All", "News", "Articles")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.toolbarTitle.text = "News & Articles"
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        apiErrorHandlingBinding=binding.errorState
        networkCall()
        if(NetworkUtil.getConnectivityStatusString(this)==0){
            networkCall()
        }


        binding.videosVideoListRv.layoutManager = LinearLayoutManager(this)

        newsAdapter = NewsPagerAdapter(this)
        binding.videosVideoListRv.adapter = newsAdapter

        handler = Handler(Looper.myLooper()!!)
        val searchRunnable =
            Runnable {
                getNews(tags = searchCharSequence.toString())
            }

        //for slider banner
        setBanners()
        getNewsCategories()
        fabButton()

        CoroutineScope(Dispatchers.Main).launch {
            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@NewsAndArticlesActivity, LoginMainActivity::class.java)
                startActivity(intent)
                this@NewsAndArticlesActivity.finish()

            }else{
                Firebase.dynamicLinks
                    .getDynamicLink(intent)
                    .addOnSuccessListener {pendingDynamicLinkData: PendingDynamicLinkData? ->

                        var deepLink: Uri? = null
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                        }
                        if (deepLink != null) {

                            val intent =
                                Intent(this@NewsAndArticlesActivity, NewsFullviewActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                            e -> Log.w("TAG", "getDynamicLink:onFailure", e)
                    }
            }
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                searchCharSequence = charSequence
                handler!!.removeCallbacks(searchRunnable)
                handler!!.postDelayed(searchRunnable, 150)
//                if (charSequence.isNotEmpty()) {
//                    binding.micBtn.visibility = View.GONE
//                } else {
//                    binding.micBtn.visibility = View.VISIBLE
//                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        newsAdapter.onItemShareClick = {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://adminuat.outgrowdigital.com/videoshare?title=${it?.title}&content=${it?.desc}&image=${it?.thumbnailUrl}&audio=${it?.audioUrl}&date=${it?.startDate}&source=${it?.sourceName}"))
                .setDomainUriPrefix("https://outgrowdev.page.link")
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
                        .build()
                )
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setImageUrl(Uri.parse("https://gramworkx.com/PromotionalImages/gramworkx_roundlogo_white_outline.png"))
                        .setTitle("Outgrow - Hi, Checkout the video on ${it?.title}.")
                        .setDescription("Watch more videos and learn with Outgrow")
                        .build()
                )
                .buildShortDynamicLink().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val shortLink: Uri? = task.result.shortLink
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                        sendIntent.type = "text/plain"
                        startActivity(Intent.createChooser(sendIntent, "choose one"))

                    }
                }

            Firebase.dynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }
                    if (deepLink != null) {

                        val intent =
                            Intent(this@NewsAndArticlesActivity, NewsFullviewActivity::class.java)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
        }
        newsAdapter.onItemClick = {
            val intent = Intent(this@NewsAndArticlesActivity, NewsFullviewActivity::class.java)
            intent.putExtra("title", it?.title)
            intent.putExtra("content", it?.desc)
            intent.putExtra("image", it?.thumbnailUrl)
            intent.putExtra("audio", it?.audioUrl)
            intent.putExtra("date", it?.startDate)
            intent.putExtra("source", it?.sourceName)
            startActivity(intent)
        }

        binding.micBtn.setOnClickListener { speechToText() }
    }

    private fun networkCall() {
        if(NetworkUtil.getConnectivityStatusString(this)==0){
            binding.clInclude.visibility= View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility= View.VISIBLE
            binding.addFab.visibility= View.GONE
            binding.materialCardView.visibility=View.GONE
            this?.let { ToastStateHandling.toastWarning(it,"Please check your internet connectivity",Toast.LENGTH_SHORT) }


        }else{
            binding.clInclude.visibility= View.GONE
            apiErrorHandlingBinding.clInternetError.visibility= View.GONE
            binding.addFab.visibility= View.VISIBLE
            binding.materialCardView.visibility=View.VISIBLE
            getNewsCategories()
            setBanners()
        }

    }

    private fun getNewsCategories() {
        binding.videoCategoryChipGroup.removeAllViews()
        selectedCategory = null
        for (category in newsCategoryList) {
            createChip(category)
        }

    }

    private fun createChip(category: String) {
        val chip = Chip(this)
        chip.text = category
        chip.isCheckable = true
        chip.isClickable = true
        chip.isCheckedIconVisible = false
        chip.setTextColor(
            AppCompatResources.getColorStateList(
                this,
                com.waycool.uicomponents.R.color.bg_chip_text
            )
        )
        chip.setChipBackgroundColorResource(com.waycool.uicomponents.R.color.chip_bg_selector)
        chip.chipStrokeWidth = 1f
        chip.chipStrokeColor = AppCompatResources.getColorStateList(
            this,
            com.waycool.uicomponents.R.color.bg_chip_text
        )

        if (selectedCategory == null) {
            chip.isChecked = true
            selectedCategory = category
            if (category == "All")
                getNews()
            else getNews(vansType = category)
        }


        chip.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (b) {
                selectedCategory = category
                if (category == "All")
                    getNews()
                else getNews(vansType = category)
            }
        }
        binding.videoCategoryChipGroup.addView(chip)
    }

    private fun getNews(
        vansType: String? = null,
        tags: String? = null
    ) {

        viewModel.getVansNewsList(vansType, tags).observe(this) {
            newsAdapter.submitData(lifecycle, it)
        }
    }


    fun shareClick(
        pos: Int,
        url: String?,
        content: String,
        imageView: ImageView?,
        appLink: String
    ) {
        Log.d("url", (url)!!)
        AppUtil.shareItem(this@NewsAndArticlesActivity, url, imageView, content + "\n" + appLink)
    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter()
        viewModel.getVansAdsList().observe(this) {

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
                    this@NewsAndArticlesActivity, " " + e.message,
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
            if (resultCode == RESULT_OK && data != null) {
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
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(resources.getDrawable(com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener() {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener() {
            FeatureChat.zenDeskInit(this)
        }
    }

    companion object {
        private val REQUEST_CODE_SPEECH_INPUT = 1
    }
}