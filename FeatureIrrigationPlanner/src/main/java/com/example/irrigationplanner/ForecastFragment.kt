package com.example.irrigationplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.irrigationplanner.adapter.ForecastAdapter
import com.example.irrigationplanner.adapter.PagerForcastAdapter
import com.example.irrigationplanner.databinding.FragmentForecastBinding
import com.example.irrigationplanner.viewModel.IrrigationViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.Irrigation
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
            if(accountId!=null&&irrigation!=null) {
                irrigation = it.getParcelable("IrrigationHis")!!
                accountId = it.getInt("accountId")!!
                plotId = it.getInt("plotId")!!
            }
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
        viewModelData(0)
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
            if(data?.get(0)?.area !=null) {
                area = data[0].area.toString()
                length = data.get(0).lenDrip.toString()
                width = data.get(0).widthDrip.toString()
              areaperPlant = (length.toDouble() * width.toDouble()).toString().trim()
                mPagerForcastAdapter = PagerForcastAdapter(data.get(0))
            }else{
                if (data != null) {
                    mPagerForcastAdapter = PagerForcastAdapter(data.get(0))
                }
            }
            mPagerForcastAdapter.setListData(irrigation.irrigationForecast!!)
            binding.viewPager.adapter = mPagerForcastAdapter
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
               // val customView = tab.setCustomView(R.layout.item_tab_irrigation)
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
        // viewModel.viewModelScope.launch{
//      viewModel.getIrrigationHis(2,3).observe(viewLifecycleOwner){
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(0)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(1)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(2)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(3)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(4)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(5)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(irrigation.irrigationForecast?.days?.get(6)).setCustomView(R.layout.item_tab_irrigation))
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when(binding.tabLayout.selectedTabPosition){
//                    0-> viewModelData(0)
//                    1-> viewModelData(1)
//                    2-> viewModelData(2)
//                    3-> viewModelData(3)
//                    4-> viewModelData(4)
//                    5-> viewModelData(5)
//                    6-> viewModelData(6)
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//        })
//    }}}
    }

    private fun onclick(){
        binding.topAppBar.setNavigationOnClickListener(){
            this.findNavController().navigateUp()
        }
    }

    private fun viewModelData(i:Int){


//        viewModel.viewModelScope.launch(){
//
//                val dep = irrigation.irrigationForecast?.depletion?.get(i).toString().toFloat()
//                binding.tvEtc.text = "${irrigation.irrigationForecast?.etc?.get(i)} mm" ?: ""
//                binding.tvEto.text = "${(irrigation.irrigationForecast?.eto?.get(i))} mm"
//                binding.tvMm.text = "${irrigation.irrigationForecast?.depletion?.get(i)} mm"?:""
//                binding.tvAcres.setText(String.format(Locale.ENGLISH, "%.0f", dep * 4046.86 * area / 0.9) + " L")
//                if(areaPerPlant<=0){
//                binding.tvPerPlant.visibility = View.INVISIBLE
//                    binding.textView25.visibility = View.INVISIBLE
//                }
//                else {
//                    binding.tvPerPlant.text = (areaPerPlant*dep).toString()
//                    binding.tvPerPlant.visibility = View.VISIBLE
//                    binding.textView25.visibility = View.INVISIBLE
//                }
//
//               val  properties = irrigation.irrigationForecast
//                if (properties!!.mad[i] == 0) {
//                    val value = 30 - properties.depletion[i].toFloat()
//                    if (value <= 0) {
//                        binding.irrigationReq.text = "Irrigation Required"
//                    } else {
//                        val value = 30 - properties.depletion[i].toFloat()
//                        val percentage = (value / 30) * 100
//                        binding.irrigationReq.text = "Irrigation Not Required"
//                    }
//                } else {
//                    val value = properties.mad[i] - properties.depletion[i].toFloat()
//                    if (value <= 0) {
//                        binding.irrigationReq.text = "Irrigation Required"
//                    } else {
//                        val value = properties.mad[i] - properties.depletion[i].toFloat()
//                        val percentage = (value / properties.mad[i]) * 100
//                        binding.irrigationReq.text = "Irrigation Not Required"
//                    }
////        if(level<=0)
////        holder.waterLevel.progress = level
//                }
//            }
        }
    fun viewPager(){
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
    }

