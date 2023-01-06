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
import com.example.addcrop.R
import com.example.addcrop.databinding.FragmentPlantSpacingBinding

import com.example.addcrop.viewmodel.AddCropViewModel
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantSpacingFragment : Fragment() {
    private var _binding: FragmentPlantSpacingBinding? = null
    private val binding get() = _binding!!
    var area: String? = null
    var date: String? = null
    var account_id: Int? = null
    var crop_id: Int? = null
    var nickname: String? = null
    var crop_type: Int? = null
    var irrigation_selected: String? = null
    var noOFPlants: String? = null
    var farm_id: Int? = null
    var acrea_type: String? = null
    var plantToPlant: String = ""
    var planetBed: String = ""
    var dripEmitterRate: String = ""

    private val viewModel by lazy { ViewModelProvider(this)[AddCropViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantSpacingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        translationSoilTesting()
        val bundle = Bundle()
        viewModel.getUserDetails().observe(viewLifecycleOwner) { it ->
            val accountID = it.data?.accountId
            if (arguments != null) {
                nickname = arguments?.getString("nick_name")
                account_id = arguments?.getInt("account_id")
                crop_id = arguments?.getInt("cropid")
                crop_type = arguments?.getInt("crop_type")
                area = arguments?.getString("area")
                date = arguments?.getString("date")
                irrigation_selected = arguments?.getString("irrigation_selected")
                noOFPlants = arguments?.getString("numberOfPlanets")
                farm_id = arguments?.getInt("farm_id")
                acrea_type = arguments?.getString("area_type")


                Log.d("TAG", "onCreateViewGetData: $account_id")
                Log.d("TAG", "onCreateViewGetData: $crop_id")
                Log.d("TAG", "onCreateViewGetData: $crop_type")
                Log.d("TAG", "onCreateViewGetData: $area")
                Log.d("TAG", "onCreateViewGetData: $date")
                Log.d("TAG", "onCreateViewGetData: $irrigation_selected")
                Log.d("TAG", "onCreateViewGetData: $noOFPlants")

                val map = mutableMapOf<String, Any>()
                account_id?.let {
                    if (accountID != null) {
                        map["account_no_id"] = accountID
                    }
                }

                if (farm_id != null && farm_id != 0) {
                    farm_id?.let { it1 ->
                        map["farm_id"] = it1
                        Log.d("TAG", "onViewCreated: $farm_id")
                    }
                }
                crop_id?.let { map.put("crop_id", it) }
                crop_type?.let { map.put("soil_type_id", it) }
                nickname?.let { map.put("plot_nickname", it) }
                area?.let { map.put("area", it) }
                irrigation_selected?.let { map.put("irrigation_type", it) }
                date?.let { map.put("sowing_date", it) }
                noOFPlants?.let { map.put("no_of_plants", it) }
//            var length=binding.et
                map["drip_emitter_rate"] = binding.etNumberWidthDistance.text
                map["area_type"] = acrea_type.toString().lowercase()
//                map["len_drip"] = binding.etNumber.text
//                map["width_drip"] = binding.etNumberWidth.text
                binding.constraintLayout3.setOnSelectListener {
                    Log.d("TAG", "onViewCreatedBtnselect: $it")
                    Toast.makeText(requireContext(), it.text, Toast.LENGTH_SHORT).show()
                    if (it.text == "ft") {
                        map["len_drip"] = binding.etNumber.text.toString().toInt() * 0.305
                    } else if (it.text == "cm") {
                        map["len_drip"] = binding.etNumber.text.toString().toInt() * 0.01
                    } else if ((it.text == "mtr")) {
                        map["len_drip"] = binding.etNumber.text
                    }else {
                        map["len_drip"] = binding.etNumber.text
                    }
                }
                binding.constraintLayoutBedWidth.setOnSelectListener {
                    if (it.text == "ft") {
                        map["width_drip"] = binding.etNumber.text.toString().toInt() * 0.305
                    } else if (it.text == "cm") {
                        map["width_drip"] = binding.etNumber.text.toString().toInt() * 0.01
                    } else if (it.text == "mtr") {
                        map["width_drip"] = binding.etNumber.text
                    }else{
                        map["width_drip"] = binding.etNumber.text
                    }
                }
                binding.cardCheckHealth.setOnClickListener {
                    plantToPlant = binding.etNumber.text.toString().trim()
                    planetBed = binding.etNumberWidth.text.toString().trim()
                    dripEmitterRate = binding.etNumberWidthDistance.text.toString().trim()
                    if (plantToPlant.isEmpty()) {
                        binding.etNumber.error = "Enter Plant to Plant Distance"
                    } else if (planetBed.isEmpty()) {
                        binding.etNumberWidth.error = "Plant Bed width"
                    } else if (dripEmitterRate.isEmpty()) {
                        binding.etNumberWidthDistance.error = "Drip Emitter Rate Per Plant"
                    } else if (plantToPlant.isNotEmpty() && planetBed.isNotEmpty() && dripEmitterRate.isNotEmpty()) {
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
            binding.toolbar.setOnClickListener {
                val isSuccess = findNavController().navigateUp()
                if (!isSuccess) requireActivity().onBackPressed()
            }


        }
    }
    fun translationSoilTesting() {
        CoroutineScope(Dispatchers.Main).launch {
            val title = TranslationsManager().getString("add_crop")
            binding.toolbarTitle.text = title
            var NickNamehint = TranslationsManager().getString("e_g_crop_name")
            binding.etNumber.hint =NickNamehint
            binding.etNumberWidth.hint=NickNamehint
            binding.etNumberWidthDistance.hint=NickNamehint
        }
        TranslationsManager().loadString("plant_spacing_details", binding.plot)
        TranslationsManager().loadString("plant_to_plant_distance", binding.plantDistnce)
        TranslationsManager().loadString("plant_bed_width", binding.plantDistnceWidth)
        TranslationsManager().loadString("drip_emitter_rate_per_plant", binding.plantDistnceWidthDistance)
        TranslationsManager().loadString("save", binding.tvCheckCrop)
    }

    private fun passDataDripIrrigation(map: String) {
        val map = mutableMapOf<String, Any>()
        map["map"] = map
        Log.d("TAG", "passDataDripIrrigation: $map")

    }


}