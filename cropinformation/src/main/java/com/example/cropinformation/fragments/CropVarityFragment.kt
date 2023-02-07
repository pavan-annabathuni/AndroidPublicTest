package com.example.cropinformation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.adapter.CropVarietyAdapter
import com.example.cropinformation.databinding.FragmentCropVarityBinding
import com.example.cropinformation.viewModle.CropInfoViewModel
import com.google.gson.JsonParseException
import com.waycool.data.Local.utils.TypeConverter
import com.waycool.data.eventscreentime.EventScreenTimeHandling


class CropVarityFragment : Fragment() {
    private var cropId: Int? = null
    private var cropName: String? = null
    private lateinit var binding: FragmentCropVarityBinding
    private val ViewModel: CropInfoViewModel by lazy {
        ViewModelProviders.of(this).get(CropInfoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cropId = it.getInt("CropId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCropVarityBinding.inflate(inflater)
       // ViewModel.cropAdvisory()
        observer()
        Log.d("CropID", "onCreateView: $cropId")
        return binding.root

    }

    private fun observer() {
//       if(cropId!=1){
        ViewModel.getCropInformationDetails(cropId!!).observe(requireActivity()){
            val data = it.data
            for(i in 0 until it.data!!.size){
                if(it.data!![i].label_name=="Crop Variety"|| it.data!!.first().labelNameTag=="Crop Variety") {
                    binding.labelName.text = data?.get(i)!!.label_name

                    val varietyList =
                        try {
                            TypeConverter.convertStringToCropVariety(data[i].label_value!!)
                        } catch (jsonException: JsonParseException) {
                            data[i].labelValueTag?.let { it1 ->
                                TypeConverter.convertStringToCropVariety(
                                    it1
                                )
                            }
                        }
//                    if (varietyList.isNullOrEmpty())
//                        varietyList =
//                            TypeConverter.convertStringToCropVariety(data[i].labelValueTag!!)

                    if(varietyList!=null) {
                        val adapter = CropVarietyAdapter()
                        binding.rvCropVariety.adapter=adapter
                        adapter.submitList(varietyList)
                    }
                    break
}
                    }
                }

        }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("CropVarityFragment")
    }
}

