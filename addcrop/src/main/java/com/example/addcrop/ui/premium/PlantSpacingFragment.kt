package com.example.addcrop.ui.premium

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.addcrop.databinding.FragmentPlantSpacingBinding
import com.example.addcrop.viewmodel.AddCropViewModel
import com.waycool.data.eventscreentime.EventScreenTimeHandling
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
        translationForPlantSpacingDripIrrigation()
        viewModel.getUserDetails().observe(viewLifecycleOwner) { userDetails ->
            val accountID = userDetails.data?.accountId
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
                map["drip_emitter_rate"] = binding.etNumberWidthDistance.text
                map["area_type"] = acrea_type.toString().lowercase()
                binding.constraintLayout3.setOnSelectListener {selectPlantDistance->
                    try {
                        if (selectPlantDistance.text.trim().isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please Enter Plant to Plant Distance",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (selectPlantDistance.text == "ft") {
                                map["len_drip"] = binding.etNumber.text.toString().toInt() * 0.305
                            } else if (selectPlantDistance.text == "cm") {
                                map["len_drip"] = binding.etNumber.text.toString().toInt() * 0.01
                            } else if ((selectPlantDistance.text == "mtr")) {
                                map["len_drip"] = binding.etNumber.text
                            } else {
                                map["len_drip"] = binding.etNumber.text
                            }
                        }

                    } catch (e: NumberFormatException) {

                    }

                }

                binding.constraintLayoutBedWidth.setOnSelectListener {selectBedWidth->
                    try {
                        if (selectBedWidth.text.trim().isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please Enter Plant Bed width",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (selectBedWidth.text == "ft") {
                                map["width_drip"] = binding.etNumber.text.toString().toInt() * 0.305
                            } else if (selectBedWidth.text == "cm") {
                                map["width_drip"] = binding.etNumber.text.toString().toInt() * 0.01
                            } else if (selectBedWidth.text == "mtr") {
                                map["width_drip"] = binding.etNumber.text
                            } else {
                                map["width_drip"] = binding.etNumber.text
                            }

                        }
                    } catch (e: NumberFormatException) {

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
                        binding.progressBar?.visibility = View.VISIBLE
                        binding.cardCheckHealth.visibility = View.GONE
                        viewModel.addCropDataPass(map).observe(requireActivity()) { addCropDrip->
                            when (addCropDrip) {
                                is Resource.Success -> {
                                    binding.progressBar?.visibility = View.INVISIBLE
                                    binding.cardCheckHealth.visibility = View.VISIBLE
                                    activity?.finish()
                                }
                                is Resource.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        addCropDrip.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d(
                                        ContentValues.TAG,
                                        "postAddCropExption: ${addCropDrip.message.toString()}"
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

    private fun translationForPlantSpacingDripIrrigation() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.title?.text= TranslationsManager().getString("add_crop")

            var NickNamehint = TranslationsManager().getString("e_g_50")
            binding.etNumber.hint = NickNamehint
            binding.etNumberWidth.hint = NickNamehint
            binding.etNumberWidthDistance.hint = NickNamehint
        }
        TranslationsManager().loadString("plant_spacing_details", binding.plot)
        TranslationsManager().loadString("plant_to_plant_distance", binding.plantDistnce)
        TranslationsManager().loadString("plant_bed_width", binding.plantDistnceWidth)
        TranslationsManager().loadString(
            "drip_emitter_rate_per_plant",
            binding.plantDistnceWidthDistance
        )
        TranslationsManager().loadString("save", binding.tvCheckCrop)
    }

    private fun passDataDripIrrigation(map: String) {
        val map = mutableMapOf<String, Any>()
        map["map"] = map
        Log.d("TAG", "passDataDripIrrigation: $map")

    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("PlantSpacingFragment")
    }
}