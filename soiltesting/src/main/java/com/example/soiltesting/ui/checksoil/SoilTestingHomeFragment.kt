package com.example.soiltesting.ui.checksoil

import android.content.Intent

import android.content.pm.PackageManager
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
import com.example.soiltesting.R
import com.example.soiltesting.databinding.FragmentSoilTestingHomeBinding
import com.example.soiltesting.ui.history.HistoryDataAdapter
import com.example.soiltesting.ui.history.HistoryViewModel
import com.example.soiltesting.ui.history.StatusTrackerListener
import com.example.soiltesting.utils.Constant.TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.waycool.data.repository.domainModels.SoilTestHistoryDomain
import com.waycool.data.utils.Resource
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class SoilTestingHomeFragment : Fragment(), StatusTrackerListener {
    private var _binding: FragmentSoilTestingHomeBinding? = null
    private val binding get() = _binding!!
    private var soilHistoryAdapter = HistoryDataAdapter(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    //    private lateinit var soilHistoryAdapter: SoilHistoryAdapter
    private val viewModel by lazy { ViewModelProvider(this)[HistoryViewModel::class.java] }

    private val checkSoilTestViewModel by lazy { ViewModelProvider(this)[CheckSoilLabViewModel::class.java] }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoilTestingHomeBinding.inflate(inflater, container, false)
        soilHistoryAdapter = HistoryDataAdapter(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerview.adapter = soilHistoryAdapter
        initViewClick()
//        bindObservers()
        locationClick()
        initViewBackClick()
        expandableView()
        expandableViewTWo()
        bindObserversSoilTestHistory()
        getVideos()

    }

    private fun initViewBackClick() {
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }
    }

    private fun initViewClick() {
        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_allHistoryFragment)
        }
//        binding.tvViewAllVideos.setOnClickListener {
//            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_allVideoFragment)
//        }
        binding.cardCheckHealth.setOnClickListener {
            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment)

        }
    }
    private fun getVideos() {

        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        viewModel.getVansVideosList().observe(requireActivity()) {
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
//            Log.d(TAG, "locationClicklatitude: $latitude")
//            Log.d(TAG, "locationClicklongitude: $longitude")
            isLocationPermissionGranted()
//            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_newSoilTestFormFragment)
//            checkSoilTestViewModel.getSoilTest(1, 60.078100, 60.636580)
//            bindObserversCheckSoilTest()
//            findNavController().navigate(R.id.action_soilTestingHomeFragment_to_newSoilTestFormFragment)
        }

    }



//    @SuppressLint("ResourceAsColor")
//    private fun bindObserversCheckSoilTest() {
//        checkSoilTestViewModel.getCheckSoilTestLab().observe(viewLifecycleOwner, Observer { model ->
//            when (model) {
//                is NetworkResult.Success -> {
//                    binding.progressBar.isVisible = false
//                    binding.clProgressBar.visibility = View.GONE
//                    Log.d("TAG", "bindObserversDataCheckSoilData:" + model.data.toString())
//                    if (model.data?.data!!.isNullOrEmpty()) {
////                        binding.clProgressBar.visibility = View.VISIBLE
////                        binding.constraintLayout.setBackgroundColor(R.color.background_dialog)
//
//                        findNavController().navigate(R.id.action_soilTestingHomeFragment_to_customeDialogFragment)
//                    } else if (model.data.data.isNotEmpty()) {
//                        val response = model.data.data
//                        var bundle = Bundle().apply {
//                            putParcelableArrayList("list", ArrayList<Parcelable>(response))
//                        }
//                        Log.d(TAG, "bindObserversCheckSoilTestModel: ${model.data.data.toString()}")
//                        findNavController().navigate(
//                            R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment,
//                            bundle
//                        )
//                    }
//                }
//                is NetworkResult.Error -> {
//                    Toast.makeText(requireContext(), model.message.toString(), Toast.LENGTH_SHORT)
//                        .show()
//                    binding.progressBar.isVisible = false
//                    binding.clProgressBar.visibility = View.GONE
//                }
//                is NetworkResult.Loading -> {
//                    binding.clProgressBar.visibility = View.VISIBLE
//                    binding.progressBar.isVisible = true
//
//                }
//            }
//
//        })
//
//    }

    private fun bindObserversSoilTestHistory() {
        viewModel.getSoilTestHistory().observe(requireActivity()) {
            if (it.data!!.isEmpty()) {
                binding.clTopGuide.visibility = View.VISIBLE
            } else
                when (it) {
                    is Resource.Success -> {
                        binding.clTopGuide.visibility = View.GONE
                        Log.d("TAG", "bindObserversData:" + it.data.toString())
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
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()

                    }
                }

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun statusTracker(data: SoilTestHistoryDomain) {
        val bundle = Bundle()
        bundle.putInt("id", data.id!!)
        bundle.putString("soil_test_number", data.soil_test_number)
        Log.d(TAG, "statusTrackerIDPass: ${data.id}")
        findNavController().navigate(
            R.id.action_soilTestingHomeFragment_to_statusTrackerFragment,
            bundle
        )
    }

    private fun isLocationPermissionGranted(): Boolean {
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
            Log.d("checkLocation", "isLocationPermissionGranted:1 ")
            false
        } else {
            Log.d("checkLocation", "isLocationPermissionGranted:2 ")
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // use your location object
                        // get latitude , longitude and other info from this
                        Log.d("checkLocation", "isLocationPermissionGranted: $location")
//                        getAddress(location.latitude, location.longitude)
                        Log.d(TAG, "isLocationPermissionGrantedLotudetude: ${location.latitude}")
                        Log.d(TAG, "isLocationPermissionGrantedLotudetude: ${location.longitude}")

//                        checkSoilTestViewModel.getSoilTest(1, location.latitude, location.longitude)
//                        bindObserversCheckSoilTest()
                        checkSoilTestViewModel.getCheckSoilTestLab(
                            location.latitude,
                            location.longitude
                        ).observe(requireActivity()) {
                            when (it) {
                                is Resource.Success -> {
                                    binding.progressBar.isVisible = false
                                    binding.clProgressBar.visibility = View.GONE
                                    Log.d(
                                        "TAG",
                                        "bindObserversDataCheckSoilData:" + it.data.toString()
                                    )
                                    if (it.data!!.isNullOrEmpty()) {
//                        binding.clProgressBar.visibility = View.VISIBLE
//                        binding.constraintLayout.setBackgroundColor(R.color.background_dialog)
                                        findNavController().navigate(R.id.action_soilTestingHomeFragment_to_customeDialogFragment)
                                    } else if (it.data!!.isNotEmpty()) {
                                        val response = it.data
                                        Log.d(
                                            TAG,
                                            "bindObserversCheckSoilTestModelFJndsj: $response")
                                        var bundle = Bundle().apply {
                                            putParcelableArrayList("list", ArrayList<Parcelable>(response))
                                        }

                                        findNavController().navigate(
                                            R.id.action_soilTestingHomeFragment_to_checkSoilTestFragment,
                                            bundle
                                        )
                                    }

                                }
                                is Resource.Error -> {
                                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT)
                                        .show()

                                }
                                is Resource.Loading -> {
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


}