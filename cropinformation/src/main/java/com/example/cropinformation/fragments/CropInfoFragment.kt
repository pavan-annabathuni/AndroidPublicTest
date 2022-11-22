package com.example.cropinformation.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cropinformation.R
import com.example.cropinformation.adapter.ViewpagerAdapter
import com.example.cropinformation.databinding.FragmentCropInfoBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.featurechat.Contants.Companion.CALL_NUMBER
import com.waycool.featurechat.ZendeskChat
import com.waycool.newsandarticles.adapter.BannerAdapter
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.newsandarticles.viewmodel.NewsAndArticlesViewModel
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import zendesk.chat.*
import zendesk.messaging.MessagingActivity
import kotlin.math.roundToInt


class CropInfoFragment : Fragment() {
    private lateinit var binding: FragmentCropInfoBinding
    private val ViewModel: TabViewModel by lazy {
        ViewModelProviders.of(this).get(TabViewModel::class.java)
    }
    private val viewModel by lazy { ViewModelProvider(this)[NewsAndArticlesViewModel::class.java] }

    var bannerImageList: MutableList<AdBannerImage> = java.util.ArrayList()
    private var cropId: Int? = null
    private var cropName: String? = null
    private var cropLogo:String? = null
    private var size:Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cropId = it.getInt("cropid")
            cropName = it.getString("cropname")
            cropLogo = it.getString("cropLogo")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCropInfoBinding.inflate(inflater)

       setBanners()


//        Zendesk.INSTANCE.init(this.requireContext(), zendeskUrl, appId, oauthClientId);
//        Support.INSTANCE.init(Zendesk.INSTANCE);
//        AnswerBot.INSTANCE.init(Zendesk.INSTANCE, Support.INSTANCE);
//        Chat.INSTANCE.init(this.requireContext(), chatAccountKey);

        ViewModel.getTabItem()
        binding.lifecycleOwner = this
        binding.viewModel = ViewModel

//        recycleViewIndicator()

//        binding.ViewPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topName.text = cropName
        binding.back.setOnClickListener(){
            findNavController().popBackStack()
        }
//        binding.addFab.setOnClickListener(){
//
//           // zenDesk()
//            val popupMenu = PopupMenu(context, binding.addFab,R.style.myListPopupWindow)
//            popupMenu.menuInflater.inflate(R.menu.chat_menu_cropinfo, popupMenu.menu)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                popupMenu.gravity = Gravity.END
//            }
//
//            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.call -> {
//                        val intent = Intent(Intent.ACTION_DIAL)
//                        intent.data = Uri.parse("tel:09020601234")
//                        startActivity(intent)
//                    }
//                    R.id.chat->{
//                        //zenDesk()
//                        ZendeskChat.zenDesk(requireContext())
//
//                    }
//                }
//                true
//            })
//            try{
//                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
//                fieldMPopup.isAccessible = true
//                val mPopup = fieldMPopup.get(popupMenu)
//                mPopup.javaClass
//                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
//                    .invoke(mPopup, true)
//            } catch (e: Exception){
//                Log.e("Main", "Error showing menu icons.", e)
//            } finally {
//                popupMenu.show()
//            }
//             popupMenu.show()
//
//        }
        setTabs()
        Glide.with(requireContext()).load(cropLogo).into(binding.cropLogo)
        tabCount()
        ViewModel.cropAdvisory()
        binding.ViewPager.setPageTransformer { page, position ->
            updatePagerHeightForChild(page, binding.ViewPager)
        }
        setVideos()
        setNews()
        fabButton()
    }

    private fun zenDesk() {
        Chat.INSTANCE.init(requireContext(), "dt55P5snqpfyOrXfNqz56lwrup8amDdz",
            "73015859e3bdae57c168235eb6c96f25c46e747c24bb5e8")
        val chatConfiguration = ChatConfiguration.builder()
            .withAgentAvailabilityEnabled(false)
            .withTranscriptEnabled(false)
//
            .build()
        val visitorInfo: VisitorInfo = VisitorInfo.builder()
            .withName("Bob")
            .withEmail("bob@example.com")
            .withPhoneNumber("123456") // numeric string
            .build();

        val chatProvidersConfiguration: ChatProvidersConfiguration = ChatProvidersConfiguration.builder()
            .withVisitorInfo(visitorInfo)
            .withDepartment("English Language Group")
            .build()

        Chat.INSTANCE.setChatProvidersConfiguration(chatProvidersConfiguration)


        MessagingActivity.builder()
            .withEngines(ChatEngine.engine())
            .show(requireContext(), chatConfiguration);
    }

    private fun setTabs() {

        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) { it ->
            val data = it.data!!
            size = it.data!!.size

                //data.sortedBy { it.id }
            binding.ViewPager.adapter = ViewpagerAdapter(this,it.data, data.size,cropId!!)
            binding.tvTotalItem.text = "/${data.size}"




            TabLayoutMediator(binding.tabLayout, binding.ViewPager) { tab, position ->
                val customView = tab.setCustomView(R.layout.item_tab_crop)

                when (data[position].label_name) {

                    "Crop Variety" -> {
                       tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_crop_variety)
                        customView
                    }
                    "Soil Type Others" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.planting_others)
                        customView
                    }
                    "Soil pH" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_soilph_image)
                        customView
                        // tab.text = "Soil Ph"
                    }
                    "Soil Type" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_soil_type_img)
                        customView
                    }
                    "Sowing Season" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_sowing_planting_img)
                        customView
                    }
                    "Crop Duration" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_crop_duration_img)
                        customView

                    }
                    "Days to first harvest" -> {
                        //  binding.tabLayout.getTabAt(position)?.setCustomView(R.layout.emptytab)
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_harves_img)
                        customView
                    }
                    "Seed Rate (Pit Sowing)" -> {
                        tab.text = data[position].label_name
                       tab.setIcon(R.drawable.ci_seed_pit)
                        customView

                    }
                    "Planting Material" -> {
                        tab.text = data[position].label_name
                       tab.setIcon(R.drawable.ci_planting_material)
                        customView

                    }
                    "Planting Material others" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.planting_others)
                        customView

                    }
                    "Nursery Practices" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_nursery_period_img)
                        customView
                    }
                    "Seed Rate (Line Sowing)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_seed_line)
                        customView

                    }
                    "Seed Rate (Broadcast)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_seed_broadcast)
                        customView

                    }
                    "Seed Treatment" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_seed_treatment_img)
                        customView

                    }
                    "Sowing Depth(cm)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_sowing_depth_img)
                        customView

                    }
                    "Spacing between Row to Row" -> {
                        tab.text = data[position].label_name
                       tab.setIcon(R.drawable.img_spacing)
                        customView

                    }
                    "Spacing between Plant to Plant" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_spacing)
                        customView
                    }
                    "Field preparation" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_field_preparation_img)
                        customView

                    }
                    "Mulching" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_mulching_image)
                        customView


                    }
                    "Staking" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_staking)
                        customView
                    }
                    "Distance between stakes"->{
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_staking)
                        customView
                    }
                    "Irrigation Type" -> {
                        tab.setText(data[position].label_name)
                        tab.setIcon(R.drawable.ci_irrigation_img)
                        customView
                    }
                    "Flooding" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_flooding)
                        customView
                    }
                    "Rain Gun" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_rain_gun)
                        customView
                    }
                    "Drip" -> {
                        //tab.tabLabelVisibility = TabLayout.TAB_LABEL_VISIBILITY_UNLABELED
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_drip)
                        customView
                    }

                    "Fertilizers" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_fertiliz_image)
                        customView


                    }
                    "Micronutrients" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_micro_img)
                        customView


                    }
                    "Border crop" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_border_image)
                        customView


                    }
                    "Intercrop" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_intercrop_image)
                        customView
                    }
                    "Weed control(cultural)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_weed_image)
                        customView
                    }
                    "Weed control(chemical)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_weed_chemical_img)
                        customView
                    }
                    "Harvest (sowing, planting, transplantation)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_harves_img)
                        customView
                    }
                    "Sowing/Planting-Yield" -> {
                        tab.text = data[position].label_name
                        //tab.setIcon(R.drawable.ci_sowing_planting_img)
                        customView
                    }
                    "Yield ( kg or tons/ac)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_yield_image)
                        customView
                    }
                    "Yield-Harvest" -> {
                        tab.text = data[position].label_name
                     //   tab.setIcon(R.drawable.ci_yield_image)
                        customView
                    }
                    "Post Harvesting" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_post_har_img)
                        customView
                    }
                    "Proposed Next Crops" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_pro_nextcrop_img)
                        customView
                    }
                    "Sowing/Planting" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_sowing_planting_img)
                        customView
                    }
                    "Training and Pruning" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_trimming_image)
                        customView
                    }
                    "Ratooning" -> {
                        tab.text = data[position].label_name
                       tab.setIcon(R.drawable.ci_ratooning_image)
                        customView
                    }
                    "Planting Material Others" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.planting_others)
                        customView
                    }
                    else->{
                      //  tab.tabLabelVisibility = TabLayout.TAB_LABEL_VISIBILITY_UNLABELED


                    }
                }
            }.attach()
        }
    }

    private fun tabCount() {
        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tvCount.text = (position + 1).toString()
                if(position<(size-1)){
                    binding.imgNext.visibility = View.VISIBLE
                binding.imgNext.setOnClickListener(){
                    binding.ViewPager.currentItem = position+1
                }
                }else{
                    binding.imgNext.visibility = View.GONE
                }
                if(position==0){
                  binding.imgPrev.visibility = View.GONE
                }else{
                    binding.imgPrev.visibility = View.VISIBLE
                    binding.imgPrev.setOnClickListener {
                        binding.ViewPager.currentItem = position-1
                    }
                }

            }
        }
        binding.ViewPager.registerOnPageChangeCallback(myPageChangeCallback)
//        binding.ViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                val view = // ... get the view
//                    view?.post {
//                        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view!!.width, View.MeasureSpec.EXACTLY)
//                        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//                        view?.measure(wMeasureSpec, hMeasureSpec)
//
//                        if (binding.ViewPager.layoutParams.height != view!!.measuredHeight) {
//                            // ParentViewGroup is, for example, LinearLayout
//                            // ... or whatever the parent of the ViewPager2 is
//                            binding.ViewPager.layoutParams = (binding.ViewPager.layoutParams as LinearLayout.LayoutParams)
//                                .also { lp -> lp.height = view!!.measuredHeight }
//                        }
//                    }
//            }
//        })
    }

    //Zendesk Chat and Calling Function
    private fun fabButton(){
        var isVisible = false
        binding.addFab.setOnClickListener(){
            if(!isVisible){
                binding.addFab.setImageDrawable(resources.getDrawable(R.drawable.ic_cross))
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
                        intent.data = Uri.parse(CALL_NUMBER)
                        startActivity(intent)
        }
        binding.addChat.setOnClickListener(){
            ZendeskChat.zenDesk(requireContext())
        }
    }



    private fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
        view.post {
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wMeasureSpec, hMeasureSpec)
            pager.layoutParams = (pager.layoutParams).also { lp -> lp.height = view.measuredHeight }
            pager.invalidate()
        }
    }

    private fun setNews() {
        val newsBinding: GenericLayoutNewsListBinding = binding.layoutNews
        val adapter = NewsGenericAdapter()
        newsBinding.newsListRv.adapter = adapter
        ViewModel.getVansNewsList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        newsBinding.viewAllNews.setOnClickListener {
            val intent=Intent(requireActivity(), NewsAndArticlesActivity::class.java)
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
                R.id.action_cropInfoFragment_to_newsFullviewActivity,
                bundle
            )
        }

    }

    private fun setVideos() {
        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        ViewModel.getVansVideosList().observe(requireActivity()) {
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
                R.id.action_cropInfoFragment_to_playVideoFragment2,
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


}