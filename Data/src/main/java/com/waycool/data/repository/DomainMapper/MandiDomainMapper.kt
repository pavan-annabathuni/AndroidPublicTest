package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.*
import com.waycool.data.repository.domainModels.MandiDomain
import com.waycool.data.repository.domainModels.MandiDomainData
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.util.DomainMapper

class MandiDomainMapper : DomainMapper<MandiDomain, MandiEntity> {
    override fun mapToDomain(dto: MandiEntity): MandiDomain {

        return MandiDomain(
            data = MandiDataDomainMapper().mapToDomain(dto.data),
            message = dto.message,
            status = dto.status
        )
    }
}

class MandiDataDomainMapper : DomainMapper<MandiDomainData, MandiEntityData> {
    override fun mapToDomain(dto: MandiEntityData): MandiDomainData {

        return MandiDomainData(
            numberOfRecordsPerPage = dto.numberOfRecordsPerPage,
            page = dto.page,
            records = MandiRecordDomainMapper().toDomainList(dto.records),
            startFrom = dto.startFrom,
            total_pages = dto.total_pages,
            total_results = dto.total_results
        )
    }
}
class MandiRecordDomainMapper : DomainMapper<MandiDomainRecord, MandiEntityRecord> {
    fun toDomainList(initial: List<MandiEntityRecord>): List<MandiDomainRecord> {
        return initial.map { mapToDomain(it) }
    }
    override fun mapToDomain(dto: MandiEntityRecord): MandiDomainRecord {

        return MandiDomainRecord(
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



