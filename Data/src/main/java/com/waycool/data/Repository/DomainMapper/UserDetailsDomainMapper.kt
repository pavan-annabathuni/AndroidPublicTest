package com.waycool.data.Repository.DomainMapper

import com.waycool.data.Local.Entity.AccountEntity
import com.waycool.data.Local.Entity.ProfileEntity
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Network.NetworkModels.AccountNetwork
import com.waycool.data.Network.NetworkModels.ProfileNetwork
import com.waycool.data.Network.NetworkModels.UserDetailsData
import com.waycool.data.Repository.DomainModels.AccountDomain
import com.waycool.data.Repository.DomainModels.ProfileDomain
import com.waycool.data.Repository.DomainModels.UserDetailsDomain
import com.waycool.data.Repository.util.DomainMapper

class UserDetailsDomainMapper : DomainMapper<UserDetailsDomain, UserDetailsEntity> {
    override fun mapToDomain(dto: UserDetailsEntity): UserDetailsDomain {
        return UserDetailsDomain(
            id = dto.id,
            name = dto.name,
            contact = dto.contact,
            email = dto.email,
            approved = dto.approved,
            orgCodeId = dto.orgCodeId,
            profile = ProfileDomainMapper().mapToDomain(dto.profile ?: ProfileEntity()),
            account = AccountDomainMapper().toDomainList(dto.account)
        )
    }

    class ProfileDomainMapper : DomainMapper<ProfileDomain, ProfileEntity> {
        override fun mapToDomain(dto: ProfileEntity): ProfileDomain {
            return ProfileDomain(
                id = dto.id,
                gender = dto.gender,
                address = dto.address,
                village = dto.village,
                pincode = dto.pincode,
                lat = dto.lat,
                long = dto.long,
                profilePic = dto.profilePic,
                userId = dto.userId,
                langId = dto.langId,
                stateId = dto.stateId,
                districtId = dto.districtId,
                subDistrictId = dto.subDistrictId
            )
        }

    }

    class AccountDomainMapper : DomainMapper<AccountDomain, AccountEntity> {
        override fun mapToDomain(dto: AccountEntity): AccountDomain {
            return AccountDomain(
                id = dto.id,
                accountNo = dto.accountNo,
                accountType = dto.accountType,
                isActive = dto.isActive
            )
        }

        fun toDomainList(initial: List<AccountEntity>): List<AccountDomain> {
            return initial.map { mapToDomain(it) }
        }

    }
}