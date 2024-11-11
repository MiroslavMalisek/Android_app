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
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel

class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]

        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            Log.d("user", (result.localUser).toString())
            //registration successful
            if (result.localUser != null) {
                PreferenceData.getInstance().putUser(requireContext(), result.localUser)
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                viewModel.getUser(requireContext(), "20")
                //findNavController().navigate(R.id.action_register_to_feed)
            } else {
                Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
            }
        }

        val loginButton: Button = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener{
            viewModel.loginUser(
                requireContext(),
                view.findViewById<EditText>(R.id.username_edittext).text.toString(),
                view.findViewById<EditText>(R.id.password_edittext).text.toString()
            )
        }
        return view
    }
}