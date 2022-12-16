package com.waycool.iwap.premium

import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemPremiumAddFarmBinding
import com.example.soiltesting.databinding.ItemPremiumCropsBinding
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.Polygon
import com.google.android.libraries.maps.model.PolygonOptions
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.iwap.R
import java.util.*

class MyFarmPremiumAdapter(val farmdetailslistener: farmdetailslistener) :  RecyclerView.Adapter<MyFarmPremiumViewHolder>() {
//    val context: Context,
    var details = mutableListOf<MyFarmsDomain>()
    private var mMap: GoogleMap? = null
    private var polygon: Polygon? = null
    private var district: String? = null
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
//        (details.farmCenter)?.get(0)?.latitude?.let { lat ->
//            (details.farmCenter)?.get(0)?.longitude?.let { lng ->
//                getFarmLocation(
//                    lat, lng
//                )
//            }
//        }
        holder.binding.viewFarmDetails.setOnClickListener {
            farmdetailslistener.farmDetails(details)
        }


    }
//    private fun getFarmLocation(lat: Double, lng: Double) {
//        val geocoder = Geocoder(context, Locale.getDefault())
//        val list: List<Address> =
//            geocoder.getFromLocation(lat, lng, 1) as List<Address>
//        district = list[0].locality + "," + list[0].adminArea
//    }
//
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
//    fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
//        val builder = LatLngBounds.builder()
//        for (ll in points) {
//            builder.include(ll)
//        }
//        return builder.build()
//    }

    override fun getItemCount(): Int {
        return details.size
    }

}
class MyFarmPremiumViewHolder(val binding: ItemPremiumAddFarmBinding) :
    RecyclerView.ViewHolder(binding.root) {
}