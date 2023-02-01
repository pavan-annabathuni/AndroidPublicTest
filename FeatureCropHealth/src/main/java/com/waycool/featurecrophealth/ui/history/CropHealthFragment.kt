package com.waycool.featurecrophealth.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurechat.Contants
import com.waycool.featurechat.FeatureChat
import com.waycool.featurecrophealth.CropHealthViewModel
import com.waycool.featurecrophealth.R
import com.waycool.featurecrophealth.databinding.FragmentCropHealthBinding
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class CropHealthFragment : Fragment() {

   private lateinit var binding: FragmentCropHealthBinding
//    private val binding get() = binding!!

    private lateinit var videosBinding: GenericLayoutVideosListBinding
 

    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    private var module_id = "3"

    private lateinit var historyAdapter: AiCropHistoryAdapter
    private val viewModel by lazy { ViewModelProvider(this)[CropHealthViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCropHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initView()
        binding.recyclerview.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        videosBinding = binding.layoutVideos

        apiErrorHandlingBinding = binding.errorState
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }
        binding.tvViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("Crophealth_requesthistory_viewall")

            findNavController().navigate(R.id.action_cropHealthFragment_to_cropHistoryFragment)
        }
        videosBinding.viewAllVideos.setOnClickListener {
            EventClickHandling.calculateClickEvent("video_viewall")
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }
        videosBinding.ivViewAll.setOnClickListener {
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }
        historyAdapter = AiCropHistoryAdapter(requireContext())
        binding.recyclerview.adapter = historyAdapter
        binding.cardCheckHealth.setOnClickListener {
            if(NetworkUtil.getConnectivityStatusString(context)==0){
                ToastStateHandling.toastError(
                    requireContext(),
                    "Please check you internet connectivity",
                    Toast.LENGTH_SHORT
                )            }else{
                findNavController().navigate(R.id.action_cropHealthFragment_to_cropSelectFragment)

            }
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()

//                    val isSuccess = activity?.let { findNavController().popBackStack() }
//                    if (!isSuccess) activity?.let { NavUtils.navigateUpFromSameTask(it) }
                }
            }
        activity?.let {
            activity?.onBackPressedDispatcher?.addCallback(
                it,
                callback
            )
        }




        bindObservers()
        getVideos()
        fabButton()
        binding.tvCheckCrop.isSelected = true
        historyAdapter.onItemClick = {
            if (it?.disease_id==null) {
                ToastStateHandling.toastError(requireContext(), "Please upload quality image", Toast.LENGTH_SHORT)
            } else {
                val eventBundle=Bundle()
                eventBundle.putString("cropname",it.cropName)
                eventBundle.putString("diseasename",it.disease_name)
                eventBundle.putString("image",it.image_url)
                EventItemClickHandling.calculateItemClickEvent("crophealth_requestHistory",eventBundle)
                val bundle = Bundle()
                it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
//            it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
                findNavController().navigate(
                    R.id.action_cropHealthFragment_to_navigation_pest_and_disease_details,
                    bundle
                )
            }

        }
        translationSoilTesting()
        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }


    }
    fun translationSoilTesting() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("crop_health")
            binding.tvToolBar.text = title
        }
        TranslationsManager().loadString("videos_not_available",videosBinding.tvNoVANs,"Videos are not available with us.")


        TranslationsManager().loadString("crop_protect_info", binding.tvOurAll,"Our ‘Pest & Disease Detection’ service helps in detecting pests & diseases and recommends control measures using Artificial Intelligence.")
        TranslationsManager().loadString("take_picture", binding.tvTakeImage,"Take a Picture")
        TranslationsManager().loadString("get_diagnosed", binding.tvTextSoilTwo,"Get Diagnosed")
        TranslationsManager().loadString("get_measures", binding.tvTextSoilThree,"Get Preventive\n" +
                "Measures")
        TranslationsManager().loadString("request_history", binding.tvRequest,"Request History")
        TranslationsManager().loadString("check_crop_health", binding.tvCheckCrop,"Check your Crop health")
        TranslationsManager().loadString("str_viewall", binding.tvViewAll,"View all")
        TranslationsManager().loadString("videos", videosBinding.videosTitle,"Videos")
        TranslationsManager().loadString("str_viewall", videosBinding.viewAllVideos,"View all")

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
            getVideos()
            bindObservers()

        }
    }

    private fun getVideos() {
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getVansVideosList(module_id).collect {
                adapter.submitData(lifecycle, it)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    videosBinding.videoCardNoInternet.visibility = View.VISIBLE
                    videosBinding.noDataVideo.visibility = View.GONE
                    videosBinding.viewAllVideos.visibility = View.GONE
                    videosBinding.ivViewAll.visibility = View.GONE

                    videosBinding.videosListRv.visibility = View.INVISIBLE
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error ) {
                                    if(adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.viewAllVideos.visibility = View.GONE
                                        videosBinding.ivViewAll.visibility = View.GONE
                                        videosBinding.tvNoVANs.text = "Videos are being loaded.Please wait for some time"
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                    }
                                }

                                if (it1 is LoadState.NotLoading) {
                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.viewAllVideos.visibility = View.GONE
                                        videosBinding.ivViewAll.visibility = View.GONE

                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                    } else {
                                        videosBinding.noDataVideo.visibility = View.GONE
                                        videosBinding.viewAllVideos.visibility = View.VISIBLE
                                        videosBinding.ivViewAll.visibility = View.VISIBLE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.VISIBLE

                                    }
                                }
                            }
                    }


                }

            }
        }

/*
        viewModel.getVansVideosList(module_id).observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
            binding.clProgressBar.visibility=View.GONE

            */
/*       if (adapter.snapshot().size==0){
                       videosBinding.noDataVideo.visibility=View.VISIBLE
                       binding.clProgressBar.visibility=View.GONE

                   }
                   else{
                       videosBinding.noDataVideo.visibility=View.GONE
                       adapter.submitData(lifecycle, it)
                       binding.clProgressBar.visibility=View.GONE

                   }*//*


        }
*/

        adapter.onItemClick = {
            val eventBundle=Bundle()
            eventBundle.putString("title",it?.title)
            EventItemClickHandling.calculateItemClickEvent("crophealth_video",eventBundle)
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_cropHealthFragment_to_playVideoFragment3,
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
//    private fun initView() {
//        binding.recyclerview.layoutManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
//
//    }

    private fun bindObservers() {

        viewModel.getAiCropHistoryFromLocal().observe(viewLifecycleOwner) {
            if (it.data?.isEmpty() == true) {
                binding.clTopGuide.visibility = View.VISIBLE
                binding.clRequest.visibility = View.GONE
            } else
                when (it) {
                    is Resource.Success -> {
                        binding.clTopGuide.visibility = View.GONE
                        binding.clProgressBar.visibility=View.GONE

//                        Log.d(TAG, "bindObserversData:" + model.data.toString())
//                        historyAdapter.submitList(it.data)

                        val response = it.data
                        if (response?.size!! <=2) {
                            historyAdapter.submitList(response)
//                            historyAdapter.submitList(response)

                        }
                        else{
                            val arrayList = ArrayList<AiCropHistoryDomain>()
                            arrayList.add(response[0])
                            arrayList.add(response[1])
                            historyAdapter.submitList(arrayList)

                        }
//                        else if (response.size==2){
//                            historyAdapter = NoteAdapter(2)
//                            historyAdapter.submitList(response)
//
//                        }
//                        else{
//                            historyAdapter= NoteAdapter(2)
//                            historyAdapter.submitList(response)
//
//                        }
                    }
                    is Resource.Error -> {
                        ToastStateHandling.toastError(
                            requireContext(),
                            it.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                    }
                    is Resource.Loading -> {

                    }
                }
        }

    }


//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
    private fun fabButton(){
        var isVisible = false
        binding.addFab.setOnClickListener(){
            if(!isVisible){
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            }else{
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener(){
            EventClickHandling.calculateClickEvent("call_icon")
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener(){
            EventClickHandling.calculateClickEvent("chat_icon")
            FeatureChat.zenDeskInit(requireContext())
        }
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropHealthFragment")
    }
}