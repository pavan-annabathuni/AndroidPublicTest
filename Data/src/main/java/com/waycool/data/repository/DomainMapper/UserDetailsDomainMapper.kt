package com.waycool.data.repository.DomainMapper

import com.waycool.data.Local.Entity.ProfileEntity
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.repository.domainModels.ProfileDomain
import com.waycool.data.repository.domainModels.UserDetailsDomain
import com.waycool.data.repository.util.DomainMapper

class UserDetailsDomainMapper : DomainMapper<UserDetailsDomain, UserDetailsEntity> {
    override fun mapToDomain(dto: UserDetailsEntity): UserDetailsDomain {
        return UserDetailsDomain(
            userId = dto.userId,
            name = dto.name,
            email = dto.email,
            phone = dto.phone,
            jwt = dto.jwt,
            profile = ProfileDomainMapper().mapToDomain(dto.profile ?: ProfileEntity()),
            accountId = dto.accountId,
            accountNo = dto.accountNo,
            accountType = dto.accountType,
            accountIsActive = dto.accountIsActive,
            defaultModules = dto.defaultModules,
            subscription = dto.subscription,
            roleTitle = dto.roleTitle,
            roleId = dto.roleId,
            title = dto.roleTitle
        )
    }

    class ProfileDomainMapper : DomainMapper<ProfileDomain, ProfileEntity> {
        override fun mapToDomain(dto: ProfileEntity): ProfileDomain {
            return ProfileDomain(
                id = dto.id,
                remotePhotoUrl = dto.remotePhotoUrl,
                langId = dto.langId,
                userId = dto.userId,
                lat = dto.lat,
                long = dto.long,
                pincode = dto.pincode,
                village = dto.village,
                address = dto.address,
                state = dto.state,
                district = dto.district,
                subDistrict = dto.subDistrict,
                langCode = dto.langCode,
                lang = dto.lang,
            )
        }

    }

}