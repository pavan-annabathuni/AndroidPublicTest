package com.waycool.data.repository.DomainMapper

import com.waycool.data.Network.NetworkModels.AddressComponentsDTO
import com.waycool.data.Network.NetworkModels.GeocodeDTO
import com.waycool.data.Network.NetworkModels.ResultsDTO
import com.waycool.data.repository.domainModels.GeocodeDomain
import com.waycool.data.repository.domainModels.ResultsDomain
import com.waycool.data.repository.util.DomainMapper

class GeocodeDomainMapper : DomainMapper<GeocodeDomain, GeocodeDTO> {
    override fun mapToDomain(dto: GeocodeDTO): GeocodeDomain {
        return GeocodeDomain(
            results = ResultsDomainMapper().toDomainList(dto.results) as ArrayList<ResultsDomain>,
            status = dto.status
        )
    }

    fun toDomainList(initial: List<GeocodeDTO>): List<GeocodeDomain> {
        return initial.map { mapToDomain(it) }
    }


    class ResultsDomainMapper : DomainMapper<ResultsDomain, ResultsDTO> {

        fun toDomainList(initial: List<ResultsDTO>): List<ResultsDomain> {
            return initial.map { mapToDomain(it) }
        }

        override fun mapToDomain(dto: ResultsDTO): ResultsDomain {
            return ResultsDomain(
//                addressComponents = AddressComponentsDomainMapper().toDomainList(dto.addressComponents) as ArrayList<AddressComponentsDomain>,
                formattedAddress = dto.formattedAddress,
                country = getAddressComponent(dto.addressComponents, "country"),
                state = getAddressComponent(dto.addressComponents, "administrative_area_level_1"),
                district = getAddressComponent(dto.addressComponents, "administrative_area_level_2")?:getAddressComponent(dto.addressComponents, "administrative_area_level_3"),
                locality = getAddressComponent(dto.addressComponents, "locality"),
                subLocality = getAddressComponent(dto.addressComponents, "sublocality"),
                pincode = getAddressComponent(dto.addressComponents, "postal_code")
            )
        }

        fun getAddressComponent(
            addressComponents: ArrayList<AddressComponentsDTO>,
            component: String
        ): String? {
            for (components in addressComponents) {
                if (components.types.contains(component)) {
                    return components.longName
                }
            }
            return null
        }
    }


//    class AddressComponentsDomainMapper :
//        DomainMapper<AddressComponentsDomain, AddressComponentsDTO> {
//        override fun mapToDomain(dto: AddressComponentsDTO): AddressComponentsDomain {
//            return AddressComponentsDomain(
//                longName = dto.longName,
//                shortName = dto.shortName,
//                types = dto.types
//            )
//        }
//
//        fun toDomainList(initial: List<AddressComponentsDTO>): List<AddressComponentsDomain> {
//            return initial.map { mapToDomain(it) }
//        }
//    }
}