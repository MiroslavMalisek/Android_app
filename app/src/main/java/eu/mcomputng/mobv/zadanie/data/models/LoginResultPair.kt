package eu.mcomputng.mobv.zadanie.data.models

data class LoginResultPair (
    val message: String,
    val localUser: LocalUser? = null
)