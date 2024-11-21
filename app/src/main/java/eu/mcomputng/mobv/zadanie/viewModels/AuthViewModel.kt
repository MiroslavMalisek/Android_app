package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.models.LoginResultPair
import eu.mcomputng.mobv.zadanie.data.models.RegistrationResultPair
import eu.mcomputng.mobv.zadanie.data.models.UpdateLocationPair
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel(), ViewModelInterface {
    private val _registrationResult = MutableLiveData<RegistrationResultPair>()
    val registrationResult: LiveData<RegistrationResultPair> get() = _registrationResult

    private val _loginResult = MutableLiveData<LoginResultPair>()
    val loginResult: LiveData<LoginResultPair> get() = _loginResult

    val loginUsername = MutableLiveData<String>()
    val loginPassword = MutableLiveData<String>()

    fun registerUser(context: Context, username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationResult.postValue(dataRepository.apiRegisterUser(context, username, email, password))
        }
    }

    fun loginUser(view: View) {
        viewModelScope.launch {
            _loginResult.postValue(dataRepository.apiLoginUser(
                view.context,
                loginUsername.value ?: "",
                loginPassword.value ?: ""))
        }
    }

    fun getUser(context: Context, id: String){
        viewModelScope.launch {
            dataRepository.apiGetUser(context, id)
        }
    }

    fun getGeofence(context: Context){
        viewModelScope.launch {
            dataRepository.apiGetGeofenceUsers(context)
        }
    }

    override fun clear() {
        _registrationResult.postValue(RegistrationResultPair(""))
        _loginResult.postValue(LoginResultPair(""))
        loginUsername.postValue("")
        loginPassword.postValue("")
    }
}