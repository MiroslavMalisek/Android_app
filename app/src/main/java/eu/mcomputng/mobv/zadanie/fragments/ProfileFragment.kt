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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewModelAuth: AuthViewModel
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
        viewModel.sharingLocation.postValue(true)
    }

    private fun disableSharingLocation(){
        viewModel.sharingLocation.postValue(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        viewModelAuth = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)

        binding = FragmentProfileBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.loadProfileBtn.setOnClickListener {
                val user = PreferenceData.getInstance().getUser(requireContext())
                user?.let { storedUser ->
                    viewModel.loadUser(requireContext(), storedUser.id)
                }
            }

            bnd.logoutBtn.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                viewModel.deleteLocation(requireContext())
                Log.d("login result when logout", viewModelAuth.loginResult.value.toString())

                it.findNavController().navigate(R.id.action_profile_to_intro, null, Utils.options)
            }

            viewModel.userResult.observe(viewLifecycleOwner) {
                if (it.message.isNotEmpty()) {
                    Snackbar.make(view, it.message, Snackbar.LENGTH_LONG).setAnchorView(fab).show()
                }
            }

            //load initial sharing from preference data
            viewModel.sharingLocation.postValue(
                PreferenceData.getInstance().getSharing(requireContext())
            )

            viewModel.sharingLocation.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        if (!hasPermissions(requireContext())) {
                            disableSharingLocation()
                            requestPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } else {
                            //has permissions and switch is on
                            PreferenceData.getInstance().putSharing(requireContext(), true)
                        }
                    } else {
                        //switch is off
                        PreferenceData.getInstance().putSharing(requireContext(), false)
                        PreferenceData.getInstance().putLocationAcquired(requireContext(), false)
                        viewModel.deleteLocation(requireContext())
                    }
                }
            }

            viewModel.deleteLocationResult.observe(viewLifecycleOwner){result ->
                //delete of location failed
                if (!result.success){
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).setAnchorView(fab).show()
                }else{
                    Log.d("location deleted: ", result.success.toString())
                }
                viewModelAuth.getGeofence(requireContext())
            }


        }
    }



}