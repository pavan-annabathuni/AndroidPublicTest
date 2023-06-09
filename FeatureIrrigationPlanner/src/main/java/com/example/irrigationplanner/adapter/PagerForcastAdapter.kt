package com.example.irrigationplanner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.irrigationplanner.R
import com.example.irrigationplanner.databinding.ItemPagerForecastBinding
import com.waycool.data.Network.NetworkModels.IrrigationForecast
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PagerForcastAdapter(val myCropDataDomain: MyCropDataDomain)

    : RecyclerView.Adapter<PagerForcastAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: ItemPagerForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val acres = binding.tvAcres
        val etc = binding.tvEtc
        val eto = binding.tvEto
        val mm = binding.tvMm
        val perPlant = binding.textView25
        val areaPerPlant = binding.tvPerPlant
        val irrigationReq = binding.irrigationReq
        val totalWaterLoss = binding.textView20
        val eva = binding.textView2
        val totalAcre = binding.textView23
        val irImage = binding.imgMessage
        val llIrrigation = binding.llMessage
    }

    private var details = IrrigationForecast()
    fun setListData(listData: IrrigationForecast) {
        this.details = listData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemPagerForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerForcastAdapter.MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.totalAcre.text = "for ${myCropDataDomain.area} acres"
        val properties = details
        var dep = details.depletion[position]
        var area = myCropDataDomain.area
        var width: String? = myCropDataDomain.widthDrip
        var length = myCropDataDomain.lenDrip
        var areaPerPlant:String?=null
        if(!length.isNullOrEmpty()&&!width.isNullOrEmpty()) {
             areaPerPlant =(length.toDouble().times(width.toDouble())).toString().trim()
        }

        if (!area.isNullOrEmpty() && !dep.isNullOrEmpty()) {
//           area = (width?.toDouble()?.let { length?.toDouble()?.times(it) }).toString()
            holder.acres.text = String.format(Locale.ENGLISH, "%.0f", dep.toFloat() * 4046.86 * (area).toFloat() / 0.9) + " L"
        } else holder.acres.text = "0"
        if (!length.isNullOrEmpty() && !width.isNullOrEmpty()) {
            holder.areaPerPlant.text = String.format(Locale.ENGLISH, "%.0f", dep?.toFloat()?:0f * (areaPerPlant?.toFloat()
                ?:"".toFloat()) / 0.9) + " L"

        } else {
            holder.areaPerPlant.text = "0"
            holder.areaPerPlant.visibility = View.INVISIBLE
            holder.perPlant.visibility = View.INVISIBLE
        }
        holder.mm.text = dep
        holder.etc.text = properties.etc[position] + "mm"
        holder.eto.text = properties.eto[position].toString() + "mm"

        var irrigationReq:String
        var irrigationNotReq:String
        CoroutineScope(Dispatchers.Main).launch {
            irrigationNotReq = TranslationsManager().getString("str_irrigation_not_req")
            irrigationReq = TranslationsManager().getString("str_Irrigation_req")

        if (properties.mad[position] == 0.0) {
            val value = 0 - (properties.depletion[position]?.toFloat()?:0f)
            if (value < 0) {
                holder.irrigationReq.text = irrigationReq
                holder.irImage.setImageResource(R.drawable.ic_red_irrigation)
                holder.llIrrigation.setBackgroundColor(holder.itemView.resources.getColor(R.color.LightREd))

            } else {
                holder.irrigationReq.text = irrigationNotReq
                holder.irImage.setImageResource(R.drawable.ic_green_irrigation)
                holder.llIrrigation.setBackgroundColor(holder.itemView.resources.getColor(R.color.LightGreen))
            }
        } else {
            val value = properties.mad[position]?.minus((properties.depletion[position]?.toFloat()!!))
            if (value != null) {
                if (value <= 0) {
                    holder.irrigationReq.text = irrigationReq
                    holder.irImage.setImageResource(R.drawable.ic_red_irrigation)
                    holder.llIrrigation.setBackgroundColor(holder.itemView.resources.getColor(R.color.LightREd))
                } else {
        //                val value = properties.mad[position] ?: 0.0 - properties.depletion[position].toFloat()
        //                if (properties.mad[position] != null)
        //                val percentage = (value / properties.mad[position]!!) * 100
                    holder.irImage.setImageResource(R.drawable.ic_green_irrigation)
                    holder.llIrrigation.setBackgroundColor(holder.itemView.resources.getColor(R.color.LightGreen))
                    holder.irrigationReq.text = irrigationNotReq
                }
            }

        }


        //translation
        TranslationsManager().loadString("str_total_water_loss", holder.totalWaterLoss)
        TranslationsManager().loadString("str_evapotranspiration", holder.eva)

    }}

    override fun getItemCount(): Int {
        return 7
    }


}