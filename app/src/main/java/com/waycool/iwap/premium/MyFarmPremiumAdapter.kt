package com.waycool.iwap.premium

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.data.repository.domainModels.ViewDeviceDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.iwap.databinding.ItemPremiumAddFarmBinding
import com.waycool.iwap.home.FarmCropsAdapter

class MyFarmPremiumAdapter(val farmdetailslistener: Farmdetailslistener,val farmSelectedListener: FarmSelectedListener, val context: Context) :
    RecyclerView.Adapter<MyFarmPremiumAdapter.MyFarmPremiumViewHolder>() {

    var details = mutableListOf<MyFarmsDomain>()
    var selectedFarmPosition: Int? = null
    private var cropList:MutableList<MyCropDataDomain> = mutableListOf()
    private var deviceList: MutableList<ViewDeviceDomain> = mutableListOf()

//    var onFarmSelected: ((MyFarmsDomain?) -> Unit)? = null

    fun setMovieList(movies: List<MyFarmsDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFarmPremiumViewHolder {
        val binding =
            ItemPremiumAddFarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFarmPremiumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyFarmPremiumViewHolder, position: Int) {
        val detail = details[position]
        holder.binding.tvAddDeviceStart.text = detail.farmName
       holder. binding.tvAddDevice .isSelected = true
        holder.binding.tvAddDeviceStart.isSelected = true
        holder.binding.totalAreea.text = "${detail.farmArea} Acres"
        holder.binding.farmLocation.text ="${detail.farmLocation?:""}"
        TranslationsManager().loadString("view_farm_detail", holder.binding.tvAddDevice,"View Farm Details")
        val farmsCropsAdapter=FarmCropsAdapter()
        holder.binding.cropFarmRv.adapter=farmsCropsAdapter
        farmsCropsAdapter.submitList(cropList.filter { it.farmId==detail.id })
        val deviceList = deviceList.filter { it.farmId==detail.id }
        if(deviceList.isNullOrEmpty()){
            holder.binding.deviceIv.visibility=View.GONE
        }else{
            holder.binding.deviceIv.visibility=View.VISIBLE
        }
        if ((detail.isPrimary ?: 0) == 1) {
            holder.binding.ivFeedback.visibility = View.VISIBLE
        } else
            holder.binding.ivFeedback.visibility = View.GONE

        if (selectedFarmPosition == null) {
            selectedFarmPosition = position
            farmSelectedListener.onFarmSelected(detail)
        }

        if (position == selectedFarmPosition) {
            holder.binding.farmcl.setBackgroundResource(com.example.soiltesting.R.drawable.bg_add_form)
        } else {
            holder.binding.farmcl.setBackgroundResource(com.example.soiltesting.R.drawable.bg_ans)
        }

        holder.binding.farmcl.setOnClickListener {
            farmSelectedListener.onFarmSelected(detail)
            if (selectedFarmPosition != holder.layoutPosition) {
                val temp = selectedFarmPosition
                selectedFarmPosition = holder.layoutPosition
                notifyItemChanged(temp!!)
                notifyItemChanged(selectedFarmPosition!!)
            }
        }
        holder.binding.viewFarmDetails.setOnClickListener {
            farmdetailslistener.onFarmDetailsClicked(detail)
        }
    }
    override fun getItemCount(): Int {
        return details.size
    }

    fun updateCropsList(list:List<MyCropDataDomain>){
        cropList.clear()
        cropList.addAll(list)
        notifyDataSetChanged()
    }
    fun updateDeviceList(list: List<ViewDeviceDomain>) {
        deviceList.clear()
        deviceList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyFarmPremiumViewHolder(val binding: ItemPremiumAddFarmBinding) :
        RecyclerView.ViewHolder(binding.root) {
        }
}