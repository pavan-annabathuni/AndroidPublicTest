package com.example.soiltesting.ui.checksoil

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemLabsSampleBinding
import com.waycool.data.Network.NetworkModels.CheckSoilTestData
import com.waycool.data.Network.NetworkModels.CheckSoilTestLabDTO
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import java.util.ArrayList


class SoilTestingLabsAdapter(private val checkSoilTestListener: CheckSoilTestListener) :
    RecyclerView.Adapter<SoilTestingLabsHolder>() {
    private var selectedPosition = 0
        var details = mutableListOf<CheckSoilTestDomain>()
    fun setMovieList(movies: ArrayList<CheckSoilTestDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoilTestingLabsHolder {
        val binding =
            ItemLabsSampleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoilTestingLabsHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: SoilTestingLabsHolder, position: Int) {
        val details = details[position]
        if (details !=null){
            holder.binding.tvLabTitle.text = details.onpName .toString()
            holder.binding.tvName.text = details.onpAddress .toString()
            holder.binding.tvCheckCrop.text = details.onpDistanceKm.toString() + " from your location"
            holder.binding.pinCode.text = details.onpPincode.toString()
//        holder.binding.ivCheck.isEnabled == false
            if (position == 0 && holder.binding.ivCheck.isChecked == true) {
                checkSoilTestListener.checkBoxSoilTest(details)
            }

        }

//        holder.itemView.set
//        holder.binding.ivCheck.setOnClickListener { view ->
//            selectedPosition = holder.adapterPosition
//            notifyDataSetChanged()
//            checkSoilTestListener.checkBoxSoilTest(details)
//        }
//        val checkItem = holder.binding.ivCheck
//        if (selectedPosition == position) {
//            checkItem.setChecked(true)
//
//        } else {
//            checkItem.setChecked(false);
//        }
    }

    fun upDateList() {
        selectedPosition = -1
        this.details = details
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return details.size
    }

}

class SoilTestingLabsHolder(val binding: ItemLabsSampleBinding) :
    RecyclerView.ViewHolder(binding.root) {


}