package com.example.cropinformation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.databinding.FragmentCropVarityBinding
import com.example.cropinformation.databinding.FragmentNurseryBinding
import com.example.cropinformation.utils.Constants
import com.example.cropinformation.viewModle.TabViewModel
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
        return binding.root

    }

    private fun observer() {
        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner){
            val data = it.data!!

            for(i in 0..data.size-1){
                if(data[i].label_name=="Nursery Practices"||data[i].labelNameTag=="Nursery Practices") {

                    var jsonData: String = data[i].labelValueTag!!
                    val jsonArray = JSONArray(jsonData)
                    binding.Germination.text = jsonArray.getJSONObject(0).get("title").toString()
                    binding.GerminationTime.text = jsonArray.getJSONObject(0).get("value").toString()
                    binding.tv2.text = jsonArray.getJSONObject(1).get("title").toString()
                    binding.tvTime2.text = jsonArray.getJSONObject(1).get("value").toString()
                    binding.tv3.text = jsonArray.getJSONObject(2).get("title").toString()
                    binding.tvTime3.text = jsonArray.getJSONObject(2).get("value").toString()
                    binding.tv4.text = jsonArray.getJSONObject(3).get("title").toString()
                    binding.tvTime4.text = jsonArray.getJSONObject(3).get("value").toString()
                }}}}}