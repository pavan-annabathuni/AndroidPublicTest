package com.waycool.weather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.waycool.weather.databinding.FragmentSheetHourlyBinding
import com.waycool.weather.viewModel.HourlyViewModel
import com.waycool.weather.viewModel.HourlyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import com.waycool.weather.utils.WeatherIcons
import java.text.SimpleDateFormat


class SheetHourlyFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSheetHourlyBinding
    lateinit var today:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSheetHourlyBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val imageProperty = SheetHourlyFragmentArgs.fromBundle(requireArguments()).hourly

        val viewModelFactory = HourlyViewModelFactory(imageProperty,application)

        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(HourlyViewModel::class.java)

        observer()
        onClick()
        translation()
        return binding.root
    }

       fun observer(){

           binding.viewModel?.selectedProperty?.observe(viewLifecycleOwner){

               WeatherIcons.setWeatherIcon(it.weather[0].icon.toString(),binding.icon2)
               val date: Int? = it.dt
               val formatter = SimpleDateFormat("hh:mm a")//or use getDateInstance()
               val formatedDate = formatter.format(date?.times(1000L))
               binding.date.text = formatedDate+" Today"
           }

       }

    fun onClick(){
        binding.imgClose.setOnClickListener(){
            this.dismiss()
        }
    }
    override fun getTheme(): Int {
        return com.waycool.weather.R.style.BottomSheetDialog
    }
    fun translation() {
        TranslationsManager().loadString("str_hourly_weather", binding.textView2,"Hourly Weather")
//       today = TranslationsManager().loadString("str_today", binding.imgShare).toString()
        TranslationsManager().loadString("str_humidity", binding.labelHumidity,"Humidity")
        TranslationsManager().loadString("str_rain", binding.labelRain,"Chance of Rain")
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("SheetHopurlyFragment")
    }
}