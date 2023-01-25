package com.example.cropinformation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentNurseryBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import org.json.JSONArray

class NurseryFragment : Fragment() {
    private lateinit var binding: FragmentNurseryBinding
    private val ViewModel: TabViewModel by lazy {
        ViewModelProviders.of(this).get(TabViewModel::class.java)
    }
    private var cropId: Int? = null
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
        binding = FragmentNurseryBinding.inflate(inflater)
        //ViewModel.cropAdvisory()
        observer()
        translation()
        return binding.root
    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!
            for(i in data.indices){
                if(data[i].label_name=="Nursery Practices"||data[i].labelNameTag=="Nursery Practices") {

                    try {
                        var jsonData: String = data[i].labelValueTag!!
                        val jsonArray = JSONArray(jsonData)
                        for (j in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(j)
                            when (j) {
                                0 -> {
                                    binding.Germination.text = jsonObject.getString("title")
                                    binding.GerminationTime.text = jsonObject.getString("value")
                                }
                                1 -> {
                                    binding.tv2.text = jsonObject.getString("title")
                                    binding.tvTime2.text = jsonObject.getString("value")
                                }
                                2 -> {
                                    binding.tv3.text = jsonObject.getString("title")
                                    binding.tvTime3.text = jsonObject.getString("value")
                                }
                                3 -> {
                                    binding.tv4.text = jsonObject.getString("title")
                                    binding.tvTime4.text = jsonObject.getString("value")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("Nursery", "observer: $e")
                    }
                }}}}
    private fun translation(){
        TranslationsManager().loadString("str_germination_period",binding.Germination)
        TranslationsManager().loadString("str_nursery_period",binding.tv2)
        TranslationsManager().loadString("hardening_period",binding.tv3)
        TranslationsManager().loadString("str_transplanting",binding.tv4)
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NurseryFragment")
    }
}