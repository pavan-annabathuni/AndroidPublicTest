package com.waycool.featurelogin.support

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.waycool.data.Network.NetworkModels.LanguageData
import com.waycool.data.Repository.DomainModels.LanguageMasterDomain

class LanguageDiffCallback(private val oldList: List<LanguageMasterDomain>, private val newList: List<LanguageMasterDomain>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].langCode === newList.get(newItemPosition).langCode
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        val (_, value, name) = oldList[oldPosition]
        val (_, value1, name1) = newList[newPosition]

        return name == name1 && value == value1
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}