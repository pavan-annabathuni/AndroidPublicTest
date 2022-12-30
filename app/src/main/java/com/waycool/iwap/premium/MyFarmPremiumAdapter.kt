package com.waycool.iwap.premium

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.maps.*
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.LatLngBounds
import com.google.android.libraries.maps.model.Polygon
import com.google.android.libraries.maps.model.PolygonOptions
import com.waycool.data.repository.domainModels.MyCropDataDomain
import com.waycool.data.repository.domainModels.MyCropsDomain
import com.waycool.data.repository.domainModels.MyFarmsDomain
import com.waycool.iwap.R
import com.waycool.iwap.databinding.ItemPremiumAddFarmBinding
import com.waycool.iwap.home.FarmCropsAdapter
import kotlin.collections.ArrayList

class MyFarmPremiumAdapter(val farmdetailslistener: Farmdetailslistener, val context: Context) :
    RecyclerView.Adapter<MyFarmPremiumAdapter.MyFarmPremiumViewHolder>() {

    var details = mutableListOf<MyFarmsDomain>()
    var selectedFarmPosition: Int? = null
    private var cropList:MutableList<MyCropDataDomain> = mutableListOf()
    private val farmsCropsAdapter by lazy { FarmCropsAdapter() }
    var onFarmSelected: ((MyFarmsDomain?) -> Unit)? = null

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
        holder.binding.tvAddDeviceStart.isSelected = true
        holder.binding.totalAreea.text = "${detail.farmArea} Acres"
        holder.binding.cropFarmRv.adapter=farmsCropsAdapter

        farmsCropsAdapter.submitList(cropList.filter { it.farmId==detail.id })
//        holder.binding.tvEnableAddDevice.text = ""
        if ((detail.isPrimary ?: 0) == 1) {
            holder.binding.ivFeedback.visibility = View.VISIBLE
        } else
            holder.binding.ivFeedback.visibility = View.GONE
//        loadFarm(details.farmJson,holder.mapCurrent)
//        (details.farmCenter)?.get(0)?.latitude?.let { lat ->
//            (details.farmCenter)?.get(0)?.longitude?.let { lng ->
//                getFarmLocation(
//                    lat, lng
//                )
//            }
//        }

        if (selectedFarmPosition == null) {
            selectedFarmPosition = position
            onFarmSelected?.invoke(details[position])
        }

//        if (position == selectedFarmPosition) {
//            holder.binding.farmcl.setBackgroundResource(com.example.soiltesting.R.drawable.bg_add_form)
//        } else {
//            holder.binding.farmcl.setBackgroundResource(com.example.soiltesting.R.drawable.bg_ans)
//        }

//        holder.binding.farmcl.setOnClickListener {
//            onFarmSelected?.invoke(details[holder.layoutPosition])
//            if (selectedFarmPosition != holder.layoutPosition) {
////                val temp = selectedFarmPosition
//                selectedFarmPosition = holder.layoutPosition
////                notifyItemChanged(temp!!)
////                notifyItemChanged(selectedFarmPosition!!)
//            notifyDataSetChanged()
//            }
//        }
        holder.binding.viewFarmDetails.setOnClickListener {
            farmdetailslistener.farmDetails(detail)
        }


    }
//    private fun getFarmLocation(lat: Double, lng: Double) {
//        val geocoder = Geocoder(context, Locale.getDefault())
//        val list: List<Address> =
//            geocoder.getFromLocation(lat, lng, 1) as List<Address>
//        district = list[0].locality + "," + list[0].adminArea
//    }


//    override fun onViewRecycled(holder: MyFarmPremiumViewHolder) {
//        // Cleanup MapView here?
//        holder.mapCurrent?.apply {
//            clear()
////            mapType = GoogleMap.MAP_TYPE_NORMAL
////            loadFarm(details[],this)
//        }
//    }

    override fun getItemCount(): Int {
        return details.size
    }

    fun updateCropsList(list:List<MyCropDataDomain>){
        cropList.clear()
        cropList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyFarmPremiumViewHolder(val binding: ItemPremiumAddFarmBinding) :
        RecyclerView.ViewHolder(binding.root), OnMapReadyCallback {
        var mapCurrent: GoogleMap? = null
        var polygonCurrent: Polygon? = null
        var map: MapView? = null

        init {
            map = itemView.findViewById(R.id.map_premium_add_farm)

            if (map != null) {
                map!!.onCreate(null)
                map!!.onResume()
                map!!.getMapAsync(this)
                map?.isClickable = false
            }
        }

        override fun onMapReady(googleMap: GoogleMap?) {
            MapsInitializer.initialize(context.applicationContext)
            mapCurrent = googleMap
            mapCurrent?.uiSettings?.setAllGesturesEnabled(false)
            mapCurrent?.uiSettings?.isMapToolbarEnabled = false
            loadFarm(details[layoutPosition].farmJson, mapCurrent)

        }


        private fun loadFarm(farmJson: ArrayList<LatLng>?, mapCurrent: GoogleMap?) {
            mapCurrent?.mapType = GoogleMap.MAP_TYPE_NORMAL
            if (farmJson != null) {
                if (polygonCurrent != null)
                    polygonCurrent?.remove()
                if (farmJson.size >= 3) {
                    polygonCurrent = mapCurrent?.addPolygon(
                        PolygonOptions().addAll(farmJson).fillColor(Color.argb(100, 58, 146, 17))
                            .strokeColor(
                                Color.argb(255, 255, 255, 255)
                            )
                    )

                    mapCurrent?.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            getLatLnBounds(farmJson), 10
                        )
                    )
                } else {
                    mapCurrent?.animateCamera(CameraUpdateFactory.newLatLngZoom(farmJson[0], 16f))
                }
            }

        }

        private fun getLatLnBounds(points: List<LatLng?>): LatLngBounds? {
            val builder = LatLngBounds.builder()
            for (ll in points) {
                builder.include(ll)
            }
            return builder.build()
        }

    }
}