package com.example.mandiprice.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DistanceAdapter
import com.example.mandiprice.adapter.DistanceAdapter.*
import com.example.mandiprice.databinding.FragmentMandiBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.utils.NetworkUtil
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.videos.adapter.AdsAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MandiFragment : Fragment() {
//    private var long: String? = null
//    private var lat: String? = null
//    private var price: String? = null
//    private var distance: String? = null
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private lateinit var binding: FragmentMandiBinding
    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    private lateinit var adapterMandi: DistanceAdapter
    private var sortBy: String = "asc"
    private var orderBy: String = "distance"
    private var cropCategory: String? = null
    private var state: String? = null
    private var crop: String? = null
    private var search: String? = null
    private var cropCategoryId: Int? = 1
    private var count = 0
    private var lat= "12.22"
    private var long= "78.22"
    var distance = "Distance"
    private var price = "Price"
    var accountID = 0

    val arrayCat = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMandiBinding.inflate(inflater)

        binding.lifecycleOwner = this
        initClickListeners()
        setBanners()
        translation()
        return binding.root

    }

    private fun initClickListeners() {
        binding.searchBar.setOnClickListener() {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }
        binding.SpeechtextTo.setOnClickListener() {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }

        binding.topAppBar.setNavigationOnClickListener() {
            this.findNavController().navigateUp()
        }

        setBanners()
        translation()
//        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycleViewDis.layoutManager = LinearLayoutManager(requireContext())
        apiErrorHandlingBinding = binding.errorState
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
               findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )

        adapterMandi = DistanceAdapter(DiffCallback.OnClickListener {
            val args = Bundle()
            it?.crop_master_id?.let { it1 -> args.putInt("cropId", it1) }
            it?.mandi_master_id?.let { it1 -> args.putInt("mandiId", it1) }
            adapterMandi.cropName.let { it1 -> args.putString("cropName", it1) }
            adapterMandi.marketName.let { it1 -> args.putString("market", it1) }
            it?.sub_record_id?.let { it1->args.putString("sub_record_id",it1) }
            args.putString("fragment", "one")
            this.findNavController()
                .navigate(R.id.action_mandiFragment_to_mandiGraphFragment, args)
        })
        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            lat = it.data?.profile?.lat.toString()
            long = it.data?.profile?.long.toString()
            accountID = it.data?.accountId!!
        }
        binding.recycleViewDis.adapter = adapterMandi
        spinnerSetup()
        filterMenu()
        tabs()
        onClick()
        fabButton()
        //translation()

        binding.recycleViewDis.isNestedScrollingEnabled = true
        mandiApiCall()

        apiErrorHandlingBinding.clInternetError.setOnClickListener {
            mandiApiCall()
        }

    }

    private fun mandiApiCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
            binding.progressBar.visibility = View.GONE
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.addFab.visibility = View.GONE
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check your internet connectivity",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            getMandiData(cropCategory, state, crop, sortBy, orderBy)
            binding.progressBar.visibility = View.GONE
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.addFab.visibility = View.VISIBLE
        }

    }

    private fun onClick() {
        adapterMandi.addLoadStateListener { loadState->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapterMandi.itemCount < 1) {
                binding.llNotFound.visibility = View.VISIBLE
                binding.recycleViewDis.visibility = View.GONE
            }else{
                binding.llNotFound.visibility = View.GONE
                binding.recycleViewDis.visibility = View.VISIBLE
            }
        }
        val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date())
        viewModel.viewModelScope.launch {
            val today = TranslationsManager().getString("str_today")
            binding.textView2.text = "$today $sdf"
        }
    }

    private fun filterMenu() {
        binding.filter.setOnClickListener() {
            val popupMenu = PopupMenu(context, binding.filter)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_crick -> {
                        item.isChecked = !item.isChecked
                        sortBy = "asc"
                        binding.filter.text = "Low to High"
                        binding.recycleViewDis.adapter = adapterMandi
                        mandiApiCall()
//                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                    }
                    R.id.action_ftbal -> {
                        if (item.isChecked) {
                            item.setChecked(false)
                        } else {
                            item.setChecked(true)
                        }
                        sortBy = "desc"
                        binding.recycleViewDis.adapter = adapterMandi
                        mandiApiCall()

//                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                        binding.filter.text = "High to Low"
                    }
                }
                true
            }
            popupMenu.show()
        }
    }


    private fun spinnerSetup() {
        viewModel.viewModelScope.launch {
            var category = TranslationsManager().getString("str_category")
        viewModel.getCropCategory().observe(viewLifecycleOwner) { it ->

            val cropCategoryList: MutableList<String> = (it?.data?.map { data ->
                data.categoryName
            } ?: emptyList()) as MutableList<String>


            if (cropCategoryList.isNotEmpty())

                    if(!category.isNullOrEmpty())
                    cropCategoryList[0] = category
                    else cropCategoryList[0] ="Category"

            val arrayAdapter =
                ArrayAdapter(requireContext(), R.layout.item_spinner, cropCategoryList)
            binding.spinner1.adapter = arrayAdapter
            binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    if (position > 0) {
                        cropCategory = text
                        mandiApiCall()

//                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                    } else {
                        if (cropCategory != null) {
                            cropCategory = ""
                            mandiApiCall()

//                            getMandiData(cropCategory, state, crop, sortBy, orderBy)

                        }
                    }
                    binding.recycleViewDis.adapter = adapterMandi
                    if(position>0){
                    viewModel.getAllCrops().observe(viewLifecycleOwner) {
                        val filter = it.data?.filter { it.cropCategory_id == cropCategoryId }
                        var cropNameList = (filter?.map { data ->
                            data.cropName
                        } ?: emptyList()).toMutableList()

                        if (cropNameList.size==0)
                            cropNameList = (it.data?.map { data ->
                                data.cropName
                            } ?: emptyList()).toMutableList()

                        val arrayAdapter2 =
                            ArrayAdapter(requireContext(), R.layout.item_spinner, cropNameList)
                        binding.spinner2.adapter = arrayAdapter2
                        binding.spinner2?.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {

                                    val text = binding.spinner2.selectedItem.toString()
                                    if (position > 0) {
                                        crop = text
                                        mandiApiCall()

//                                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                                    } else {
                                        if (crop != null) {
                                            crop = ""
                                            mandiApiCall()

//                                            getMandiData(cropCategory, state, crop, sortBy, orderBy)
                                        }
                                    }
                                    binding.recycleViewDis.adapter = adapterMandi

                                }
                            }

                    }
                    }else{
                        viewModel.getAllCrops().observe(viewLifecycleOwner) {
                            // val filter = it.data?.filter { it.cropCategory_id == cropCategoryId }
                            var cropNameList = (it.data?.map { data ->
                                data.cropName
                            } ?: emptyList()).toMutableList()


//            if (cropNameList.size==0)
//                cropNameList = (it.data?.get(0)?.cropNameTag ?: "") as MutableList<String?>

                            val arrayAdapter2 =
                                ArrayAdapter(requireContext(), R.layout.item_spinner, cropNameList)
                            binding.spinner2.adapter = arrayAdapter2
                            binding.spinner2?.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onNothingSelected(parent: AdapterView<*>?) {

                                    }

                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {

                                        val text = binding.spinner2.selectedItem.toString()
                                        if (position > 0) {
                                            crop = text
                                            mandiApiCall()

//                                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                                        } else {
                                            if (crop != null) {
                                                crop = ""
                                                mandiApiCall()

//                                            getMandiData(cropCategory, state, crop, sortBy, orderBy)
                                            }
                                        }
                                        binding.recycleViewDis.adapter = adapterMandi

                                    }
                                }

                        }
                    }}
                }

            }}

        viewModel.getAllCrops().observe(viewLifecycleOwner) {
           // val filter = it.data?.filter { it.cropCategory_id == cropCategoryId }
            var cropNameList = (it.data?.map { data ->
                data.cropName
            } ?: emptyList()).toMutableList()

//            if (cropNameList.size==0)
//                cropNameList = (it.data?.get(0)?.cropNameTag ?: "") as MutableList<String?>

            val arrayAdapter2 =
                ArrayAdapter(requireContext(), R.layout.item_spinner, cropNameList)
            binding.spinner2.adapter = arrayAdapter2
            binding.spinner2?.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

                        val text = binding.spinner2.selectedItem.toString()
                        if (position > 0) {
                            crop = text
                            mandiApiCall()

//                                        getMandiData(cropCategory, state, crop, sortBy, orderBy)

                        } else {
                            if (crop != null) {
                                crop = ""
                                mandiApiCall()

//                                            getMandiData(cropCategory, state, crop, sortBy, orderBy)
                            }
                        }
                        binding.recycleViewDis.adapter = adapterMandi

                    }
                }

        }

        viewModel.viewModelScope.launch {
            var state = TranslationsManager().getString("str_state")
            viewModel.getState().observe(viewLifecycleOwner) {
                val stateNameList = (it?.data?.data?.map { data ->
                    data.state_name
                } ?: emptyList()).toMutableList()
                stateNameList.sort()

                if (stateNameList.isNotEmpty())
                    if(!state.isNullOrEmpty())
                    stateNameList[0] = state
                else stateNameList[0] = "State"
                val arrayAdapter3 =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, stateNameList)
                binding.spinner3.adapter = arrayAdapter3
            }
        }

        binding.spinner3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text = binding.spinner3.selectedItem.toString()
                if (position > 0) {
                    state = text
                    getMandiData(cropCategory, state, crop, sortBy, orderBy)
                } else {
                    if (state == null) {
                        state = ""
                        getMandiData(cropCategory, state, crop, sortBy, orderBy)
                    }
                }
                binding.recycleViewDis.adapter = adapterMandi

            }

        }

    }


    private fun tabs() {
        viewModel.viewModelScope.launch {
            distance = TranslationsManager().getString("distance")
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(distance).setCustomView(R.layout.item_tab)
            )

            price = TranslationsManager().getString("Price")
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(price).setCustomView(R.layout.item_tab)
        )}
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "distance"
                            sortBy = "asc"
//                                binding.recycleViewDis.adapter = adapterMandi
                            getMandiData(cropCategory, state, crop, sortBy, orderBy)


                        } else {
                            orderBy = "distance"
//                                binding.recycleViewDis.adapter = adapterMandi
                            getMandiData(cropCategory, state, crop, sortBy, orderBy)


                        }

                    }
                    1 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "price"
                            sortBy = "desc"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            getMandiData(cropCategory, state, crop, sortBy, orderBy)


                        } else {
                            orderBy = "price"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            getMandiData(cropCategory, state, crop, sortBy, orderBy)


                        }

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // viewModel.getMandiRecord(cropCategory,orderBy,sortBy)
            }
        })
    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter(requireContext())
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

    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener() {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
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
            FeatureChat.zenDeskInit(requireContext())
        }
    }

    private fun getMandiData(
        cropCategory: String? = null,
        state: String? = null,
        crop: String? = null,
        sortBy: String? = "Distance",
        orderBy: String? = "Asc",
        search: String? = null
    ) {
        viewModel.viewModelScope.launch {
            if (lat != null && long != null)
                viewModel.getMandiDetails(
                    lat!!,
                    long!!,
                    cropCategory,
                    state,
                    crop,
                    sortBy,
                    orderBy,
                    search
                )
                    .observe(requireActivity()) {
                        adapterMandi.submitData(lifecycle, it)
                        Handler().postDelayed({
                            binding.llPorgressBar.visibility = View.GONE


                        }, 1500)


                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun translation(){
        var mandi = "Mandi Price"
        viewModel.viewModelScope.launch {
            mandi = TranslationsManager().getString("mandi_price")
            binding.topAppBar.title = mandi
        }
        TranslationsManager().loadString("search_crop_mandi",binding.searchBar)
        TranslationsManager().loadString("sort_by",binding.filter,"Sort By")

    }
}