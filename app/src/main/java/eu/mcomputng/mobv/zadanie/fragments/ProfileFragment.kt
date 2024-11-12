package eu.mcomputng.mobv.zadanie.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var viewModel: ProfileViewModel
    private var binding: FragmentProfileBinding? = null
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                viewModel.sharingLocation.postValue(false)
            }else{
                viewModel.sharingLocation.postValue(true)
            }
        }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                it.findNavController().navigate(R.id.action_profile_to_intro)
            }

            viewModel.userResult.observe(viewLifecycleOwner) {
                if (it.message.isNotEmpty()) {
                    Snackbar.make(view, it.message, Snackbar.LENGTH_LONG).show()
                }
            }

            viewModel.sharingLocation.postValue(
                PreferenceData.getInstance().getSharing(requireContext())
            )

            viewModel.sharingLocation.observe(viewLifecycleOwner) {
                it?.let {
                    if (it) {
                        if (!hasPermissions(requireContext())) {
                            viewModel.sharingLocation.postValue(false)
                            requestPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } else {
                            PreferenceData.getInstance().putSharing(requireContext(), true)
                        }
                    } else {
                        PreferenceData.getInstance().putSharing(requireContext(), false)
                    }
                }
            }


        }
    }



}