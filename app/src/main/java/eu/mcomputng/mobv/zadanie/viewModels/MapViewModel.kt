package eu.mcomputng.mobv.zadanie.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.models.UpdateLocationPair
import kotlinx.coroutines.launch

class MapViewModel(private val dataRepository: DataRepository) : ViewModel(), ViewModelInterface {
    private val _updateLocationResult = MutableLiveData<UpdateLocationPair>()
    val updateLocationResult: LiveData<UpdateLocationPair> get() = _updateLocationResult

    val locationAcquired = MutableLiveData<Boolean?>(null)

    fun updateLocation(context: Context, lat: Double, lon: Double, radius: Double) {
        viewModelScope.launch {
            _updateLocationResult.postValue(
                dataRepository.apiUpdateLocation(context, lat, lon, radius)
            )
        }
    }

    // Function to clear/reset the ViewModel
    override fun clear() {
        _updateLocationResult.postValue(UpdateLocationPair(""))
        locationAcquired.postValue(null)
    }

}