package eu.mcomputng.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentLoginBinding
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var viewModel: AuthViewModel
    private var binding: FragmentLoginBinding? = null

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

        binding = FragmentLoginBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                Log.d("user", (result.localUser).toString())
                Log.d("message", result.message)
                Log.d("is not empty", result.message.isNotEmpty().toString())
                //login successful
                if (result.localUser != null) {
                    PreferenceData.getInstance().putUser(requireContext(), result.localUser)
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    //viewModel.getUser(requireContext(), "20")
                    //viewModel.getGeofence(requireContext())
                    findNavController().navigate(R.id.action_login_to_map)
                } else {
                    if (result.message.isNotEmpty()){
                        Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
                    }else{
                        //when there was logout, there is no message and new login has to be done
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}