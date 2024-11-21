package eu.mcomputng.mobv.zadanie.viewModels

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClearMultipleViewModels {
    fun clearViewModels(activity: FragmentActivity, viewModelClasses: List<Class<out ViewModel>>) {
        viewModelClasses.forEach { modelClass ->
            val viewModel = viewModelExists(activity, modelClass)
            if (viewModel != null && viewModel is ViewModelInterface) {
                viewModel.clear() // Clear the ViewModel if it implements the interface
            }
        }
    }

    fun <T : ViewModel> viewModelExists(activity: FragmentActivity, modelClass: Class<T>): T? {
        return try {
            Log.d("creating view", modelClass.toString())
            val viewModel = ViewModelProvider(activity)[modelClass]
            if (viewModel is ViewModelInterface) {
                viewModel
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d("creating view error", modelClass.toString())
            null // Return null if the ViewModel doesn't exist
        }
    }
}