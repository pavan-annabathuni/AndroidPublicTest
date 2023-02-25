package com.waycool.iwap.myfarms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.iwap.databinding.ItemPremiumMyfarmsBinding
import com.waycool.iwap.home.FarmCropsAdapter
import com.waycool.iwap.premium.Farmdetailslistener

class MyFarmFragmentAdapter(val farmdetailslistener: Farmdetailslistener, val context: Context) :
    RecyclerView.Adapter<MyFarmFragmentAdapter.MyFarmPremiumViewHolder>() {
    //    val context: Context,
    var details = mutableListOf<MyFarmsDomain>()
    var selectedFarmPosition: Int? = null
    private var cropList: MutableList<MyCropDataDomain> = mutableListOf()
    private var deviceList: MutableList<ViewDeviceDomain> = mutableListOf()

    var onFarmSelected: ((MyFarmsDomain?) -> Unit)? = null

    fun setFarmsList(movies: List<MyFarmsDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFarmPremiumViewHolder {
        val binding =
            ItemPremiumMyfarmsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFarmPremiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyFarmPremiumViewHolder, position: Int) {
        val detail = details[position]
        holder.binding.tvAddDeviceStart.text = detail.farmName
        holder.binding.farmLoaction.text = "${detail.farmLocation?:""}"
        holder.binding.tvAddDeviceStart.isSelected = true
        holder.binding.tvAddDevice.isSelected = true
        TranslationsManager().loadString("view_farm_detail", holder.binding.tvAddDevice, "View Farm Details")
        holder.binding.totalAreea.text = "${detail.farmArea} Acres"
        if ((detail.isPrimary ?: 0) == 1) {
            holder.binding.ivFeedback.visibility = View.VISIBLE
        } else
            holder.binding.ivFeedback.visibility = View.GONE

        val farmsCropsAdapter= FarmCropsAdapter()
        holder.binding.cropFarmRv.adapter=farmsCropsAdapter
        farmsCropsAdapter.submitList(cropList.filter { it.farmId==detail.id })

        val deviceList = deviceList.filter { it.farmId==detail.id }
        if(deviceList.isNullOrEmpty()){
            holder.binding.deviceIv.visibility=View.GONE
        }else{
            holder.binding.deviceIv.visibility=View.VISIBLE
        }
        if (selectedFarmPosition == null) {
            selectedFarmPosition = position
            onFarmSelected?.invoke(details[position])
        }

        holder.binding.viewFarmDetails.setOnClickListener {
            farmdetailslistener.onFarmDetailsClicked(detail)
        }


    }


    override fun getItemCount(): Int {
        return details.size
    }

    fun updateCropsList(list: List<MyCropDataDomain>) {
        cropList.clear()
        cropList.addAll(list)
        notifyDataSetChanged()
    }

  fun updateDeviceList(list: List<ViewDeviceDomain>) {
        deviceList.clear()
        deviceList.addAll(list)
        notifyDataSetChanged()
    }


    inner class MyFarmPremiumViewHolder(val binding: ItemPremiumMyfarmsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}