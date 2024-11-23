package eu.mcomputng.mobv.zadanie.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils
import eu.mcomputng.mobv.zadanie.bottomBar.CustomConstraintLayout
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ClearMultipleViewModels
import eu.mcomputng.mobv.zadanie.viewModels.FeedViewModel
import eu.mcomputng.mobv.zadanie.viewModels.MapViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ViewModelInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModelProfile: ProfileViewModel
    private var binding: FragmentProfileBinding? = null
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                disableSharingLocation()
            }else{
                enableSharingLocation()
            }
        }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableSharingLocation(){
        viewModelProfile.sharingLocation.postValue(true)
    }

    private fun disableSharingLocation(){
        viewModelProfile.sharingLocation.postValue(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelProfile = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)

        binding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModelProfile
        }.also { bnd ->
            bnd.loadProfileBtn.setOnClickListener {
                val user = PreferenceData.getInstance().getUser(requireContext())
                user?.let { storedUser ->
                    viewModelProfile.loadUser(requireContext(), storedUser.id)
                }
                Log.d("reset_preference", PreferenceData.getInstance().getResetPasswordUserEmail(requireContext()).toString())
            }

            bnd.logoutBtn.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {viewModelProfile.deleteLocationBlocking(requireContext())}
                    //if location was deleted
                    if (viewModelProfile.deleteLocationResult.value?.success == true){
                        clearViewModels()
                        PreferenceData.getInstance().clearData(requireContext())
                        Log.d("logout", "true")
                        //Log.d("login result when logout", ViewModelProvider(requireActivity())[AuthViewModel::class.java].loginResult.value.toString())
                        it.findNavController().navigate(R.id.action_profile_to_intro, null, Utils.options)
                    }
                    //else leave for observer
                }


                /*
                viewModel.deleteLocation(requireContext())
                PreferenceData.getInstance().clearData(requireContext())
                Log.d("login result when logout", viewModelAuth.loginResult.value.toString())

                it.findNavController().navigate(R.id.action_profile_to_intro, null, Utils.options)*/
            }

            viewModelProfile.userResult.observe(viewLifecycleOwner) {
                if (it.message.isNotEmpty()) {
                    Snackbar.make(view, it.message, Snackbar.LENGTH_LONG).setAnchorView(fab).show()
                }
            }

            //load initial sharing from preference data
            viewModelProfile.sharingLocation.postValue(
                PreferenceData.getInstance().getSharing(requireContext())
            )

            viewModelProfile.sharingLocation.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        if (!hasPermissions(requireContext())) {
                            disableSharingLocation()
                            requestPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } else {
                            //has permissions and switch being turned on
                            if (!PreferenceData.getInstance().getSharing(requireContext())){
                                PreferenceData.getInstance().putSharing(requireContext(), true)
                                PreferenceData.getInstance().putLocationAcquired(requireContext(), false)
                            }
                        }
                    } else {
                        //switch is being turned off
                        if (PreferenceData.getInstance().getSharing(requireContext())){
                            PreferenceData.getInstance().putSharing(requireContext(), false)
                            PreferenceData.getInstance().putLocationAcquired(requireContext(), false)
                            viewModelProfile.deleteLocation(requireContext())
                        }
                    }
                }
            }

            viewModelProfile.deleteLocationResult.observe(viewLifecycleOwner){result ->
                //delete of location failed
                if (!result.success){
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).setAnchorView(fab).show()
                }else{
                    Log.d("location deleted: ", result.success.toString())
                }
                //viewModelAuth.getGeofence(requireContext())
            }


        }
    }

    fun clearViewModels(){
        val viewModelClasses = listOf(
            ProfileViewModel::class.java,
            AuthViewModel::class.java,
            FeedViewModel::class.java,
            MapViewModel::class.java
        )

        ClearMultipleViewModels().clearViewModels(requireActivity(), viewModelClasses)

        /*ClearMultipleViewModels().clearViewModels(listOf(
            this.viewModelProfile,
            ViewModelProvider(requireActivity())[AuthViewModel::class.java],
            ViewModelProvider(requireActivity())[FeedViewModel::class.java],
            ViewModelProvider(requireActivity())[MapViewModel::class.java]
        ))*/
    }





}