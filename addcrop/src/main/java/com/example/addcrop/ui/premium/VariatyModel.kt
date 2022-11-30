package com.example.addcrop.ui.premium

data class VariatyModel(val id: Int, val name: String, val isSelected: Boolean? = null)
object MockList {

    fun getModel(): List<VariatyModel> {


        val itemModel1 = VariatyModel(
            1,
            "Bhagwa",
        )

        val itemModel2 = VariatyModel(
            2,
            "Ganesh",
        )


        val itemModel3 = VariatyModel(
            3,
            "Others",
        )
        val itemList: ArrayList<VariatyModel> = ArrayList()
        itemList.add(itemModel1)
        itemList.add(itemModel2)
        itemList.add(itemModel3)
        return itemList
    }
}