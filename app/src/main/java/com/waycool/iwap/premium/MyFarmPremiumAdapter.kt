package com.waycool.iwap.premium

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemPremiumAddFarmBinding
import com.example.soiltesting.databinding.ItemPremiumCropsBinding
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.PolygonOptions
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.iwap.R
import java.util.ArrayList

class MyFarmPremiumAdapter( val farmdetailslistener: farmdetailslistener) :  RecyclerView.Adapter<MyFarmPremiumViewHolder>() {
    var details = mutableListOf<MyFarmsDomain>()
    fun setMovieList(movies: ArrayList<MyFarmsDomain>?) {
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
        val details = details[position]
        holder.binding.tvAddDeviceStart.text=details.farmName
        holder.binding.totalAreea.text=details.farmArea
        holder.binding.tvEnableAddDevice.text="Bangalore"

//        loadFarm(details.farmJson)
        holder.binding.viewFarmDetails.setOnClickListener {
            farmdetailslistener.farmDetails(details)

        }


    }

//    private fun loadFarm(farmJson: ArrayList<LatLng>?) {
//        mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
//        if (farmJson != null) {
//            val points = farmJson
//            if (points != null) {
//                if (polygon != null)
//                    polygon!!.remove()
//                polygon = null
//                if (points.size >= 3) {
//                    polygon = mMap?.addPolygon(
//                        PolygonOptions().addAll(points).fillColor(Color.argb(100, 58, 146, 17))
//                            .strokeColor(
//                                Color.argb(255, 255, 255, 255)
//                            )
//                    )
//
//                    mMap?.animateCamera(
//                        CameraUpdateFactory.newLatLngBounds(
//                            getLatLnBounds(points), 10
//                        )
//                    )
//                } else {
//                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(points[0], 16f))
//                }
//            }
//        }
//
//    }

    override fun getItemCount(): Int {
        return details.size
    }

}
class MyFarmPremiumViewHolder(val binding: ItemPremiumAddFarmBinding) :
    RecyclerView.ViewHolder(binding.root) {
}