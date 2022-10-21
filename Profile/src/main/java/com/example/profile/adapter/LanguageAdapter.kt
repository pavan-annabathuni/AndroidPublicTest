package com.example.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain
import com.waycool.featurelogin.databinding.ViewholderLanguageCardviewBinding
import com.waycool.featurelogin.support.LanguageDiffCallback

class LanguageAdapter: RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private val languageList: MutableList<LanguageMasterDomain> = mutableListOf()

    private var selectedPos: Int = -1
    private lateinit var context: Context
    var onItemClick: ((LanguageMasterDomain) -> Unit)? = null


    fun setData(newLangList: List<LanguageMasterDomain>) {
        val diffCallback = LanguageDiffCallback(languageList, newLangList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        languageList.clear()
        languageList.addAll(newLangList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context;
        val binding =
            ViewholderLanguageCardviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nativeNameTv.setText(languageList.get(position).langNative)
        holder.languageNameTv.setText(languageList.get(position).lang.toString().trim())

        if (selectedPos == -1 && languageList[position].langCode!!.contains("en")) {
            selectedPos = holder.adapterPosition
            onItemClick?.invoke(languageList[position])
        }

        if (position == selectedPos) {
//            holder.mLanguageParent.setBackground(context.resources.getDrawable(R.drawable.selector_revamp_language_selected))
            holder.mLanguageParent.backgroundTintList =
                ContextCompat.getColorStateList(context, com.waycool.uicomponents.R.color.green)
            holder.tickLayout.setVisibility(View.VISIBLE)
            // holder.checkbox.setChecked(true);
            // holder.checkbox.setChecked(true);
            holder.nativeNameTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    com.waycool.uicomponents.R.color.white
                )
            )
            holder.languageNameTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    com.waycool.uicomponents.R.color.white
                )
            )

        } else {
//            holder.mLanguageParent.setBackground(context.resources.getDrawable(R.drawable.bg_lang_circle))
            holder.mLanguageParent.backgroundTintList =
                ContextCompat.getColorStateList(context, com.waycool.uicomponents.R.color.white)

            holder.tickLayout.setVisibility(View.GONE)
            holder.nativeNameTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    com.waycool.uicomponents.R.color.textdark
                )
            )
            holder.languageNameTv.setTextColor(
                ContextCompat.getColor(
                    context,
                    com.waycool.uicomponents.R.color.textdark
                )
            )
        }
        holder.mLanguageParent.setOnClickListener {
            val tempPos: Int = selectedPos
            selectedPos = holder.adapterPosition
            notifyItemChanged(tempPos)
            notifyItemChanged(selectedPos)
        }
    }

    override fun getItemCount(): Int {
        return languageList.size
    }

    inner class ViewHolder(binding: ViewholderLanguageCardviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nativeNameTv: TextView = binding.nativeTvVh
        val languageNameTv: TextView = binding.languageTvVh
        val mLanguageParent = binding.languageParent
        val tickLayout: ImageView = binding.tickView

        init {
            mLanguageParent.setOnClickListener {
                onItemClick?.invoke(languageList[adapterPosition])
            }
        }
    }
}