package eu.mcomputng.mobv.zadanie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val register_button: Button = view.findViewById(R.id.register_button)
        register_button.setOnClickListener{
            Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction("Home"){
                    findNavController().navigate(R.id.action_register_to_intro)
                }
                .show()
        }
        return view
    }
}