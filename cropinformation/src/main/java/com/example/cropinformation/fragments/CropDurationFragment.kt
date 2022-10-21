package com.example.cropinformation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.cropinformation.R
import com.example.cropinformation.databinding.FragmentCropDurationBinding
import com.example.cropinformation.utils.Constants.Companion.CROP_ID
import com.example.cropinformation.viewModle.TabViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CropDurationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CropDurationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding:FragmentCropDurationBinding
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
        binding = FragmentCropDurationBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModel
        //ViewModel.cropAdvisory()
//        ViewModel.response3.observe(viewLifecycleOwner){
//            val data = it.filter { it -> it.crop_id==CROP_ID}
//            for(i in 0..data.size){
//                if(data[i].label_name=="Crop Duration") {
//                    binding.tvDays.text = data[i].label_value
//                    binding.tvDays2.text = data[i].label_value
//                }
//        }}
        ViewModel.getCropInformationDetails(cropId!!).observe(requireActivity()){
           // val data = it.filter { it -> it.crop_id==CROP_ID}
            val data = it.data
            for(i in 0 until data!!.size){
                if(data!![i].label_name=="Crop Duration") {
                    binding.tvDays.text = data[i].label_value
                    binding.tvDays2.text = data[i].label_value
                }
            }
        }
        return binding.root
    }
}