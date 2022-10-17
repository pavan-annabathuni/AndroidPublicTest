package com.example.weather.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.weather.R
import com.example.weather.adapters.HourlyAdapter
import com.example.weather.adapters.WeatherAdapter
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.utils.Constants.*
import com.example.weather.viewModel.WeatherViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {
    private lateinit var binding:FragmentWeatherBinding
    private lateinit var shareLayout: LinearLayout
    val yellow = "#070D09"
    val lightYellow = "#FFFAF0"
    val red = "#FF2C23"
    val lightRed = "#FFD7D0"
    val green = "#08FA12"
    val lightGreen = "#08FA12"
    private val ViewModel:WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    class AdBannerImage(var url: String, var current_page: String, var position: String)

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

        binding = FragmentWeatherBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = ViewModel
        shareLayout = binding.shareScreen
        binding.imgShare.setOnClickListener(){
            screenShot()
        }
        binding.recycleView.adapter = WeatherAdapter(WeatherAdapter.DiffCallback.OnClickListener {
         ViewModel.displayPropertyDaily(it)
        })
       binding.recycleViewHourly.adapter = HourlyAdapter(HourlyAdapter.OnClickListener {
         ViewModel.displayPropertyHourly(it)
       })

        observer()
        ViewModel.getCurrentWeather()
        ViewModel.getWeekWeather()
        ViewModel.getHourlyWeather()


        binding.imgBack.setOnClickListener { requireActivity().onBackPressed() }
        return binding.root
    }
//     fun onClick(){
//          binding.recycleViewHourly.setOnClickListener(){
//              binding.recycleViewHourly.setBackgroundResource(R.drawable.green_border)
//          }
//     }


    fun screenShot(){
        val now = Date()
        android.text.format.DateFormat.format("",now)
        val path = context?.getExternalFilesDir(null)?.absolutePath+"/"+now+".jpg"
        val bitmap = Bitmap.createBitmap(shareLayout.width,shareLayout.height,Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        shareLayout.draw(canvas)
        val imageFile = File(path)
        val outputFile = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputFile)
        outputFile.flush()
        outputFile.close()
        val URI = FileProvider.getUriForFile(requireContext(),"com.example.outgrow",imageFile)

        val i = Intent()
        i.action = Intent.ACTION_SEND
        //i.putExtra(Intent.EXTRA_TEXT,"Title")
        i.putExtra(Intent.EXTRA_STREAM,URI)
        i.type = "text/plain"
        startActivity(i)
    }

    fun observer(){
        ViewModel.response.observe(viewLifecycleOwner) {

            if (null != it) {
                val date:Int = it.dt
                val formatter = SimpleDateFormat("EE, d MMMM")//or use getDateInstance()
                val formatedDate = formatter.format(date*1000L)
                binding.date.text = formatedDate.toString()
            }
           // binding.icon.text = it.weather[0].description
            when(it.weather[0].id){
                200-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                201-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                    
                }
                202-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                    
                }
                210-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                211-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                212-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                    
                }
                221-> {binding.tvTodayTips.text = it.weather[0].description
                    binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                    
                }
                230-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                231-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                    
                }
                232-> {binding.icon.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                }
                300-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                301-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                302-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                    
                }
                310-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                311-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                312-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning,0,0,0)
                }
                313-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                314-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                321-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.tvTodayTips.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
               500-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.tvTodayTips.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                501-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                502-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                503-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                504-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                511-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                520-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                521-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                522-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                531-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(red))
                    
                }
                701-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                711-> {binding.tvTodayTips.text = it.weather[0].description
                    binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                721-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                731-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                741-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                751-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                761-> {binding.tvTodayTips.text = it.weather[0].description
                    binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                800-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(green))
                   //
                    binding.tvTodayTips.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation,0,0,0)

                }
                801-> {binding.tvTodayTips.text = it.weather[0].description
                    //binding.icon.setTextColor(Color.parseColor(green))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation,0,0,0)
                }
                802-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(green))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation,0,0,0)
                }
                803-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }
                804-> {binding.tvTodayTips.text = it.weather[0].description
                   // binding.icon.setTextColor(Color.parseColor(yellow))
                   
                    binding.icon.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_circle_exclamation_brown,0,0,0)
                }

            }
        }


        ViewModel.navigateToSelectedProperty.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    WeatherFragmentDirections.actionWeatherFragmentToSheetDialogFragment(it))
                ViewModel.displayPropertyDetailsCompleteDaily()
            }
        }
        ViewModel.navigateToSelectedHourly.observe(viewLifecycleOwner){
            if(null!=it){
            this.findNavController().navigate(WeatherFragmentDirections.actionWeatherFragmentToSheetHourlyFragment(it))
            ViewModel.displayPropertyDetailsCompleteHourly()

        }}
    }


}