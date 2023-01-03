package com.waycool.featurecrophealth.ui.history

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.AiCropHistoryDomain
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
            findNavController().navigate(R.id.action_cropHealthFragment_to_cropHistoryFragment)
        }
        videosBinding.viewAllVideos.setOnClickListener {
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




        bindObservers()
        getVideos()
        fabButton()

        historyAdapter.onItemClick = {
            if (it?.disease_id==null) {
                ToastStateHandling.toastError(requireContext(), "Please upload quality image", Toast.LENGTH_SHORT)
            } else {
                val bundle = Bundle()
                it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
//            it?.disease_id?.let { it1 -> bundle.putInt("diseaseid", it1) }
                findNavController().navigate(
                    R.id.action_cropHealthFragment_to_pestDiseaseDetailsFragment2,
                    bundle
                )
            }

        }

        binding.backBtn.setOnClickListener() {
            activity?.finish()
        }

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
                    videosBinding.videosListRv.visibility = View.INVISIBLE
                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    videosBinding.noDataVideo.visibility = View.VISIBLE
                                    videosBinding.videoCardNoInternet.visibility = View.GONE
                                    videosBinding.videosListRv.visibility = View.INVISIBLE
                                }

                                if (it1 is LoadState.NotLoading) {

                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                    } else {
                                        videosBinding.noDataVideo.visibility = View.GONE
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

        viewModel.getAiCropHistory().observe(viewLifecycleOwner) {
            if (it.data?.isEmpty() == true) {
                binding.clTopGuide.visibility = View.VISIBLE
                binding.clRequest.visibility = View.GONE
            } else
                when (it) {
                    is Resource.Success -> {
                        binding.takeGuide.visibility = View.GONE
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
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(Contants.CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener(){
            FeatureChat.zenDeskInit(requireContext())
        }
    }

}