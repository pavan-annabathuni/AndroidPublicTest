package com.example.profile.adapter


import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.profile.databinding.ItemSupportBinding
import com.waycool.data.Network.NetworkModels.GetFarmSupportData
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.translations.TranslationsManager

class AddUseAdapter(val onClickListener: OnClickListener, private val userDetailsDomain: UserDetailsDomain):ListAdapter<GetFarmSupportData,AddUseAdapter.ViewHolder>(DiffCallback){
    class ViewHolder(binding:ItemSupportBinding):
        RecyclerView.ViewHolder(binding.root) {
     val delete = binding.delete
        val name = binding.name
        val number = binding.tvNumber
        val firstName = binding.textView10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val properties = getItem(position)
        holder.name.text = properties.name
        holder.number.text = properties.contact
        val text = holder.name.text[0]
        holder.firstName.text = text.toString()

   holder.delete.setOnClickListener {
       onClickListener.clickListener(properties)

   }
        if(userDetailsDomain.phone==properties.contact)
            holder.delete.visibility = View.INVISIBLE
        TranslationsManager().loadString("str_delete",holder.delete)

        if(userDetailsDomain.roleId==31){
            holder.delete.visibility = View.INVISIBLE
        }

    }
    companion object DiffCallback : DiffUtil.ItemCallback<GetFarmSupportData>() {

        override fun areItemsTheSame(
            oldItem: GetFarmSupportData,
            newItem: GetFarmSupportData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: GetFarmSupportData,
            newItem: GetFarmSupportData
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class OnClickListener(val clickListener: (data: GetFarmSupportData) -> Unit) {
        fun onClick(data: GetFarmSupportData) = clickListener(data)
    }
}