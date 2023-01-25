package com.example.cropinformation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cropinformation.*
import com.example.cropinformation.fragments.*
import com.waycool.data.repository.domainModels.CropInformationDomainData

//private val data: MutableList<Data> = mutableListOf()

class ViewpagerAdapter(
    fragmentActivity: Fragment,
    var data: List<CropInformationDomainData>?,
    val size: Int,
    var cropid: Int
) :
    FragmentStateAdapter(fragmentActivity) {
    // var data2 = data.value?.filter { data ->data.crop_id==cropid  }
    var data2 = data
    override fun createFragment(position: Int): Fragment {
        return when (data2?.get(position)?.labelNameTag ?: data2?.get(position)?.label_name) {

            "Crop Variety" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = CropVarityFragment()
                fragment.arguments = bundle
                fragment

            }
            "Soil pH" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SoilPhFragment()
                fragment.arguments = bundle
                fragment
            }
            "Soil Type" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SoiltypeFragment()
                fragment.arguments = bundle
                fragment
            }
            "Sowing Season" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SowingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Planting Material" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = PlantingMaterialFragment()
                fragment.arguments = bundle
                fragment
            }
            "Nursery Practices" -> {

                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = NurseryFragment()
                fragment.arguments = bundle
                fragment
            }
            "Crop Duration" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = CropDurationFragment()
                fragment.arguments = bundle
                fragment
            }
            "Seed Rate (Line Sowing)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SeedFragment()
                fragment.arguments = bundle
                fragment
            }
            "Seed Rate (Pit Sowing)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SeedRateFragment()
                fragment.arguments = bundle
                fragment
            }
            "Seed Rate (Broadcast)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SeedTreatmentFragment()
                fragment.arguments = bundle
                fragment
            }
            "Sowing Depth(cm)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SowingDepthFragment()
                fragment.arguments = bundle
                fragment
            }
            "Seed Treatment" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SeedTreatmentFragment()
                fragment.arguments = bundle
                fragment
            }
            "Spacing between Row to Row" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SpacingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Spacing between Plant to Plant" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = PlantToPlantFragment()
                fragment.arguments = bundle
                fragment
            }
            "Days to first harvest" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = HarvestFragment()
                fragment.arguments = bundle
                fragment
            }
            "Field preparation" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = FieldPreparationFragment()
                fragment.arguments = bundle
                fragment
            }
            "Intercrop" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = IntercropFragment()
                fragment.arguments = bundle
                fragment
            }
            "Irrigation Type" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = IrrigationTypeFragment()
                fragment.arguments = bundle
                fragment
            }
            "Staking" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = StakingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Distance between stakes" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = StakingDistanceFragment()
                fragment.arguments = bundle
                fragment
            }
            "Mulching" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = MulchingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Fertilizers" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = FertilizersFragment()
                fragment.arguments = bundle
                fragment
            }
            "Border crop" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = BlorderCropFragment()
                fragment.arguments = bundle
                fragment
            }
            "Weed control(cultural)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = WeedCulturalFragment()
                fragment.arguments = bundle
                fragment
            }
            "Weed control(chemical)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = WeedChemicalFragment()
                fragment.arguments = bundle
                fragment
            }
            "Yield ( kg or tons/ac)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = YieldFragment()
                fragment.arguments = bundle
                fragment
            }
            "Harvest (sowing, planting, transplantation)" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = HarvestSowingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Post Harvesting" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = PostHarvestFragment()
                fragment.arguments = bundle
                fragment
            }
            "Proposed Next Crops" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = NextCropFragment()
                fragment.arguments = bundle
                fragment
            }
            "Sowing/Planting" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = SowingPlantingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Flooding" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = FloodingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Drip" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = DripFragment()
                fragment.arguments = bundle
                fragment
            }
            "Training and Pruning" -> {

                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = TrimmingFragment()
                fragment.arguments = bundle
                fragment
            }
            "Ratooning" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = RatooningFragment()
                fragment.arguments = bundle
                fragment
            }
            "Micronutrients" -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = MicroNutrientsFragment()
                fragment.arguments = bundle
                fragment
            }

            else -> {
                val bundle = Bundle()
                bundle.putInt("CropId", cropid)
                val fragment = CropVarityFragment()
                fragment.arguments = bundle
                fragment
            }
        }

    }

    override fun getItemCount(): Int = size


//    fun update(newData:List<Data>){
//          with(_response2){
//              clear()
//              addAll(newData)
//          }
//
//          notifyDataSetChanged()
//      }
}