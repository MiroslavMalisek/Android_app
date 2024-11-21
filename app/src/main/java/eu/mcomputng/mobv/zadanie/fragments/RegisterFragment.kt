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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils.hideKeyboard
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        //val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        //fab.visibility = View.VISIBLE

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel>create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]

        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            //Log.d("user", (result.user).toString())
            //registration successful
            if (result.localUser != null) {
                PreferenceData.getInstance().putUser(requireContext(), result.localUser)
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_register_to_map)
            } else {
                if (result.message.isNotEmpty()){
                    Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
                }else{
                    //when there was logout, there is no message and new login has to be done
                }
            }
        }

        val registerButton: Button = view.findViewById(R.id.registerButton)
        registerButton.setOnClickListener{
            hideKeyboard(requireActivity())
            //clear focus from text inputs after login click
            view.findViewById<EditText>(R.id.username_edittext).clearFocus()
            view.findViewById<EditText>(R.id.email_edittext).clearFocus()
            view.findViewById<EditText>(R.id.password_edittext).clearFocus()
            view.findViewById<EditText>(R.id.password_repeat_edittext).clearFocus()

            val password =  view.findViewById<EditText>(R.id.password_edittext).text.toString()
            val repeatPassword = view.findViewById<EditText>(R.id.password_repeat_edittext).text.toString()
            if (password != repeatPassword){
                Snackbar.make(view, R.string.registerErrorRepeatPassword, Snackbar.LENGTH_LONG).show()
            }else{
                viewModel.registerUser(
                    requireContext(),
                    view.findViewById<EditText>(R.id.username_edittext).text.toString(),
                    view.findViewById<EditText>(R.id.email_edittext).text.toString(),
                    password
                )
            }
        }

        //hide keyboard and remove focus from inputs when user click on screen
        view.setOnTouchListener { _, _ ->
            hideKeyboard(requireActivity()) // Hide keyboard
            view.findViewById<EditText>(R.id.username_edittext).clearFocus()
            view.findViewById<EditText>(R.id.email_edittext).clearFocus()
            view.findViewById<EditText>(R.id.password_edittext).clearFocus()
            view.findViewById<EditText>(R.id.password_repeat_edittext).clearFocus()
            false // Return false to allow other touch events to be processed
        }

        return view
    }
}