package com.waycool.newsandarticles.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.repository.domainModels.VansSharedDataDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.data.utils.SpeechToText
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.DOMAIN_URI_PREFIX
import com.waycool.newsandarticles.adapter.NewsPagerAdapter
import com.waycool.newsandarticles.adapter.onItemClickNews
import com.waycool.newsandarticles.databinding.ActivityNewsAndArticlesBinding
import com.waycool.newsandarticles.viewmodel.NewsAndArticlesViewModel
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.uicomponents.utils.Constants
import com.waycool.videos.adapter.AdsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NewsAndArticlesActivity : AppCompatActivity(), onItemClickNews {
    private var vans: VansSharedDataDomain? = null
    private var searchTag: CharSequence? = ""
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private var selectedCategory: String? = null
    private var searchCharSequence: CharSequence? = null
    private lateinit var newsAdapter: NewsPagerAdapter
    private var handler: Handler? = null
    private var runnable: Runnable?=null
    val moduleId="4"

    val binding: ActivityNewsAndArticlesBinding by lazy {
        ActivityNewsAndArticlesBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy { ViewModelProvider(this)[NewsAndArticlesViewModel::class.java] }

    private val newsCategoryList = listOf("All", "News", "Articles")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener {
          onBackPressed()
        }



        apiErrorHandlingBinding=binding.errorState
        networkErrorStateTranslations(apiErrorHandlingBinding)

        networkCall()
        if(NetworkUtil.getConnectivityStatusString(this)==0){
            networkCall()
        }


        binding.videosVideoListRv.layoutManager = LinearLayoutManager(this)

        newsAdapter = NewsPagerAdapter(this,this)
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
        translation()

        DeepLinkNavigator.navigateFromDeeplink(this@NewsAndArticlesActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink != null) {
                if (deepLink?.lastPathSegment!! == DeepLinkNavigator.NEWS_ARTICLE) {
                    val id = deepLink.getQueryParameter("id")
                    Log.d("NADeepLink","NADeepLink1 $deepLink  $id")
                    if (!id.isNullOrEmpty()) {
                        Log.d("NADeepLink","NADeepLink2 $id")
                        viewModel.getVansSharedData(id.toInt()).observe(this,androidx.lifecycle.Observer{
                            when(it){
                                is Resource.Success->{
                                    val vans =it.data
                                    Log.d("NADeepLink","NADeepLink2 $vans")
                                    val intent = Intent(this@NewsAndArticlesActivity, NewsAndArticlesFullViewActivity::class.java)
                                    intent.putExtra("id",vans?.id)
                                    intent.putExtra("title", vans?.title)
                                    intent.putExtra("content", vans?.desc)
                                    intent.putExtra("image", vans?.thumbnail_url)
                                    intent.putExtra("audio", vans?.audio_url)
                                    intent.putExtra("date", vans?.updated_at)
                                    intent.putExtra("source", vans?.source_name)
                                    intent.putExtra("vansType", vans?.vans_type)
                                    startActivity(intent)
                                }
                                is Resource.Loading->{
                                    Log.d("NADeepLink","NADeepLink2 load")

                                }
                                is Resource.Error->{
                                    Log.d("NADeepLink","NADeepLink2 error ${it.message}")

                                }
                            }

                        })
                    }
                }

            }
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
    }

    private fun networkCall() {
        if(NetworkUtil.getConnectivityStatusString(this)==0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.addFab.visibility = View.GONE
            binding.materialCardView.visibility = View.GONE
            this.let {
                CoroutineScope(Dispatchers.Main).launch {
                    val toastCheckInternet = TranslationsManager().getString("check_your_interent")
                    if (!toastCheckInternet.isNullOrEmpty()) {

                        ToastStateHandling.toastError(
                            this@NewsAndArticlesActivity, toastCheckInternet,
                            LENGTH_SHORT
                        )

                    } else {
                        AppUtils.translatedToastCheckInternet(this@NewsAndArticlesActivity)
                    }
                }

            }
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


    private fun setBanners() {
        val bannerAdapter = AdsAdapter(this@NewsAndArticlesActivity, binding.bannerViewpager)
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
        viewModel.getVansAdsList(moduleId).observe(this) { bannerAdapter.submitList(it.data)
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
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)
        bannerAdapter.onItemClick={
            EventClickHandling.calculateClickEvent("NewsArticles_Adbanner")
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
        viewModel.viewModelScope.launch {
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, SpeechToText.getLangCode())}
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

    @Deprecated("Deprecated in Java")
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
        binding.addFab.setOnClickListener {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(this,com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(this,com.waycool.uicomponents.R.drawable.ic_chat_call))
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
            FeatureChat.zenDeskInit(this)
        }
    }

    companion object {
        private val REQUEST_CODE_SPEECH_INPUT = 1
    }

    override fun onItemClick(it: VansFeederListDomain?) {
        val eventBundle=Bundle()
        eventBundle.putString("NewsAndArticlesTitle",it?.title)
        if(selectedCategory!=null){
            eventBundle.putString("selectedCategory","NewsArticles_$selectedCategory")
        }
        EventItemClickHandling.calculateItemClickEvent("NewsArticles_landing",eventBundle)
        val intent = Intent(this@NewsAndArticlesActivity, NewsAndArticlesFullViewActivity::class.java)
        intent.putExtra("id", it?.id)
        intent.putExtra("title", it?.title)
        intent.putExtra("content", it?.desc)
        intent.putExtra("image", it?.thumbnailUrl)
        intent.putExtra("audio", it?.audioUrl)
        intent.putExtra("date", it?.startDate)
        intent.putExtra("source", it?.sourceName)
        intent.putExtra("vansType", it?.vansType)
        startActivity(intent)
    }

    override fun onShareItemClick(it: VansFeederListDomain?, view: View) {
        binding.clShareProgress.visibility=View.VISIBLE
        val thumbnail = if(!it?.thumbnailUrl.isNullOrEmpty()){
            it?.thumbnailUrl
        } else{
            DeepLinkNavigator.DEFAULT_IMAGE_URL
        }
        val eventBundle=Bundle()
        eventBundle.putString("NewsAndArticlesTitle", it?.title)
        if(selectedCategory!=null){
            eventBundle.putString("selectedCategory","NewsArticles_$selectedCategory")
        }
        EventItemClickHandling.calculateItemClickEvent("NewsArticles_share",eventBundle)

        Log.d("NewsAndArticlesShare","NewsAndArticles Domain $DOMAIN_URI_PREFIX")

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://app.outgrowdigital.com/newsandarticlesfullscreen?id=${it?.id}"))
            .setDomainUriPrefix(DOMAIN_URI_PREFIX)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse(Constants.PLAY_STORE_LINK))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setImageUrl(Uri.parse(thumbnail))
                    .setTitle("Outgrow - Hi, Checkout the News and Articles about ${it?.title}.")
                    .setDescription("Watch more News and Articles and learn with Outgrow")
                    .build()
            )
            .buildShortDynamicLink().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.clShareProgress.visibility=View.GONE
                    Handler().postDelayed({view.isEnabled = true
                    },1000)
                    val shortLink: Uri? = task.result.shortLink
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    Log.d("NewsAndArticlesShare","NewsAndArticles Short link ${shortLink.toString()}")

                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, "choose one"))

                }
                else{
                    Log.d("NewsAndArticlesShare","NewsAndArticlesShare ${task.exception}")
                }
            }
    }
    private fun translation(){
        viewModel.viewModelScope.launch {
            binding.toolbarTitle.text = TranslationsManager().getString("str_news")
            binding.search.hint = TranslationsManager().getString("search")
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
        EventScreenTimeHandling.calculateScreenTime("NewsAndArticlesActivity")
    }
}



