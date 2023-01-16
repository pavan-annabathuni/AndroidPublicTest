package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.*
import com.waycool.data.repository.domainModels.MandiDomain
import com.waycool.data.repository.domainModels.MandiDomainData
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.repository.util.DomainMapper

class MandiDomainMapper : DomainMapper<MandiDomain, MandiEntity> {
    override fun mapToDomain(dto: MandiEntity): MandiDomain {

        return MandiDomain(
            data = dto.data?.let { MandiDataDomainMapper().mapToDomain(it) },
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
            total_pages = dto.totalPages,
            total_results = dto.totalResults
        )
    }
}
class MandiRecordDomainMapper : DomainMapper<MandiDomainRecord, MandiEntityRecord> {
    fun toDomainList(initial: List<MandiEntityRecord>): List<MandiDomainRecord> {
        return initial.map { mapToDomain(it) }
    }
    override fun mapToDomain(dto: MandiEntityRecord): MandiDomainRecord {

        return MandiDomainRecord(
            crop_te = dto.cropTe,
            market_te = dto.marketTe,
            crop_hi = dto.cropHi,
            market_hi = dto.marketHi,
            crop_kn = dto.cropKn,
            market_kn = dto.marketKn,
            market_mr = dto.marketMr,
            crop_mr = dto.cropMr,
            crop_ta = dto.cropTa,
            market_ta = dto.marketTa,
            crop = dto.crop,
            market = dto.market,
            avg_price = dto.avgPrice,
            source = dto.source,
            price_status = dto.priceStatus,
            crop_master_id = dto.cropMasterId,
            mandi_master_id = dto.mandiMasterId,
            crop_logo = dto.cropLogo,
            sub_record_id = dto.subRecordId,
            id = dto.subRecordId,
            distance = dto.distance


        )
    }
}



