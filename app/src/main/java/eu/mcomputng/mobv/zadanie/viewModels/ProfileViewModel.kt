package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.models.UserGetPair
import kotlinx.coroutines.launch

class ProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _userResult = MutableLiveData<UserGetPair>()
    val userResult: LiveData<UserGetPair> get() = _userResult

    val sharingLocation = MutableLiveData<Boolean?>(null)

    fun loadUser(context: Context, uid: String) {
        viewModelScope.launch {
            _userResult.postValue(dataRepository.apiGetUser(context, uid))
        }
    }
}