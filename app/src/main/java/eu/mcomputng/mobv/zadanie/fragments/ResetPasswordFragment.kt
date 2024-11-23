package eu.mcomputng.mobv.zadanie.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils.hideKeyboard
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentLoginBinding
import eu.mcomputng.mobv.zadanie.databinding.FragmentResetPasswordBinding
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private lateinit var viewModel: AuthViewModel
    private var binding: FragmentResetPasswordBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResetPasswordBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.resetPasswordResult.observe(viewLifecycleOwner) { result ->
                //reset request successful
                hideKeyboard(requireActivity())
                //clear focus from text inputs after click
                bnd.emailEdittext.clearFocus()
                if (result.success) {
                    PreferenceData.getInstance().putResetPasswordUserEmail(requireContext(), viewModel.resetPasswordEmail.value.toString())
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).setAction("Login") {
                        viewModel.clearResetPassword()
                        findNavController().navigate(R.id.action_reset_to_login)
                    }.show()
                    viewModel.clearResetPassword()
                } else {
                    if (result.message.isNotEmpty()){
                        Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
                        viewModel.clearResetPasswordResult()
                    }
                }
            }
            //hide keyboard and remove focus from inputs when user click on screen
            view.setOnTouchListener { _, _ ->
                hideKeyboard(requireActivity()) // Hide keyboard
                bnd.emailEdittext.clearFocus()
                false // Return false to allow other touch events to be processed
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}