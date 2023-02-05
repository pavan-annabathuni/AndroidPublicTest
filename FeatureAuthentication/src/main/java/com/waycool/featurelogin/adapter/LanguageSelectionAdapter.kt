package com.waycool.featurelogin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.repository.domainModels.LanguageMasterDomain
import com.waycool.featurelogin.databinding.ViewholderLanguageCardviewBinding
import com.waycool.featurelogin.support.LanguageDiffCallback


class LanguageSelectionAdapter : RecyclerView.Adapter<LanguageSelectionAdapter.ViewHolder>() {
    private val languageList: MutableList<LanguageMasterDomain> = mutableListOf()
    private var selectedPos: Int = -1
    private lateinit var context: Context
    var onItemClick: ((LanguageMasterDomain) -> Unit)? = null


    fun setData(newLangList: List<LanguageMasterDomain>) {
        val diffCallback = LanguageDiffCallback(languageList, newLangList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        //clearing the language list
        languageList.clear()
        //Adding new language list
        languageList.addAll(newLangList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context;
        val binding = ViewholderLanguageCardviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Setting data to views
        holder.tvNativeLanguage.text = languageList[position].langNative
        holder.tvLanguage.text = languageList[position].lang.toString().trim()

        //change background color and text color of selected item
        if (position == selectedPos) {
            holder.cvLanguageSelectionItem.backgroundTintList = ContextCompat.getColorStateList(context, com.waycool.uicomponents.R.color.green)
            holder.ivTickMark.visibility = View.VISIBLE
            holder.tvNativeLanguage.setTextColor(ContextCompat.getColor(context, com.waycool.uicomponents.R.color.white))
            holder.tvLanguage.setTextColor(ContextCompat.getColor(context, com.waycool.uicomponents.R.color.white))
        }
        //else set default design
        else {
            holder.cvLanguageSelectionItem.backgroundTintList = ContextCompat.getColorStateList(context, com.waycool.uicomponents.R.color.white)
            holder.ivTickMark.visibility = View.GONE
            holder.tvNativeLanguage.setTextColor(ContextCompat.getColor(context,com.waycool.uicomponents.R.color.textdark))
            holder.tvLanguage.setTextColor(ContextCompat.getColor(context, com.waycool.uicomponents.R.color.textdark))
        }

        //Click on the entire Item
        holder.cvLanguageSelectionItem.setOnClickListener {
            val tempPos: Int = selectedPos
            selectedPos = holder.layoutPosition
            notifyItemChanged(tempPos)
            notifyItemChanged(selectedPos)
            onItemClick?.invoke(languageList[position])
        }
    }

    //returning size of list
    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class ViewHolder(binding: ViewholderLanguageCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvNativeLanguage: TextView = binding.tvNativeLanguage
        val tvLanguage: TextView = binding.tvLanguage
        val cvLanguageSelectionItem = binding.cvLanguageSelectionItem
        val ivTickMark: ImageView = binding.ivTickMark
    }

    fun updatedSelectedLanguage(langCode: String){
        var pos = -1
        languageList.forEach {
            pos++
            if(it.langCode == langCode){
                selectedPos = pos
            }
        }
        notifyDataSetChanged()
    }
}