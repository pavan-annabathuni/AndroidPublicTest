package com.example.mandiprice.fragments

import android.content.Intent
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DistanceAdapter
import com.example.mandiprice.adapter.DistanceAdapter.*
import com.example.mandiprice.databinding.FragmentMandiBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.newsandarticles.adapter.BannerAdapter
import com.waycool.videos.adapter.VideosPagerAdapter
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


        //binding.viewModel = viewModel

//        viewModel.pagination(cropCategory,state,crop, orderBy, "asc").observe(viewLifecycleOwner){
//            Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
//            Log.d("Pagination", "onCreateView: ${it.toString()} ")
//            val distanceAdapter = DistanceAdapter(DiffCallback.OnClickListener{
//
//            })
//            binding.recycleViewDis.adapter=distanceAdapter
//            distanceAdapter.submitData(lifecycle,it)
//            Log.d("Pagination", "onCreateView: ${distanceAdapter.itemCount} ")
//
//        }


        binding.searchBar.setOnClickListener() {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }
        binding.SpeechtextTo.setOnClickListener() {
            this.findNavController()
                .navigate(MandiFragmentDirections.actionMandiFragmentToSearchFragment())
        }

//        viewModel.status.observe(viewLifecycleOwner){
//           // Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
//        }


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
            this.findNavController()
                .navigate(R.id.action_mandiFragment_to_mandiGraphFragment,args)
        })
        binding.recycleViewDis.adapter = adapterMandi
        viewModel.viewModelScope.launch {
            viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).
            observe(viewLifecycleOwner){
               // binding.viewModel = it
                adapterMandi.submitData(lifecycle,it)
               // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
            }
        }
        setBanners()
        filterMenu()
        tabs()
        spinnerSetup()
        onClick()





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
                        sortBy = "asc"
                        binding.filter.text = "Low to High"
                        binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).
                            observe(viewLifecycleOwner){
                                adapterMandi.submitData(lifecycle,it)
                            }
                        }
                        Log.d("High", "filterMenu: $cropCategory ")

                    }
                    R.id.action_ftbal -> {
                        sortBy = "desc"
                        binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                            observe(viewLifecycleOwner){
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle,it)
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

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, crops_category)
        binding.spinner1.adapter = arrayAdapter
        binding.spinner1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    binding.recycleViewDis.adapter = adapterMandi
                    val text = binding.spinner1.selectedItem.toString()
                    cropCategory = text
                    binding.recycleViewDis.adapter = adapterMandi
                    viewModel.viewModelScope.launch {
                        viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                        observe(viewLifecycleOwner){
                            // binding.viewModel = it
                            adapterMandi.submitData(lifecycle,it)
                            // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        }

        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_spinner, crops)
        binding.spinner2.adapter = arrayAdapter2
        binding.spinner2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    val text = binding.spinner2.selectedItem.toString()
                    crop = text
                    binding.recycleViewDis.adapter = adapterMandi
                    viewModel.viewModelScope.launch {
                        viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                        observe(viewLifecycleOwner){
                            // binding.viewModel = it
                            adapterMandi.submitData(lifecycle,it)
                            // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        }
        val states = resources.getStringArray(R.array.str_states)

            val arrayAdapter3 = ArrayAdapter(requireContext(), R.layout.item_spinner,states)


            binding.spinner3.adapter = arrayAdapter3
            binding.spinner3?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        val text = binding.spinner3.selectedItem.toString()
                        state = text
                        binding.recycleViewDis.adapter = adapterMandi
                        viewModel.viewModelScope.launch {
                            viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                            observe(viewLifecycleOwner){
                                // binding.viewModel = it
                                adapterMandi.submitData(lifecycle,it)
                                // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                            }
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
                            if(binding.filter.text == "Sort by") {
                                orderBy = "distance"
                                sortBy = "asc"
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                                    observe(viewLifecycleOwner){
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle,it)
                                        Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }
                            else{
                                orderBy = "distance"
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                                    observe(viewLifecycleOwner){
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle,it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }

                        }
                        1 -> {
                            //  Toast.makeText(context, "WORKED2", Toast.LENGTH_SHORT).show()
                            if(binding.filter.text == "Sort by"){
                            orderBy = "price"
                             sortBy = "desc"
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).

                                    observe(viewLifecycleOwner){
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle,it)
                                        // Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
                                    }
                                }}
                            else{
                                orderBy = "price"
                                binding.recycleViewDis.adapter = adapterMandi
                                viewModel.viewModelScope.launch {
                                    viewModel.getMandiDetails(cropCategory, state, crop, sortBy, orderBy,search).
                                    observe(viewLifecycleOwner){
                                        // binding.viewModel = it
                                        adapterMandi.submitData(lifecycle,it)
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

    }