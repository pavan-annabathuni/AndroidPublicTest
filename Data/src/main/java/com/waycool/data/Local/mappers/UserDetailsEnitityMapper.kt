package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.AccountEntity
import com.waycool.data.Local.Entity.ProfileEntity
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AccountNetwork
import com.waycool.data.Network.NetworkModels.ProfileNetwork
import com.waycool.data.Network.NetworkModels.UserDetailsData

class UserDetailsEnitityMapper : EntityMapper<UserDetailsEntity, UserDetailsData> {
    fun mapFromEntity(entity: UserDetailsEntity): UserDetailsData {
        return UserDetailsData(
            id = entity.id,
            name = entity.name,
            contact = entity.contact,
            email = entity.email,
            encryptedToken=entity.encryptedToken,
            approved = entity.approved,
            orgCodeId = entity.orgCodeId,
            profile = entity.profile?.let { ProfileMapper().mapFromEntity(it) },
            account = AccountMapper().fromEntityList(entity.account) as ArrayList<AccountNetwork>
        )
    }

    override fun mapToEntity(dto: UserDetailsData): UserDetailsEntity {
        return UserDetailsEntity(
            id = dto.id,
            name = dto.name,
            contact = dto.contact,
            email = dto.email,
            approved = dto.approved,
            encryptedToken=dto.encryptedToken,
            orgCodeId = dto.orgCodeId,
            profile = dto.profile?.let { ProfileMapper().mapToEntity(it) },
            account = AccountMapper().toEntityList(dto.account) as ArrayList<AccountEntity>
        )
    }

    class AccountMapper : EntityMapper<AccountEntity, AccountNetwork> {
        fun mapFromEntity(entity: AccountEntity): AccountNetwork {
            return AccountNetwork(
                id = entity.id,
                accountNo = entity.accountNo,
                accountType = entity.accountType,
                isActive = entity.isActive
            )
        }

        override fun mapToEntity(dto: AccountNetwork): AccountEntity {
            return AccountEntity(
                id = dto.id,
                accountNo = dto.accountNo,
                accountType = dto.accountType,
                isActive = dto.isActive
            )
        }

        fun fromEntityList(initial: List<AccountEntity>): List<AccountNetwork> {
            return initial.map { mapFromEntity(it) }
        }

        fun toEntityList(initial: List<AccountNetwork>): List<AccountEntity> {
            return initial.map { mapToEntity(it) }
        }
    }

    class ProfileMapper : EntityMapper<ProfileEntity, ProfileNetwork> {
        fun mapFromEntity(entity: ProfileEntity): ProfileNetwork {
            return ProfileNetwork(
                id = entity.id,
                gender = entity.gender,
                address = entity.address,
                village = entity.village,
                pincode = entity.pincode,
                lat = entity.lat,
                long = entity.long,
                profilePic = entity.profilePic,
                userId = entity.userId,
                langId = entity.langId,
                stateId = entity.stateId,
                districtId = entity.districtId,
                subDistrictId = entity.subDistrictId
            )
        }

        override fun mapToEntity(dto: ProfileNetwork): ProfileEntity {
            return ProfileEntity(
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
}