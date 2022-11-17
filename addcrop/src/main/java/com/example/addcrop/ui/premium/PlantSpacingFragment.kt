package com.example.addcrop.ui.premium

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.databinding.FragmentPlantSpacingBinding

import com.example.addcrop.viewmodel.AddViewModel
import com.waycool.data.utils.Resource

class PlantSpacingFragment : Fragment() {
    private var _binding: FragmentPlantSpacingBinding? = null
    private val binding get() = _binding!!
    var area: String? = null
    var date: String? = null
    var account_id: Int? = null
    var crop_id: Int? = null
    var crop_type: Int? = null
    var irrigation_selected: String? = null
    var noOFPlants: String? = null
    var plantToPlant:String=""
    var planetBed:String=""
    var dripEmitterRate:String=""

    private val viewModel by lazy { ViewModelProvider(this)[AddViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantSpacingBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bundle = Bundle()
        if (arguments != null) {
            account_id = arguments?.getInt("account_id")
            crop_id = arguments?.getInt("cropid")
            crop_type = arguments?.getInt("crop_type")
            area = arguments?.getString("area")
            date = arguments?.getString("date")
            irrigation_selected = arguments?.getString("irrigation_selected")
            noOFPlants = arguments?.getString("numberOfPlanets")

            Log.d("TAG", "onCreateViewGetData: $account_id")
            Log.d("TAG", "onCreateViewGetData: $crop_id")
            Log.d("TAG", "onCreateViewGetData: $crop_type")
            Log.d("TAG", "onCreateViewGetData: $area")
            Log.d("TAG", "onCreateViewGetData: $date")
            Log.d("TAG", "onCreateViewGetData: $irrigation_selected")
            Log.d("TAG", "onCreateViewGetData: $noOFPlants")

            val map = mutableMapOf<String, Any>()
            account_id?.let { map.put("account_no_id", it) }
            crop_id?.let { map.put("crop_id", it) }
            crop_type?.let { map.put("soil_type_id", it) }
//            map.put("plot_nickname", )
            area?.let { map.put("area", it) }
            irrigation_selected?.let { map.put("irrigation_type", it) }
            date?.let { map.put("sowing_date", it) }
            noOFPlants?.let { map.put("no_of_plants", it) }
//            var length=binding.et
            map.put("len_drip", binding.etNumber.text)
            map.put("width_drip", binding.etNumberWidth.text)
            map.put("drip_emitter_rate", binding.etNumberWidthDistance.text)

            binding.cardCheckHealth.setOnClickListener {
                plantToPlant = binding.etNumber.text.toString().trim()
                planetBed = binding.etNumberWidth.text.toString().trim()
                dripEmitterRate = binding.etNumberWidthDistance.text.toString().trim()
                if (plantToPlant.isEmpty()){
                    binding.etNumber.error = "Enter Plant to Plant Distance"
                }else if (planetBed.isEmpty()){
                    binding.etNumberWidth.error="Plant Bed width"
                }else if (dripEmitterRate.isEmpty()){
                    binding.etNumberWidthDistance.error="Plant to Plant Distance"
                }else if (plantToPlant.isNotEmpty() && planetBed.isNotEmpty() && dripEmitterRate.isNotEmpty()){
                    viewModel.addCropDataPass(map).observe(requireActivity()) {
                        when (it) {
                            is Resource.Success -> {
                                activity?.finish()
                            }
                            is Resource.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d(
                                    ContentValues.TAG,
                                    "postAddCropExption: ${it.message.toString()}"
                                )
                            }
                            is Resource.Loading -> {
                                Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                                    .show()

                            }
                        }
                    }
                }

            }


        }
        binding.backBtn.setOnClickListener {
            val isSuccess = findNavController().navigateUp()
            if (!isSuccess) requireActivity().onBackPressed()
        }


    }

    private fun passDataDripIrrigation(map: String) {
        val map = mutableMapOf<String, Any>()
        map["map"] = map
        Log.d("TAG", "passDataDripIrrigation: $map")


    }


}