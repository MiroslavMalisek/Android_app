package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.models.RegistrationResultPair
import eu.mcomputng.mobv.zadanie.data.models.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<RegistrationResultPair>()
    val registrationResult: LiveData<RegistrationResultPair> get() = _registrationResult

    fun registerUser(context: Context, username: String, email: String, password: String) {
        viewModelScope.launch {
            _registrationResult.postValue(dataRepository.apiRegisterUser(context, username, email, password))
        }
    }
}