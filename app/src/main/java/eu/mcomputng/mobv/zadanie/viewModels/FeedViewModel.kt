package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputng.mobv.zadanie.utils.Evento
import kotlinx.coroutines.launch

class FeedViewModel(private val repository: DataRepository, private val context: Context) : ViewModel() {

    val feed_items: LiveData<List<UserEntity>?> =
        liveData {
            loading.postValue(true)
            repository.apiGetGeofenceUsers(context)
            loading.postValue(false)
            emitSource(repository.getUsers())
        }

    val loading = MutableLiveData(false)

    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    fun updateItems() {
        viewModelScope.launch {
            loading.postValue(true)
            _message.postValue(Evento(repository.apiGetGeofenceUsers(context)))
            loading.postValue(false)
        }
    }


}