package com.example.soiltesting.ui.checksoil

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soiltesting.databinding.ItemLabsSampleBinding
import com.waycool.data.repository.domainModels.CheckSoilTestDomain
import java.util.ArrayList


class SoilTestingLabsAdapter(private val checkSoilTestListener: CheckSoilTestListener) :
    RecyclerView.Adapter<SoilTestingLabsHolder>() {
    private var selectedPosition = -1
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
        holder.binding.tvLabTitle.text = details.onp_name.toString()
        holder.binding.tvName.text = details.onp_address.toString()
        holder.binding.tvCheckCrop.text = details.onp_distance_km.toString()
        holder.binding.pinCode.text = details.onp_pincode.toString()
        holder.binding.ivCheck.setOnClickListener { view ->
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            checkSoilTestListener.checkBoxSoilTest(details)
        }
        val checkItem = holder.binding.ivCheck
        if (selectedPosition == position) {
            checkItem.setChecked(true)

        } else {
            checkItem.setChecked(false);
        }
    }

    fun upDateList() {
        selectedPosition=-1
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