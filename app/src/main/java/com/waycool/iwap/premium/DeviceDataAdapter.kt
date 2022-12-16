package com.waycool.iwap.premium

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.Network.NetworkModels.ViewDeviceData
import com.waycool.iwap.databinding.TempBinding

class DeviceDataAdapter : RecyclerView.Adapter<DeviceDataViewHolder>()  {
    var itemsList = mutableListOf<ViewDeviceData>()
    fun setMovieList(movies: ArrayList<ViewDeviceData>) {
        if (movies != null) {
            this.itemsList = movies.toMutableList()
        }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceDataViewHolder {
        val binding =
            TempBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceDataViewHolder, position: Int) {
        val details = itemsList[position]
//        holder.binding.tvAddDeviceStart.text = details.model?.modelName.toString()
//        holder.binding.totalAreea.text = details.iotDevicesData?.battery.toString()
//        holder.binding.
        holder.binding.let {
            it.totalAreea.text = details.iotDevicesData?.battery.toString()
            it.tvAddDeviceStart.text = details.model?.modelName.toString()
            it.tvTempDegree.text = details.temperature.toString()
            it.tvWindDegree.text = details.rainfall.toString()
            it.tvHumidityDegree.text = details.humidity.toString()
            it.tvWindSpeedDegree.text = details.windspeed.toString()
            it.tvLeafWetnessDegree.text = details.leafWetness.toString()
            it.tvPressureDegree.text = details.pressure.toString()
            it.ivSoilDegree.text = details.soilTemperature1.toString() + "C"
            it.ivSoilDegreeOne.text = details.soilTemperature2.toString() + "C"
            it.tvLastUpdate.text = details.dataTimestamp.toString()
            it.tubeSpeedometer.maxSpeed = 100f
            it.tubeSpeedometer.speedTo(140f)
            it.tubeSpeedometer.speedometerBackColor = Color.GRAY

        }
    }

    override fun getItemCount(): Int {
        return itemsList.size

    }
    fun upDateList(list: ArrayList<ViewDeviceData>) {
        this.itemsList = list
        notifyDataSetChanged()

    }

}
class DeviceDataViewHolder(val binding: TempBinding) :
    RecyclerView.ViewHolder(binding.root) {
}