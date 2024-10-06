package eu.mcomputng.mobv.zadanie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton

class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_intro, container, false)
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        val login_button: MaterialButton = view.findViewById(R.id.login_button_main)
        login_button.setOnClickListener {
            findNavController().navigate(R.id.action_intro_to_login)
        }
        val register_button: Button = view.findViewById(R.id.register_button_main)
        register_button.setOnClickListener {
            findNavController().navigate(R.id.action_intro_to_register)
        }

        return view

    }

}