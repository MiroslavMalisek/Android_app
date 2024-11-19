package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.models.UpdateLocationPair
import eu.mcomputng.mobv.zadanie.data.models.UserGetPair
import eu.mcomputng.mobv.zadanie.utils.Evento
import kotlinx.coroutines.launch

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _userResult = MutableLiveData<UserGetPair>()
    val userResult: LiveData<UserGetPair> get() = _userResult

    private val _deleteLocationResult = MutableLiveData<UpdateLocationPair>()
    val deleteLocationResult: LiveData<UpdateLocationPair> get() = _deleteLocationResult

    val sharingLocation = MutableLiveData<Boolean?>(null)

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

    fun deleteUsers() {
        viewModelScope.launch {
            dataRepository.deleteUsers()
            Log.d("users after logout", dataRepository.getUsers().value.toString())
        }
    }
}