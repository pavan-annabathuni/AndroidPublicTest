package com.example.irrigationplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.irrigationplanner.adapter.ForecastAdapter
import com.example.irrigationplanner.adapter.PagerForcastAdapter
import com.example.irrigationplanner.databinding.FragmentForecastBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.Irrigation
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.translations.TranslationsManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ForecastFragment : Fragment() {
    private lateinit var binding: FragmentForecastBinding
    private lateinit var mForecastAdapter: ForecastAdapter
    private lateinit var mPagerForcastAdapter: PagerForcastAdapter
    private lateinit var irrigation: Irrigation
    private var plotId:Int = 0
    private var cropId:Int? = null
     var area:String = "0"
    private var accountId = 0
     var length:String ="0"
     var width:String ="0"
    var areaperPlant:String = "0"

    private val viewModel: IrrigationViewModel by lazy {
        ViewModelProvider(this)[IrrigationViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            irrigation = it.getParcelable("IrrigationHis")!!
            accountId = it.getInt("accountId")!!
            plotId = it.getInt("plotId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForecastBinding.inflate(inflater)

         //translation
        viewModel.viewModelScope.launch {
            val title = TranslationsManager().getString("str_weekly_irrigation")
            binding.topAppBar.title = title
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mForecastAdapter = ForecastAdapter()
        binding.recycleViewHis.adapter = mForecastAdapter
//        viewModel.viewModelScope.launch {
//            viewModel.getIrrigationHis(2,3).observe(viewLifecycleOwner){
//                it.data?.data?.irrigation?.irrigationForecast?.let { it1 -> mForecastAdapter.setList(it1) }
//            }}
        if(irrigation!=null)
        irrigation.irrigationForecast?.let { mForecastAdapter.setList(it) }

        tabs()
        viewPager()
        onclick()
    }
    private fun tabs() {
        viewModel.getUserDetails().observe(viewLifecycleOwner){
            accountId = it.data?.accountId ?: 0
        }
        viewModel.getMyCrop2().observe(viewLifecycleOwner) {
            val data = it.data?.filter {itt->
                itt.id == plotId
            }
            /** calculating are per plant */
            if(data?.get(0)?.area !=null) {
                area = data[0].area.toString()
                length = data[0].lenDrip?:"0"
                width = data[0].widthDrip?:"0"
              areaperPlant = (length.toDouble() * width.toDouble()).toString().trim()
                mPagerForcastAdapter = PagerForcastAdapter(data[0])
//            }else{
//                if (data != null) {
//                    mPagerForcastAdapter = PagerForcastAdapter(data[0])
//                }
            }
            mPagerForcastAdapter.setListData(irrigation.irrigationForecast!!)
            binding.viewPager.adapter = mPagerForcastAdapter
            /** Formatting date for tabs*/
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                val customDate = irrigation.irrigationForecast!!.days[position]
                val inputDateFormatter: SimpleDateFormat =
                    SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
                val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
                val date: Date = inputDateFormatter.parse(customDate)
                //Toast.makeText(context, "${outputDateFormatter.format(date)}", Toast.LENGTH_SHORT).show()

                when (outputDateFormatter.format(date)) {
                    "Mon" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                    "Tue" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                    "Wed" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                    "Thu" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                    "Fri" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                    "Sat" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                    "Sun" -> {
                        tab.text = outputDateFormatter.format(date).toString()
                    }
                }
            }.attach()
        }
    }

    private fun onclick(){
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

    /** Creating view pager for swipes in tab*/
    private fun viewPager(){
       binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // This method is triggered when there is any scrolling activity for the current page
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            // triggered when you select a new page
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }

            // triggered when there is
            // scroll state will be changed
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })
    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("FragmeForecastFragmentntPlantingYield")
    }
    }

