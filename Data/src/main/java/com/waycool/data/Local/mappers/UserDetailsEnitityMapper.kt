package com.waycool.data.Local.mappers

import com.waycool.data.Local.Entity.ProfileEntity
import com.waycool.data.Local.Entity.UserDetailsEntity
import com.waycool.data.Local.utils.EntityMapper
import com.waycool.data.Network.NetworkModels.AccountNetwork
import com.waycool.data.Network.NetworkModels.ProfileNetwork
import com.waycool.data.Network.NetworkModels.RolesNetwork
import com.waycool.data.Network.NetworkModels.UserDetailsData

class UserDetailsEnitityMapper : EntityMapper<UserDetailsEntity, UserDetailsData> {

    override fun mapToEntity(dto: UserDetailsData): UserDetailsEntity {
        return UserDetailsEntity(
            userId = dto.id,
            name = dto.name,
            email = dto.email,
            phone = dto.contact,
            jwt = dto.jwt,
            profile = ProfileMapper().mapToEntity(dto.profile ?: ProfileNetwork()),
            accountId = getAccount(dto.account)?.id,
            accountNo = getAccount(dto.account)?.accountNo,
            accountType = getAccount(dto.account)?.accountType,
            accountIsActive = getAccount(dto.account)?.isActive,
            defaultModules = getAccount(dto.account)?.defaultModules,
            subscription = getAccount(dto.account)?.subscription,
            roleTitle = getRole(dto.roles)?.title,
            roleId = getRole(dto.roles)?.pivot?.roleId
        )
    }

    private fun getAccount(accountlist: List<AccountNetwork>?): AccountNetwork? {
        if (accountlist == null)
            return null
        for (account in accountlist) {
            if (account.accountType == "outgrow") {
                return account
            }
        }
        return null
    }

    private fun getRole(rolesList: List<RolesNetwork>?): RolesNetwork? {
        if (rolesList == null)
            return null
        for (role in rolesList) {
            if(role.title == "Farm Support"){
                return role
            }
            if (role.title == "Farmer") {
                return role
            }
        }
        return null
    }

    class ProfileMapper : EntityMapper<ProfileEntity, ProfileNetwork> {

        override fun mapToEntity(dto: ProfileNetwork): ProfileEntity {
            return ProfileEntity(
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
                langCode = dto.lang?.langCode,
                lang = dto.lang?.lang,
            )
        }


    }
}