package com.example.mandiprice.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DistanceAdapter
import com.example.mandiprice.adapter.DistanceAdapter.DiffCallback
import com.example.mandiprice.databinding.FragmentMandiBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Local.LocalSource
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.NetworkUtil
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.videos.adapter.AdsAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MandiFragment : Fragment() {
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private lateinit var binding: FragmentMandiBinding

    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    private var handler: Handler? = null
    private var runnable: Runnable?=null
    private lateinit var adapterMandi: DistanceAdapter
    private var sortBy: String = "asc"
    private var orderBy: String = "distance"
    private var selectedCropCategory: String? = null
    private var selectedState: String? = null
    private var selectedCrop: String? = null
    private var search: String? = null
    private var cropCategoryId: Int? = 1
    private var count = 0
    private var lat: String? = null
    private var long: String? = null
    var distance = "Distance"
    private var price = "Price"
    var accountID = 0
    val moduleId="11"

    val arrayCat = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
//        viewModel= activity?.let {
//            ViewModelProvider(it)[MandiViewModel::class.java]}
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMandiBinding.inflate(layoutInflater)

        binding.lifecycleOwner = this
        initClickListeners()
        setBanners()
        translation()
        EventClickHandling.calculateClickEvent("Mandi_landing")

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isSuccess = findNavController().navigateUp()
                    if (!isSuccess) activity?.let { it.finish()}
                }
            }
        activity?.let {
            it.onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }

        binding.topAppBar.setNavigationOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) activity?.let { it1 -> it1.finish() }
        }
        return binding.root

    }

    private fun initClickListeners() {
        binding.searchBar.setOnClickListener {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }
        binding.SpeechtextTo.setOnClickListener {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }

        binding.topAppBar.setNavigationOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewDis.layoutManager = LinearLayoutManager(requireContext())
        apiErrorHandlingBinding = binding.errorState
        AppUtils.networkErrorStateTranslations(apiErrorHandlingBinding)

            handler = Handler(Looper.myLooper()!!)

            viewModel.viewModelScope.launch {
                adapterMandi = DistanceAdapter(DiffCallback.OnClickListener {
                    val bundle = Bundle()
                    bundle.putString("", "Mandi${it.crop}")
                    bundle.putString("", "Mandi${it.market}")
                    EventItemClickHandling.calculateItemClickEvent("Mandi_landing", bundle)
                    val args = Bundle()
                    args.putParcelable("mandiRecord", it)
                    findNavController()
                        .navigate(R.id.action_mandiFragment_to_mandiGraphFragment, args)
                }, LocalSource.getLanguageCode() ?: "en")

                viewModel.getUserDetails().observe(viewLifecycleOwner) {
                    lat = it.data?.profile?.lat.toString()
                    long = it.data?.profile?.long.toString()
                    if (it.data?.accountId != null)
                        accountID = it.data?.accountId!!
                }
                binding.recycleViewDis.adapter = adapterMandi
            }

            spinnerSetup()
            filterMenu()
            tabs()
            loadingProgressBar()
            fabButton()
            //translation()

            binding.recycleViewDis.isNestedScrollingEnabled = true
            // mandiApiCall()

            apiErrorHandlingBinding.clInternetError.setOnClickListener {
                mandiApiCall()
            }
    }

    /** to check network is working or not */
    private fun mandiApiCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
           // binding.progressBar.visibility = View.GONE
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.addFab.visibility = View.GONE
            AppUtils.translatedToastCheckInternet(context)

        } else {
            getMandiData(selectedCropCategory, selectedState, selectedCrop, sortBy, orderBy)
            //binding.progressBar.visibility = View.GONE
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.addFab.visibility = View.VISIBLE
        }

    }

    private fun loadingProgressBar() {
        /** for progress bar if paging in in loading state it will show progressbar */
        adapterMandi.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapterMandi.itemCount < 1) {
                binding.llNotFound.visibility = View.VISIBLE
                binding.recycleViewDis.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            }
            else if (loadState.source.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            }
            else {
                binding.llNotFound.visibility = View.GONE
                binding.recycleViewDis.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
        val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date())
        viewModel.viewModelScope.launch {
            val today = TranslationsManager().getString("str_today")
            binding.textView2.text = "$today $sdf"
        }
    }

    /** Filter for low to high and high to low */
    private fun filterMenu() {
        binding.filter.setOnClickListener {
            val popupMenu = PopupMenu(context, binding.filter)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_crick -> {

                        item.isChecked = !item.isChecked
                        sortBy = "asc"
                           TranslationsManager().loadString("low_to_high",binding.filter)
//                        viewModel.viewModelScope.launch {
//                            item.title = TranslationsManager().getString("low_to_high")
//                        }
                        binding.recycleViewDis.adapter = adapterMandi
                        mandiApiCall()
//                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                    }
                    R.id.action_ftbal -> { viewModel.viewModelScope.launch {
                        item.title = TranslationsManager().getString("high_to_low")
                    }
                        item.isChecked = !item.isChecked
                        sortBy = "desc"
                        binding.recycleViewDis.adapter = adapterMandi
                        mandiApiCall()
//                        getMandiData(cropCategory, state, crop, sortBy, orderBy)
                        TranslationsManager().loadString("high_to_low",binding.filter)
//                        viewModel.viewModelScope.launch {
//                            item.title = TranslationsManager().getString("low_to_high")
//                        }
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun spinnerSetup() {
        /** Spinner for crop category */
        viewModel.viewModelScope.launch {
            var category = TranslationsManager().getString("str_category")
            viewModel.getCropCategory().observe(viewLifecycleOwner) { it ->

                val cropCategoryList: MutableList<String> = (it?.data?.map { data ->
                    data.categoryName
                } ?: emptyList()) as MutableList<String>


                if (cropCategoryList.isNotEmpty())

                    if (!category.isNullOrEmpty())
                        cropCategoryList[0] = category
                    else cropCategoryList[0] = "Category"

                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, cropCategoryList)
                binding.spinner1.adapter = arrayAdapter
                binding.spinner1.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            cropCategoryId = it.data!![position].id
                            Log.d("spinnerId", "onItemSelected: $id")
                            binding.recycleViewDis.adapter = adapterMandi
                            val text = binding.spinner1.selectedItem.toString()
                            val bundleEvents = Bundle()
                            bundleEvents.putString("", "Mandi_category_filter_$text")
                            EventItemClickHandling.calculateItemClickEvent("Market_place_category", bundleEvents)
                            if (position > 0) {
                                selectedCropCategory = text
                                mandiApiCall()

                                //                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                            } else {
                                if (selectedCropCategory != null) {
                                    selectedCropCategory = ""
                                    mandiApiCall()

                                    //                            getMandiData(cropCategory, state, crop, sortBy, orderBy)

                                }
                            }
                            binding.recycleViewDis.adapter = adapterMandi
                            if (position > 0) {
                                cropSpinner(cropCategoryId)
                            } else {
                                cropSpinner()
                            }
                        }
                    }

            }
        }

        /** Spinner for state */
        viewModel.viewModelScope.launch {
            var state = TranslationsManager().getString("str_state")
            viewModel.getState().observe(viewLifecycleOwner) {
                val stateNameList = (it?.data?.data?.map { data ->
                    data.state_name
                } ?: emptyList()).toMutableList()
                stateNameList.sort()

                if (stateNameList.isNotEmpty())
                    if (!state.isNullOrEmpty())
                        stateNameList[0] = state
                    else stateNameList[0] = "State"
                val arrayAdapter3 =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, stateNameList)
                binding.spinner3.adapter = arrayAdapter3
            }
        }

        binding.spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text = binding.spinner3.selectedItem.toString()
                val bundleEvents = Bundle()
                bundleEvents.putString("", "Mandi_state_filter_$text")
                EventClickHandling.calculateClickEvent("Mandi_state_filter_$text")
                EventItemClickHandling.calculateItemClickEvent("Market_place_state", bundleEvents)
                if (position > 0) {
                    selectedState = text
                    mandiApiCall()
                } else {
                    if (selectedState == null) {
                        selectedState = ""
                        mandiApiCall()
                    }
                }
                binding.recycleViewDis.adapter = adapterMandi

            }

        }

    }

    /** Tab for price and distance */
    private fun tabs() {
        viewModel.viewModelScope.launch {
            if(binding.tabLayout.tabCount==0) {
                distance = TranslationsManager().getString("distance")
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(distance).setCustomView(R.layout.item_tab)
                )

                price = TranslationsManager().getString("Price")
                binding.tabLayout.addTab(
                    binding.tabLayout.newTab().setText(price).setCustomView(R.layout.item_tab)
                )
            }
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    /** if tab is in distance it will sort in distance and default it will be asc */
                    0 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "distance"
                            sortBy = "asc"
//                                binding.recycleViewDis.adapter = adapterMandi
                            mandiApiCall()


                        } else {
                            orderBy = "distance"
//                                binding.recycleViewDis.adapter = adapterMandi
                            mandiApiCall()


                        }

                    }
                    /** if tab is in price it will sort by price and default it will be desc */
                    1 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "price"
                            sortBy = "desc"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            getMandiData(
                                selectedCropCategory,
                                selectedState,
                                selectedCrop,
                                sortBy,
                                orderBy
                            )


                        } else {
                            orderBy = "price"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            getMandiData(
                                selectedCropCategory,
                                selectedState,
                                selectedCrop,
                                sortBy,
                                orderBy
                            )


                        }

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    /** if tab is in distance it will sort in distance and default it will be asc */
                    0 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "distance"
                            sortBy = "asc"
//                                binding.recycleViewDis.adapter = adapterMandi
                            mandiApiCall()


                        } else {
                            orderBy = "distance"
//                                binding.recycleViewDis.adapter = adapterMandi
                            mandiApiCall()


                        }

                    }
                    /** if tab is in price it will sort by price and default it will be desc */
                    1 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "price"
                            sortBy = "desc"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            getMandiData(
                                selectedCropCategory,
                                selectedState,
                                selectedCrop,
                                sortBy,
                                orderBy
                            )


                        } else {
                            orderBy = "price"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            getMandiData(
                                selectedCropCategory,
                                selectedState,
                                selectedCrop,
                                sortBy,
                                orderBy
                            )


                        }

                    }
                }
            }
        })
    }

    /** setting value for banner */
    private fun setBanners() {

        val bannerAdapter = AdsAdapter(activity ?: requireContext(), binding.bannerViewpager)
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

            bannerAdapter.submitList(it?.data)
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
    }
    /** fab button for chat and call **/
    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener {
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

    /** setting adapter and calling api **/
    private fun getMandiData(
        cropCategory: String? = null,
        state: String? = null,
        crop: String? = null,
        sortBy: String = "Distance",
        orderBy: String = "Asc",
        search: String? = null
    ) {
        if (lat != null && long != null) {
            viewModel.getMandiDetails(lat!!, long!!, cropCategory, state, crop, sortBy, orderBy, search)
                .observe(requireActivity()) {
                    adapterMandi.submitData(lifecycle, it)
                    Handler().postDelayed({
                        binding.llPorgressBar.visibility = View.GONE
                    }, 1500)
                }
        }
    }

    private fun translation() {
        var mandi = "Market Prices"
        viewModel.viewModelScope.launch {
            mandi = TranslationsManager().getString("mandi_price")
            binding.topAppBar.title = mandi
        }
        TranslationsManager().loadString(
            "str_no_data", binding.tvNoData,
            "Selected Crop or Mandi is not available with us."
        )
        TranslationsManager().loadString("search_crop_mandi", binding.searchBar)
        TranslationsManager().loadString("sort_by", binding.filter, "Sort By")

    }

    /** Spinner for crops */
    private fun cropSpinner(categoryId: Int? = null) {
        viewModel.viewModelScope.launch {
            var cropName = TranslationsManager().getString("str_crops")

            activity?.let {acti->
                viewModel.getAllCrops().observe(acti) {
                    val filter = it.data?.filter { it1 -> it1.cropCategory_id == categoryId }
                    var cropNameList = (filter?.map { data -> data.cropName } ?: emptyList()).toMutableList()

                    if (categoryId == null) {
                        cropNameList = (it.data?.map { data -> data.cropName } ?: emptyList()) as MutableList<String?>
                    }

                    if (!cropNameList.isNullOrEmpty()) {
                        cropNameList[0] = if (!cropName.isNullOrEmpty()) cropName else "Crops"
                    }

                    val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_spinner, cropNameList)
                    binding.spinner2.adapter = arrayAdapter2
                    binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val text = binding.spinner2.selectedItem.toString()
                            val bundleEvents = Bundle()
                            bundleEvents.putString("", "Mandi_crop_filter$text")
                            EventItemClickHandling.calculateItemClickEvent("Market_Place_crop", bundleEvents)
                            if (position > 0) {
                                selectedCrop = text
                                mandiApiCall()
                            } else {
                                if (selectedCrop != null) {
                                    selectedCrop = ""
                                    mandiApiCall()
                                }
                            }
                            binding.recycleViewDis.adapter = adapterMandi
                        }
                    }
                }
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
        EventScreenTimeHandling.calculateScreenTime("MandiFragment")
    }
}