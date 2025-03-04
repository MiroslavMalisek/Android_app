package eu.mcomputng.mobv.zadanie.data.api.dtos

data class UserRegistrationResponse(val uid: String, val access: String, val refresh: String)

data class UserLoginResponse(val uid: String, val access: String, val refresh: String)

data class UserResponse(val id: String, val name: String, val photo: String)

data class UpdateLocationResponse(val success: String)

data class ResetPasswordResponse(val status: String, val message: String = "")

data class ChangePasswordResponse(val status: String)

data class RefreshTokenResponse(val uid: String, val access: String, val refresh: String)

data class GeofenceResponse(
    val me: GeofenceMeResponse,
    val list: List<GeofenceUserResponse>
)

data class GeofenceUserResponse(
    val uid: String,
    val radius: Double,
    val updated: String,
    val name: String,
    val photo: String
)


data class GeofenceMeResponse(
    val uid: String,
    val lat: Double,
    val lon: Double,
    val radius: Double
)