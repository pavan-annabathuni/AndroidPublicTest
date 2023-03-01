package com.waycool.data.Local.utils

import androidx.annotation.Keep

@Keep
interface EntityMapper <Entity, DTO>{

//    fun mapFromEntity(entity: Entity): DTO

    fun mapToEntity(dto: DTO): Entity
}