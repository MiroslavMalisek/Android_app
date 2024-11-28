package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.data.models.ChangePasswordResultPair
import eu.mcomputng.mobv.zadanie.data.models.UpdateLocationPair
import eu.mcomputng.mobv.zadanie.data.models.ProfilePhotoPair
import eu.mcomputng.mobv.zadanie.data.models.User
import eu.mcomputng.mobv.zadanie.data.models.UserGetPair
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel(), ViewModelInterface {

    private val _userResult = MutableLiveData<UserGetPair>()
    val userResult: LiveData<UserGetPair> get() = _userResult

    private val _deleteLocationResult = MutableLiveData<UpdateLocationPair>()
    val deleteLocationResult: LiveData<UpdateLocationPair> get() = _deleteLocationResult

    private val _changePasswordResult = MutableLiveData<ChangePasswordResultPair>()
    val changePasswordResult: LiveData<ChangePasswordResultPair> get() = _changePasswordResult

    private val _uploadProfilePhotoResult = MutableLiveData<ProfilePhotoPair>()
    val uploadProfilePhotoResult: LiveData<ProfilePhotoPair> get() = _uploadProfilePhotoResult

    private val _deleteProfilePhotoResult = MutableLiveData<ProfilePhotoPair>()
    val deleteProfilePhotoResult: LiveData<ProfilePhotoPair> get() = _deleteProfilePhotoResult

    val changePasswordActualPassword = MutableLiveData<String>()
    val changePasswordNewPassword = MutableLiveData<String>()
    val changePasswordRepeatPassword = MutableLiveData<String>()

    val sharingLocation = MutableLiveData<Boolean?>(null)

    val galleryPermissionsGranted = MutableLiveData<Boolean?>(null)

    fun loadUser(context: Context, uid: String) {
        viewModelScope.launch {
            _userResult.postValue(dataRepository.apiGetUser(context, uid))
        }
    }

    fun deleteLocation(context: Context){
        viewModelScope.launch {
            _deleteLocationResult.postValue(
                dataRepository.apiDeleteLocation(context))
            deleteUsers()
        }
    }

    suspend fun deleteLocationBlocking(context: Context) {
        val locationDeleteResult = withContext(Dispatchers.IO) {
            dataRepository.apiDeleteLocation(context)
        }
        //this waits until apiDeleteLocation is done
        if (locationDeleteResult.success){
            this.deleteUsers()
        }
        _deleteLocationResult.postValue(locationDeleteResult)
        Log.d("delete location when logout", locationDeleteResult.success.toString())
    }

    fun deleteUsers() {
        viewModelScope.launch {
            dataRepository.deleteUsers()
        }
    }

    fun changePassword(view: View){
        Log.d("new pass", changePasswordNewPassword.value.toString())
        Log.d("repeat pass", changePasswordRepeatPassword.value.toString())
        if (changePasswordNewPassword.value != changePasswordRepeatPassword.value){
            _changePasswordResult.postValue(ChangePasswordResultPair(
                view.context.getString(R.string.changePasswordRepeatPasswordMatch),
                false))
        }else{
            val user = PreferenceData.getInstance().getUser(view.context)
            val emailResetPassword: String? = PreferenceData.getInstance().getResetPasswordUserEmail(view.context)
            user?.let { storedUser ->
                if (emailResetPassword != null && (storedUser.username == emailResetPassword)){
                    //here we had password reset, so actual password is no hashed
                    viewModelScope.launch {
                        _changePasswordResult.postValue(
                            dataRepository.apiChangePassword(doHashPassword = false,
                                view.context,
                                changePasswordActualPassword.value ?: "",
                                changePasswordNewPassword.value ?: ""))
                    }
                }else{
                    viewModelScope.launch {
                        _changePasswordResult.postValue(
                            dataRepository.apiChangePassword(doHashPassword = true,
                                view.context,
                                changePasswordActualPassword.value ?: "",
                                changePasswordNewPassword.value ?: ""))
                    }
                }
            }

        }
    }

    fun uploadProfilePhoto(context: Context, image: File){
        viewModelScope.launch {
            val result = dataRepository.apiUploadProfilePhoto(context, image)
            if (result.message == context.getString(R.string.uploadPhotoSuccess)){
                updateUserAfterPhotoEdit(result)
            }
            _uploadProfilePhotoResult.postValue(result)
        }
    }

    fun deleteProfilePhoto(context: Context){
        viewModelScope.launch {
            val result = dataRepository.apiDeleteProfilePhoto(context)
            if (result.message == context.getString(R.string.deletePhotoSuccess)){
                updateUserAfterPhotoEdit(result)
            }
            _deleteProfilePhotoResult.postValue(result)
        }
    }

    private fun updateUserAfterPhotoEdit(result: ProfilePhotoPair){
        _userResult.postValue(UserGetPair(
            "", result.user?.let { User(it.id, result.user.name, result.user.photo) })
        )
    }

    override fun clear() {
        _userResult.postValue(UserGetPair(""))
        _deleteLocationResult.postValue(UpdateLocationPair(""))
        sharingLocation.postValue(null)
        clearChangePassword()
        clearPhotoEditor()
    }

    fun clearChangePassword(){
        changePasswordActualPassword.postValue("")
        changePasswordNewPassword.postValue("")
        changePasswordRepeatPassword.postValue("")
        clearChangePasswordResult()
    }

    fun clearChangePasswordResult(){
        _changePasswordResult.postValue(ChangePasswordResultPair())
    }

    fun clearPhotoEditor(){
        _uploadProfilePhotoResult.postValue(ProfilePhotoPair())
        _deleteProfilePhotoResult.postValue(ProfilePhotoPair())
        galleryPermissionsGranted.postValue(null)
    }
}