package eu.mcomputng.mobv.zadanie.fragments.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils.hideKeyboard
import eu.mcomputng.mobv.zadanie.Utils.isAfterResetPassword
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentChangePasswordBinding
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var profileViewModel: ProfileViewModel
    private var binding: FragmentChangePasswordBinding? = null
    private var backArrowVisibility: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChangePasswordBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = profileViewModel
        }.also { bnd ->
            profileViewModel.changePasswordResult.observe(viewLifecycleOwner) { result ->
                //change request successful
                hideKeyboard(requireActivity())
                //clear focus from text inputs after click
                bnd.changePasswordActualEdittext.clearFocus()
                bnd.changePasswordNewEdittext.clearFocus()
                bnd.changePasswordRepeatEdittext.clearFocus()
                if (result.success) {
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
                    profileViewModel.clearChangePassword()
                    if (isAfterResetPassword(requireContext())){
                        //redirect to map if there was a password reset
                        PreferenceData.getInstance().putResetPasswordUserEmail(requireContext(), null)
                        findNavController().navigate(R.id.action_change_password_to_map)
                    }
                } else {
                    if (result.message.isNotEmpty()){
                        Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
                        profileViewModel.clearChangePasswordResult()
                    }
                }
            }

            if (isAfterResetPassword(requireContext())){
                bnd.backArrow.visibility = View.INVISIBLE
            }else{
                bnd.backArrow.visibility = View.VISIBLE
            }

            bnd.backArrow.setOnClickListener{
                profileViewModel.clearChangePassword()
                findNavController().popBackStack()
            }

            //hide keyboard and remove focus from inputs when user click on screen
            view.setOnTouchListener { _, _ ->
                hideKeyboard(requireActivity()) // Hide keyboard
                bnd.changePasswordActualEdittext.clearFocus()
                bnd.changePasswordNewEdittext.clearFocus()
                bnd.changePasswordRepeatEdittext.clearFocus()
                false // Return false to allow other touch events to be processed
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}