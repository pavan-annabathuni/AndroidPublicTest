package com.waycool.iwap.allservices

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.waycool.data.repository.domainModels.ModuleMasterDomain
import com.waycool.iwap.R
import com.waycool.iwap.databinding.ItemViewallServiceBinding
import java.util.ArrayList


class SoilTestingLabsAdapter(val context: Context) :
    RecyclerView.Adapter<SoilTestingLabsHolder>() {
    var details = mutableListOf<ModuleMasterDomain>()
    fun setMovieList(movies: ArrayList<ModuleMasterDomain>?) {
        if (movies != null) {
            this.details = movies.toMutableList()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoilTestingLabsHolder {
        val binding =
            ItemViewallServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoilTestingLabsHolder(binding)
    }

    override fun onBindViewHolder(holder: SoilTestingLabsHolder, position: Int) {
        val details = details[position]
        holder.binding.tvSoilTest.text = details.title
        Glide.with(context)
            .load(details.moduleIcon)
            .into(holder.binding.ivOne)
    }


    override fun getItemCount(): Int {
        return details.size
    }

}

class SoilTestingLabsHolder(val binding: ItemViewallServiceBinding) :
    RecyclerView.ViewHolder(binding.root) {


}