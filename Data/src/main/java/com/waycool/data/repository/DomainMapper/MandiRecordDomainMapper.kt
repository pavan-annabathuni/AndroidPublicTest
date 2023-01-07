package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.*
import com.waycool.data.repository.domainModels.MandiRecordDomain
import com.waycool.data.repository.util.DomainMapper

class MandiRecordDomainMapper : DomainMapper<MandiRecordDomain, MandiRecordEntity> {
    fun toDomainList(initial: List<MandiRecordEntity>): List<MandiRecordDomain> {
        return initial.map { mapToDomain(it) }
    }
    override fun mapToDomain(dto: MandiRecordEntity): MandiRecordDomain {

        return MandiRecordDomain(
            arrival_date = dto.arrival_date,
            avg_price = dto.avg_price,
            created_at = dto.created_at,
            crop =  dto.crop,
            crop_category = dto.crop_category,
            crop_logo = dto.crop_logo,
            crop_master_id = dto.crop_master_id,
            district = dto.district,
            id = dto.id,
            last_price = dto.last_price,
            location = dto.location,
            mandi_master_id = dto.mandi_master_id,
            market = dto.market,
            max_price = dto.max_price,
            min_price = dto.min_price,
            modal_price = dto.modal_price,
            price_diff = dto.price_diff,
            price_status = dto.price_status,
            source = dto.source,
            state = dto.state,
            sub_district = dto.sub_district,
            updated_at = dto.updated_at,
            variety = dto.variety,
            distance = dto.distance

        )
    }
}



