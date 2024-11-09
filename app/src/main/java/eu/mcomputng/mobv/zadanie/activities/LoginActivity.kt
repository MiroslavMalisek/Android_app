package eu.mcomputng.mobv.zadanie.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.mcomputng.mobv.zadanie.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login_button: Button = findViewById(R.id.login_button)
        login_button.setOnClickListener{
            val username: String = findViewById<EditText>(R.id.username_edittext).text.toString()
            val password: String = findViewById<EditText>(R.id.password_edittext).text.toString()
            Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
        }
    }
}