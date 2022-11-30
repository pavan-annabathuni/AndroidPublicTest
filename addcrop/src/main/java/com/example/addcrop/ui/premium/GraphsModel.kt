package com.example.addcrop.ui.premium

data class GraphsModel(val id:Int,val name:String, val isSelected:Boolean?=null)

object MockListGraphs{

    fun getModel(): List<GraphsModel> {


        val itemModel1 = GraphsModel(
            1,
            "Thompson Seedless",
        )

        val itemModel2 = GraphsModel(
            2,
            "Bangalore Blue",
        )


        val itemModel3 = GraphsModel(
            3,
            "Sauvignon Blanc",
        )


        val itemModel4 = GraphsModel(
            4 ,
            "Sudhakar seedless",
        )

        val itemModel5 = GraphsModel(
            5 ,
            "Sharad seedless",
        )
        val itemModel6 = GraphsModel(
            6 ,
            "Mama jumbo",
        )
        val itemModel7 = GraphsModel(
            7 ,
            "Nanasaheb purple",
        )
        val itemModel8 = GraphsModel(
            8,
            "Sonaka",
        )
        val itemModel9 = GraphsModel(
            9 ,
            "Super sonaka",
        )
        val itemModel10 = GraphsModel(
            10 ,
            "Anushka",
        )
        val itemModel11 = GraphsModel(
            11 ,
            "Crimson",
        )
        val itemModel12 = GraphsModel(
            12 ,
            "Flame",
        )
        val itemModel13 = GraphsModel(
            13 ,
            "RK",
        )
        val itemModel14 = GraphsModel(
            14,
            "SSN",
        )
        val itemModel15 = GraphsModel(
            15 ,
            "Manikchaman",
        )
        val itemModel16 = GraphsModel(
            16 ,
            "Ganesh",
        )
        val itemModel17 = GraphsModel(
            17 ,
            "Dhanaka",
        )
        val itemModel18 = GraphsModel(
            18,
            "Arra-15",
        )
        val itemModel19 = GraphsModel(
            19 ,
            "Red Globe",
        )
        val itemModel20 = GraphsModel(
            20,
            "Other Table Grape Variety",
        )


        val itemList: ArrayList<GraphsModel> = ArrayList()
        itemList.add(itemModel1)
        itemList.add(itemModel2)
        itemList.add(itemModel3)
        itemList.add(itemModel4)
        itemList.add(itemModel5)
        itemList.add(itemModel6)
        itemList.add(itemModel7)
        itemList.add(itemModel8)
        itemList.add(itemModel9)
        itemList.add(itemModel10)
        itemList.add(itemModel11)
        itemList.add(itemModel12)
        itemList.add(itemModel13)
        itemList.add(itemModel14)
        itemList.add(itemModel15)
        itemList.add(itemModel16)
        itemList.add(itemModel17)
        itemList.add(itemModel18)
        itemList.add(itemModel19)
        itemList.add(itemModel20)
        return itemList
    }
}
