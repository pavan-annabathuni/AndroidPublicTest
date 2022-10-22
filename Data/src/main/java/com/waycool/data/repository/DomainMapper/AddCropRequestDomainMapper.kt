package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.AddCropResponseDTO
import com.waycool.data.repository.util.DomainMapper

class AddCropRequestDomainMapper: DomainMapper<AddCropResponseDTO, AddCropResponseDTO> {
    //    override fun mapToDomain(dto: AddCropResponseDTO): AddCropRequestDomain {
//        return  AddCropRequestDomain(
//            account_no_id = dto.accountNoId,
//            area = dto.area,
//            crop_id = dto.cropId,
//            crop_season = dto.cropSeason,
//            crop_shade = dto.cropShade,
//            crop_stage = dto.cropStage,
//            crop_variety_id = dto.cropId,
//            crop_year = dto.cropYear,
//            drip_emitter_rate = dto.dripEmitterRate,
//            farm_id = dto.cropId,
//            irrigation_type = dto.irrigationType,
//            len_drip = dto.lenDrip,
//            no_of_plants = dto.noOfPlants,
//            plot_json = dto.plotJson,
//            plot_nickname = dto.plotNickname,
//            soil_type_id = dto.cropId,
//            sowing_date = dto.sowingDate,
//            width_drip = dto.widthDrip
//        )
//    }
    override fun mapToDomain(dto: AddCropResponseDTO): AddCropResponseDTO {
        return AddCropResponseDTO(

            //            account_no_id = dto.accountNoId,
//            area = dto.area,
//            crop_id = dto.cropId,
//            crop_season = dto.cropSeason,
//            crop_shade = dto.cropShade,
//            crop_stage = dto.cropStage,
//            crop_variety_id = dto.cropId,
//            crop_year = dto.cropYear,
//            drip_emitter_rate = dto.dripEmitterRate,
//            farm_id = dto.cropId,
//            irrigation_type = dto.irrigationType,
//            len_drip = dto.lenDrip,
//            no_of_plants = dto.noOfPlants,
//            plot_json = dto.plotJson,
//            plot_nickname = dto.plotNickname,
//            soil_type_id = dto.cropId,
//            sowing_date = dto.sowingDate,
//            width_drip = dto.widthDrip

        )

    }

}