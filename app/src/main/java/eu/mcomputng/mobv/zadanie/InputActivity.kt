package eu.mcomputng.mobv.zadanie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val register_button: Button = findViewById(R.id.register_button)
        register_button.setOnClickListener{
            val username: String = findViewById<EditText>(R.id.username_edittext).text.toString()
            val password: String = findViewById<EditText>(R.id.password_edittext).text.toString()
            Toast.makeText(getApplicationContext(), username, Toast.LENGTH_LONG).show();
        }
    }
}