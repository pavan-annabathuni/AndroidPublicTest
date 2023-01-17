package com.waycool.data.repository.domainModels

data class UserDetailsDomain(
    var userId          : Int?     = null,
    var name            : String?  = null,
    var email           : String?  = null,
    var phone           : String?  = null,
    var jwt             : String?  = null,
    var profile         : ProfileDomain? = ProfileDomain(),
    var accountId       : Int?     = null,
    var accountNo       : String?  = null,
    var accountType     : String?  = null,
    var accountIsActive : Int?     = null,
    var defaultModules  : Int?     = null,
    var subscription    : Int?     = null,
    var roleTitle       : String?  = null,
    var roleId          : Int?     = null,
    var title            :String? = null

)


data class ProfileDomain(

    var id             : Int?    = null,
    var remotePhotoUrl : String? = null,
    var langId         : Int?    = null,
    var userId         : Int?    = null,
    var lat            : String? = null,
    var long           : String? = null,
    var pincode        : String? = null,
    var village        : String? = null,
    var address        : String? = null,
    var state          : String? = null,
    var district       : String? = null,
    var subDistrict    : String? = null,
    var langCode       : String? = null,
    var lang           : String? = null


)