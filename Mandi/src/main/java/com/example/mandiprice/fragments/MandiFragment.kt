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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
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
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.repository.domainModels.CropCategoryMasterDomain
import com.waycool.featurechat.Contants
import com.waycool.featurechat.ZendeskChat
import com.waycool.newsandarticles.adapter.BannerAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MandiFragment : Fragment() {
    private lateinit var binding: FragmentMandiBinding
    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    var bannerImageList: MutableList<AdBannerImage> = java.util.ArrayList()
    private lateinit var adapterMandi: DistanceAdapter
    private var crops_category =
        arrayOf("Category", "Cereals", "Pulses", "Vegetables", "Fruits", "Spices", "Others")
    private var crops = arrayOf("Crops", "Watermelon", "Apple", "Orange", "Mango")
    private var sortBy: String = "asc"
    private var orderBy: String = "distance"
    private var cropCategory: String? = null
    private var state: String? = null
    private var crop: String? = null
    private var search: String? = null
    private var crop_category_id: Int? = 1
    private var count = 0

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
        // Inflate the layout for this fragment
        binding = FragmentMandiBinding.inflate(inflater)
        binding.lifecycleOwner = this


        binding.searchBar.setOnClickListener() {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }
        binding.SpeechtextTo.setOnClickListener() {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }

        binding.topAppBar.setNavigationOnClickListener() {
            activity?.finish()
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycleViewDis.layoutManager = LinearLayoutManager(requireContext())
        adapterMandi = DistanceAdapter(DiffCallback.OnClickListener {
            val args = Bundle()
            it?.crop_master_id?.let { it1 -> args.putInt("cropId", it1) }
            it?.mandi_master_id?.let { it1 -> args.putInt("mandiId", it1) }
            it?.crop?.let { it1 -> args.putString("cropName", it1) }
            it?.market?.let { it1 -> args.putString("market", it1) }
            args.putString("fragment", "one")
            this.findNavController()
                .navigate(R.id.action_mandiFragment_to_mandiGraphFragment, args)
        })
        binding.recycleViewDis.adapter = adapterMandi
        viewModel.viewModelScope.launch {
            viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy, search)
                .observe(requireActivity()) {
                    adapterMandi.submitData(lifecycle, it)
                    Handler().postDelayed({
                        binding.llPorgressBar.visibility = View.GONE
                    }, 1500)


                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                }
        }
        spinnerSetup()
        setBanners()
        filterMenu()
        tabs()
        onClick()
        fabButton()


    }

    private fun onClick() {
//        viewModel.status.observe(viewLifecycleOwner) {
//            when (it) {
//                "true" -> {
//                    binding.llNotFound.visibility = View.GONE
//                    binding.recycleViewDis.visibility = View.VISIBLE
//                }
//                "Failed" -> {
//                    binding.llNotFound.visibility = View.VISIBLE
//                    binding.recycleViewDis.visibility = View.GONE
//                }

        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
        // Log.d("status", "onClick:$it ")
        // }
        val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date())
        binding.textView2.text = "Today $sdf"
    }

    private fun filterMenu() {
        binding.filter.setOnClickListener() {
            val popupMenu = PopupMenu(context, binding.filter)
            popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_crick -> {
                        if (item.isChecked) {
                            item.setChecked(false)
                        } else {
                            item.setChecked(true)
                        }
                        sortBy = "asc"
                        binding.filter.text = "Low to High"
                        binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(
                                cropCategory,
                                state,
                                crop,
                                sortBy,
                                orderBy,
                                search
                            ).observe(viewLifecycleOwner) {
                                adapterMandi.submitData(lifecycle, it)
                            }
                        }
                        Log.d("High", "filterMenu: $cropCategory ")

                    }
                    R.id.action_ftbal -> {
                        if (item.isChecked) {
                            item.setChecked(false)
                        } else {
                            item.setChecked(true)
                        }
                        sortBy = "desc"
                        binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(
                                cropCategory,
                                state,
                                crop,
                                sortBy,
                                orderBy,
                                search
                            ).observe(viewLifecycleOwner) {
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle, it)
                                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
                        }
                        binding.filter.text = "High to Low"
                    }
                }
                true
            })
            popupMenu.show()
        }
    }


    private fun spinnerSetup() {
        viewModel.getCropCategory().observe(viewLifecycleOwner) { it ->

            val cropCategoryList: MutableList<String> = (it?.data?.map { data ->
                data.categoryName
            } ?: emptyList()) as MutableList<String>
            if (cropCategoryList.isNotEmpty())
                cropCategoryList[0] = "-Category-"
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
                    crop_category_id = it.data!![position].id
                    Log.d("spinnerId", "onItemSelected: $id")
                    binding.recycleViewDis.adapter = adapterMandi
                    val text = binding.spinner1.selectedItem.toString()
                    if (position > 0) {
                        cropCategory = text
                    } else cropCategory = ""
                    binding.recycleViewDis.adapter = adapterMandi
                    viewModel.viewModelScope.launch {
                        viewModel.getMandiDetails(
                            cropCategory,
                            state,
                            crop,
                            sortBy,
                            orderBy,
                            search
                        ).observe(viewLifecycleOwner) {
                            // binding.viewModel = it
                            adapterMandi.submitData(lifecycle, it)
                            // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.getAllCrops().observe(viewLifecycleOwner) {
                        val filter = it.data?.filter { it.cropCategory_id == crop_category_id }
                        val cropNameList = (filter?.map { data ->
                            data.cropName
                        } ?: emptyList()).toMutableList()

                        if (cropNameList.isNotEmpty())
                            cropNameList[0] = "-Crops-"

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
                                    } else crop = ""

                                    binding.recycleViewDis.adapter = adapterMandi
                                    viewModel.viewModelScope.launch {
                                        viewModel.getMandiDetails(
                                            cropCategory,
                                            state,
                                            crop,
                                            sortBy,
                                            orderBy,
                                            search
                                        ).observe(viewLifecycleOwner) {
                                            // binding.viewModel = it
                                            adapterMandi.submitData(lifecycle, it)
                                            // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                        }

                                    }
                                }
                            }

                    }
                }

            }
        }

        viewModel.viewModelScope.launch {
            viewModel.getState().observe(viewLifecycleOwner) {
                val stateNameList = (it?.data?.data?.map { data ->
                    data.state_name
                } ?: emptyList()).toMutableList()
                stateNameList.sort()

                if (stateNameList.isNotEmpty())
                    stateNameList[0] = "-State-"
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
                } else state = ""

                binding.recycleViewDis.adapter = adapterMandi
                viewModel.viewModelScope.launch {
                    viewModel.getMandiDetails(
                        cropCategory,
                        state,
                        crop,
                        sortBy,
                        orderBy,
                        search
                    ).observe(viewLifecycleOwner) {
                        // binding.viewModel = it
                        adapterMandi.submitData(lifecycle, it)
                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }


    private fun tabs() {

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Distance").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("Price").setCustomView(R.layout.item_tab)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (binding.tabLayout.selectedTabPosition) {
                    0 -> {
                        if (binding.filter.text == "Sort by") {
                            orderBy = "distance"
                            sortBy = "asc"
//                                binding.recycleViewDis.adapter = adapterMandi
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    // binding.viewModel = it
                                    adapterMandi.submitData(lifecycle, it)
                                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            orderBy = "distance"
//                                binding.recycleViewDis.adapter = adapterMandi
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    // binding.viewModel = it
                                    adapterMandi.submitData(lifecycle, it)
                                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                }
                            }

                        }

                    }
                    1 -> {
                        //  Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()
                        if (binding.filter.text == "Sort by") {
                            orderBy = "price"
                            sortBy = "desc"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    // binding.viewModel = it
                                    adapterMandi.submitData(lifecycle, it)
                                    Handler().postDelayed({
                                        binding.llPorgressBar.visibility = View.GONE
                                    }, 2000)
                                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            orderBy = "price"
                            binding.recycleViewDis.adapter = adapterMandi
                            binding.llPorgressBar.visibility = View.VISIBLE
                            viewModel.viewModelScope.launch {
                                viewModel.getMandiDetails(
                                    cropCategory,
                                    state,
                                    crop,
                                    sortBy,
                                    orderBy,
                                    search
                                ).observe(viewLifecycleOwner) {
                                    // binding.viewModel = it
                                    adapterMandi.submitData(lifecycle, it)
                                    Handler().postDelayed({
                                        binding.llPorgressBar.visibility = View.GONE
                                    }, 2000)

                                    // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                }
                            }
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
            ZendeskChat.zenDesk(requireContext())
        }
    }
}