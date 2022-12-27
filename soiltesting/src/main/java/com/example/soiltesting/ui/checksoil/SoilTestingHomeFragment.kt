package com.example.soiltesting.ui.checksoil

import android.content.ContentValues
import android.content.Intent

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentSoilTestingHomeBinding
import com.example.soiltesting.ui.history.HistoryDataAdapter
import com.example.soiltesting.ui.history.HistoryViewModel
import com.example.soiltesting.ui.history.StatusTrackerListener
import com.example.soiltesting.utils.Constant.TAG
import com.example.soiltesting.utils.NetworkResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class SoilTestingHomeFragment : Fragment(), StatusTrackerListener {
    private lateinit  var binding: FragmentSoilTestingHomeBinding
//    private val binding get() = _binding!!
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private var soilHistoryAdapter = HistoryDataAdapter(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var accountID: Int? = null
    private var module_id = "22"


    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }

    private val checkSoilTestViewModel by lazy { ViewModelProvider(this)[CheckSoilLabViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSoilTestingHomeBinding.inflate(layoutInflater)
        soilHistoryAdapter = HistoryDataAdapter(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        apiErrorHandlingBinding = binding.errorState
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        binding.recyclerview.adapter = soilHistoryAdapter

        initViewClick()
        initViewBackClick()
        expandableView()
        expandableViewTWo()
        binding.clProgressBar.visibility=View.VISIBLE
        expandableViewThree()
        getVideos()
        fabButton()
        getAllHistory()
        setBanners()
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility=View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            binding.cardCheckHealth.visibility = View.GONE
            binding.addFab.visibility=View.GONE

            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check internet connectivity",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            binding.clInclude.visibility=View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            binding.cardCheckHealth.visibility = View.VISIBLE
            binding.addFab.visibility=View.VISIBLE
            setBanners()
            getAllHistory()
            getVideos()


        }
    }

    private fun setBanners() {
        binding.clProgressBar.visibility=View.VISIBLE

        val bannerAdapter = AdsAdapter()
        viewModel.getVansAdsList().observe(viewLifecycleOwner) {

            bannerAdapter.submitData(lifecycle, it)
            binding.clProgressBar.visibility=View.GONE

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

    private fun getAllHistory() {
        binding.clProgressBar.visibility=View.VISIBLE

        viewModel.getUserDetails().observe(viewLifecycleOwner) {
            accountID = it.data?.accountId
            if (accountID != null) {
                Log.d(ContentValues.TAG, "onCreateViewAccountID:$$accountID")
                bindObserversSoilTestHistory(accountID!!)

            }
        }
    }


    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
        binding.cardCheckHealth.setOnClickListener {
            viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account
                accountID = it.data?.accountId
                if (accountID != null) {
                    Log.d(ContentValues.TAG, "onCreateViewAccountIDAA:$accountID")
                    isLocationPermissionGranted(accountID!!)
                    binding.cardCheckHealth.isClickable = false

                }
            }
        }
    }

    private fun initViewClick() {
        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_allHistoryFragment)
        }

        binding.cardCheckHealth.setOnClickListener {
            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment)

        }
    }


    private fun getVideos() {
        binding.clProgressBar.visibility = View.VISIBLE

        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        viewModel.getVansVideosList(module_id).observe(requireActivity()) {
       /*     if (adapter.snapshot().size==0){
                videosBinding.noDataVideo.visibility=View.VISIBLE
            }
            else{
                videosBinding.noDataVideo.visibility=View.GONE
                adapter.submitData(lifecycle, it)
                            binding.clProgressBar.visibility = View.GONE

            }*/
            adapter.submitData(lifecycle, it)
        }


        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_soilTestingHomeFragment_to_playVideoFragment2,
                bundle
            )
        }
        videosBinding.viewAllVideos.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            intent.putExtra("module_id",module_id)
            startActivity(intent)
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


    private fun expandableView() {
        binding.clFAQAnsOne.setOnClickListener {
            if (binding.clExpandeble.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.cardData, AutoTransition())
                binding.clExpandeble.visibility = View.VISIBLE
                binding.ivSoil.setBackgroundResource(R.drawable.ic_arrow_up)

            } else {
                TransitionManager.beginDelayedTransition(binding.cardData, AutoTransition())
                binding.clExpandeble.visibility = View.GONE
                binding.ivSoil.setBackgroundResource(R.drawable.ic_down_arrpw)


            }
        }
    }

    private fun expandableViewThree() {
        binding.clFAQAnsThree.setOnClickListener {
            if (binding.clExpandebleThree.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.cardDataThree, AutoTransition())
                binding.clExpandebleThree.visibility = View.VISIBLE
                binding.ivSoilThree.setBackgroundResource(R.drawable.ic_arrow_up)

            } else {
                TransitionManager.beginDelayedTransition(binding.cardDataTwo, AutoTransition())
                binding.clExpandebleThree.visibility = View.GONE
                binding.ivSoilThree.setBackgroundResource(R.drawable.ic_down_arrpw)


            }
        }
    }

    private fun expandableViewTWo() {
        binding.clFAQAnsTwo.setOnClickListener {
            if (binding.clExpandebleTwo.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.cardDataTwo, AutoTransition())
                binding.clExpandebleTwo.visibility = View.VISIBLE
                binding.ivSoilTwo.setBackgroundResource(R.drawable.ic_arrow_up)

            } else {
                TransitionManager.beginDelayedTransition(binding.cardDataTwo, AutoTransition())
                binding.clExpandebleTwo.visibility = View.GONE
                binding.ivSoilTwo.setBackgroundResource(R.drawable.ic_down_arrpw)


            }
        }
    }

    private fun locationClick() {
        binding.cardCheckHealth.setOnClickListener {
//            isLocationPermissionGranted(1)
            viewModel.getUserDetails().observe(viewLifecycleOwner) {
//                    itemClicked(it.data?.data?.id!!, lat!!, long!!, onp_id!!)
//                    account=it.data.account

                accountID = it.data?.accountId
                Log.d(ContentValues.TAG, "onCreateViewAccountIDscsv:$accountID")
                isLocationPermissionGranted(accountID!!)

            }
//            Log.d(TAG, "locationClicklatitude: $latitude")
//            Log.d(TAG, "locationClicklongitude: $longitude")

//            bindObserversCheckSoilTest()
//            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_newSoilTestFormFragment)
//            checkSoilTestViewModel.getSoilTest(1, 60.078100, 60.636580)
//            bindObserversCheckSoilTest()
//            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_newSoilTestFormFragment)
        }

    }


    private fun bindObserversSoilTestHistory(account_id: Int) {
        viewModel.getSoilTestHistory(account_id).observe(requireActivity()) {
            if (it.data!!.isEmpty()) {
                binding.clTopGuide.visibility = View.VISIBLE
                binding.clRequest.visibility = View.GONE
            } else
                when (it) {
                    is Resource.Success -> {
                        binding.clProgressBar.visibility=View.GONE

                        binding.clTopGuide.visibility = View.GONE
                        binding.clRequest.visibility = View.VISIBLE

                        Log.d("TAG", "bindObserversData:" + it.data.toString())
                        if (it.data != null) {
                            val response = it.data as ArrayList<SoilTestHistoryDomain>
                            if (response.size <= 2) {
                                soilHistoryAdapter.setMovieList(response)

                            } else {
                                val arrayList = ArrayList<SoilTestHistoryDomain>()
                                arrayList.add(response[0])
                                arrayList.add(response[1])
                                soilHistoryAdapter.setMovieList(arrayList)

                            }

                        }


                    }
                    is Resource.Error -> {
                        context?.let { it1 -> ToastStateHandling.toastError(it1, "Error", Toast.LENGTH_SHORT) }

                    }
                    is Resource.Loading -> {

                    }
                }

        }
    }


    override fun statusTracker(data: SoilTestHistoryDomain) {
        val bundle = Bundle()
        bundle.putInt("s", data.id!!)
        bundle.putString("soil_test_number", data.soil_test_number)
        findNavController().navigate(
            R.id.action_soilTestingHomeFragment_to_statusTrackerFragment,
            bundle
        )
    }

    private fun isLocationPermissionGranted(account_id: Int): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
            // use your location object
            Log.d("checkLocation", "isLocationPermissionGranted:1 ")
            false
        } else {
            Log.d("checkLocation", "isLocationPermissionGranted:2 ")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && account_id != null) {
                        val latitude = String.format(Locale.ENGLISH, "%.2f", location.latitude)
                        val longitutde = String.format(Locale.ENGLISH, "%.2f", location.longitude)

                        viewModel.getCheckSoilTestLab(
                            account_id,
                            latitude,
                            longitutde
                        ).observe(requireActivity()) {
                            when (it) {
                                is Resource.Success -> {
                                    binding.clProgressBar.visibility = View.GONE
                                    binding.progressBar.isVisible = false
                                    if (it.data!!.isEmpty()) {

                                        CustomeDialogFragment.newInstance().show(
                                            requireActivity().supportFragmentManager,
                                            CustomeDialogFragment.TAG
                                        )
                                        binding.cardCheckHealth.isClickable = true
                                    } else if (it.data!!.isNotEmpty()) {
                                        val response = it.data
                                        Log.d(
                                            TAG,
                                            "bindObserversCheckSoilTestModelFJndsj: $response"
                                        )
                                        var bundle = Bundle().apply {
                                            putParcelableArrayList(
                                                "list",
                                                ArrayList<Parcelable>(response)
                                            )
                                        }

                                        bundle.putString("lat", latitude)
                                        bundle.putString("lon", longitutde)
//                                        bundle.putString("lab_name",it.data)
                                        try {
                                            findNavController().navigate(
                                                R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment,
                                                bundle
                                            )
                                            Log.d("TAGPraveen", "isLocationPermissionGranted: SetPass")
                                        }catch (e:Exception){
                                            Log.d("TAGPraveenAade", "isLocationPermissionGranted: NotPassed $e")
                                        }


                                    }

                                }
                                is Resource.Error -> {
                                    if(NetworkUtil.getConnectivityStatusString(context)==0){
                                        ToastStateHandling.toastError(
                                            requireContext(),
                                            "Please check you internet connectivity",
                                            Toast.LENGTH_SHORT
                                        )
                                    }
                                    else{
                                        ToastStateHandling.toastError(
                                            requireContext(),
                                            "Server Error",
                                            Toast.LENGTH_SHORT
                                        )
                                    }

                                    binding.clProgressBar.visibility = View.GONE

                                    binding.cardCheckHealth.isClickable = true

                                }
                                is Resource.Loading -> {
                                    binding.progressBar.isVisible = true
                                    binding.clProgressBar.visibility = View.VISIBLE
                                    Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                                        .show()

                                }
                            }

                        }


                    }
                }
            true
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
            FeatureChat.zenDeskInit(requireContext())
        }
    }

}