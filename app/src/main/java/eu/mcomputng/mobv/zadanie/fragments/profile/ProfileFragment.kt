package eu.mcomputng.mobv.zadanie.fragments.profile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils
import eu.mcomputng.mobv.zadanie.Utils.PHOTOBASEURI
import eu.mcomputng.mobv.zadanie.Utils.displayPhotoFromUri
import eu.mcomputng.mobv.zadanie.Utils.hideKeyboard
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ClearMultipleViewModels
import eu.mcomputng.mobv.zadanie.viewModels.FeedViewModel
import eu.mcomputng.mobv.zadanie.viewModels.MapViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModelProfile: ProfileViewModel
    private var binding: FragmentProfileBinding? = null
    private val PERMISSIONS_REQUIRED_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)
    private val PERMISSIONS_REQUIRED_BACKGROUND_LOCATION = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    private var mapView: MapView? = null
    private val receiver = GeofenceBroadcastReceiver()

    val requestPermissionLauncherBackground =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            Log.d("launcher", isGranted.toString())
            if (!isGranted) {
                disableBackgroundLocation()
                Log.d("launcher BE", "no")
                Toast.makeText(
                    requireContext(),
                    "Nie je povolený prístup k polohe na pozadí",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                enableBackgroundLocation()
                Log.d("launcher BE", "yes")
            }
        }

    val requestPermissionLauncherLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val deniedPermissions = permissions.filterValues { !it }.keys
            if (deniedPermissions.isNotEmpty()) {
                // At least one permission is denied
                disableSharingLocation()
                Toast.makeText(
                    requireContext(),
                    "Nie je povolený prístup k polohe",
                    Toast.LENGTH_LONG
                ).show()
            }else{
                enableSharingLocation()
            }
        }

    val requestPermissionLauncherLocationInBackground =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
        }

    fun hasLocationPermissions(context: Context) = PERMISSIONS_REQUIRED_LOCATION.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun hasBackgroundLocationPermission(context: Context) = PERMISSIONS_REQUIRED_BACKGROUND_LOCATION.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableSharingLocation(){
        viewModelProfile.sharingLocation.postValue(true)
    }

    private fun disableSharingLocation(){
        viewModelProfile.sharingLocation.postValue(false)
    }

    private fun enableBackgroundLocation(){
        viewModelProfile.backgroundLocation.postValue(true)
    }

    private fun disableBackgroundLocation(){
        viewModelProfile.backgroundLocation.postValue(false)
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

        var mapView: MapView? = view.findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)


        binding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModelProfile
        }.also { bnd ->

            bnd.customFab.setNavController(findNavController())

            val user = PreferenceData.getInstance().getUser(requireContext())
            user?.let { storedUser ->
                viewModelProfile.loadUser(requireContext(), storedUser.id)
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

            viewModelProfile.userResult.observe(viewLifecycleOwner) { result->
                if (result.message.isNotEmpty()) {
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).setAnchorView(bnd.customFab).show()
                }
                result?.let {
                    //if local user doesnt have profile photo - show icon
                    if (it.user?.photo?.isEmpty() == true){
                        bnd.userPhoto.visibility = View.GONE
                        bnd.userIcon.visibility = View.VISIBLE
                    }else{
                        //if has photo, show it
                        val userPhoto = it.user?.photo
                        Log.d("path", PHOTOBASEURI +userPhoto)
                        displayPhotoFromUri(bnd.userPhoto, PHOTOBASEURI + userPhoto)
                        bnd.userPhoto.visibility = View.VISIBLE
                        bnd.userIcon.visibility = View.GONE
                    }
                }
            }

            //load initial sharing from preference data
            viewModelProfile.sharingLocation.postValue(
                PreferenceData.getInstance().getSharing(requireContext())
            )

            //load initial background from preference data
            viewModelProfile.backgroundLocation.postValue(
                PreferenceData.getInstance().getBackgroundLocation(requireContext())
            )

            viewModelProfile.sharingLocation.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        if (!hasLocationPermissions(requireContext())) {
                            disableSharingLocation()
                            requestPermissionLauncherLocation.launch(
                                PERMISSIONS_REQUIRED_LOCATION
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

            viewModelProfile.backgroundLocation.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        if (!hasLocationPermissions(requireContext())) {
                            disableBackgroundLocation()
                            requestPermissionLauncherLocationInBackground.launch(
                                PERMISSIONS_REQUIRED_LOCATION
                            )
                        }else if (!hasBackgroundLocationPermission(requireContext())){
                            disableBackgroundLocation()
                            requestPermissionLauncherBackground.launch(
                                PERMISSIONS_REQUIRED_BACKGROUND_LOCATION[0]
                            )
                        }
                        else {
                            //has permissions and switch being turned on
                            if (!PreferenceData.getInstance().getBackgroundLocation(requireContext())){
                                PreferenceData.getInstance().putBackgroundLocation(requireContext(), true)
                                PreferenceData.getInstance().putLocationAcquired(requireContext(), false)
                                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                                fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
                                    // Logika pre prácu s poslednou polohou
                                    Log.d("ProfileFragment", "poloha posledna $it")
                                    receiver.setupGeofence(it, requireContext())
                                }
                            }
                        }
                    } else {
                        //switch is being turned off
                        if (PreferenceData.getInstance().getBackgroundLocation(requireContext())){
                            PreferenceData.getInstance().putBackgroundLocation(requireContext(), false)
                            PreferenceData.getInstance().putLocationAcquired(requireContext(), false)
                            receiver.removeGeofences(requireContext())
                        }
                    }
                }
            }


            viewModelProfile.deleteLocationResult.observe(viewLifecycleOwner){result ->
                //delete of location failed
                if (!result.success){
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).setAnchorView(bnd.customFab).show()
                }else{
                    Log.d("location deleted: ", result.success.toString())
                }
                //viewModelAuth.getGeofence(requireContext())
            }

            //hide keyboard and remove focus from inputs when user click on screen
            view.setOnTouchListener { _, _ ->
                bnd.customFab.makeFabsInvisible()
                false // Return false to allow other touch events to be processed
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }




}