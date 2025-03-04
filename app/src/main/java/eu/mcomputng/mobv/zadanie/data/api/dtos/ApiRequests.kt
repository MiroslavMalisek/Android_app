package eu.mcomputng.mobv.zadanie.data.api.dtos

data class UserRegistrationRequest(val name: String, val email: String, val password: String)

data class UserLoginRequest(val name: String, val password: String)

data class UpdateLocationRequest(val lat: Double, val lon: Double, val radius: Double)

data class ResetPasswordRequest(val email: String)

data class ChangePasswordRequest(val old_password: String, val new_password: String)

data class RefreshTokenRequest(val refresh: String)