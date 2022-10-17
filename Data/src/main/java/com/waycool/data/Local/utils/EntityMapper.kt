package com.waycool.data.Local.utils


interface EntityMapper <Entity, DTO>{

//    fun mapFromEntity(entity: Entity): DTO

    fun mapToEntity(dto: DTO): Entity
}