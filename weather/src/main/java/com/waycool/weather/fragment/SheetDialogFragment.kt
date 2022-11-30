package com.waycool.weather.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.waycool.weather.viewModel.DailyvViewModel
import com.waycool.weather.viewModel.DetailViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.weather.R
import com.waycool.weather.databinding.FragmentSheetDialogBinding
import java.text.SimpleDateFormat


class SheetDialogFragment : BottomSheetDialogFragment() {
      private lateinit var binding: FragmentSheetDialogBinding
    val yellow = "#070D09"
    val lightYellow = "#FFFAF0"
    val red = "#FF2C23"
    val lightRed = "#FFD7D0"
    val green = "#146133"
    val lightGreen = "#DEE9E2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           // BottomSheetDialog(requireContext().applicationContext,R.style.MyTransparentBottomSheetDialogTheme);
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSheetDialogBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val imageProperty = SheetDialogFragmentArgs.fromBundle(requireArguments()).daily

        val viewModelFactory = DetailViewModelFactory(imageProperty,application)

        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(DailyvViewModel::class.java)

        observer()
        onClick()
        return binding.root
    }
    fun observer(){
        binding.viewModel?.selectedProperty?.observe(viewLifecycleOwner){

                val date: Int? = it?.dt
                val formatter = SimpleDateFormat("EE, d MMMM")//or use getDateInstance()
                val formatedDate = formatter.format(date?.times(1000L))
                binding.date.text = formatedDate.toString()

            val sunrise: Int? = it?.sunrise
            val formatter2 = SimpleDateFormat("HH:mm a")//or use getDateInstance()
            val formatDate2 = formatter2.format(sunrise?.times(1000L))
            binding.tvSunrise.text = formatDate2.toString()

            val sunset: Int? = it?.sunset
            val formatter3 = SimpleDateFormat("KK:mm a")//or use getDateInstance()
            val formatDate3 = formatter3.format(sunset?.times(1000L))
            binding.tvSunset.text = formatDate3.toString()

            when(it?.weather?.get(0)?.id){
                200-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                201-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                202-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                210-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                211-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                212-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                221-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                230-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                231-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                232-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                300-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                301-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                302-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                310-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                       R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                311-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                312-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                313-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                314-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                321-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                500-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                501-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                502-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                503-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                504-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                511-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                520-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                521-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                522-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                531-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(red))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightRed))
                }
                701-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                711-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                721-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                731-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                741-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                751-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                       R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                761-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                       R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                800-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(green))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightGreen))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation,0,0,0)
                }
                801-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(green))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightGreen))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                       R.drawable.ic_circle_exclamation,0,0,0)
                }
                802-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(green))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightGreen))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation,0,0,0)
                }
                803-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                804-> {binding.alerts.text = it?.weather?.get(0)?.description
                    binding.alerts.setTextColor(Color.parseColor(yellow))
                    binding.cv.setCardBackgroundColor(Color.parseColor(lightYellow))
                    binding.alerts.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }

            }

        }

    }
    private fun onClick(){
        binding.imgClose.setOnClickListener(){
            this.dismiss()
        }

    }
    override fun getTheme(): Int {
        return R.style.BottomSheetDialog
    }
}