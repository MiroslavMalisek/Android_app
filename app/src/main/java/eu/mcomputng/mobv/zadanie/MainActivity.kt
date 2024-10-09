package eu.mcomputng.mobv.zadanie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import eu.mcomputng.mobv.zadanie.CustomConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {
    private var bottomBarVisible = false
    private lateinit var bottomNavBar: CustomConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_for_fragments)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_fragments)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navController = findNavController(R.id.nav_host_fragment)

        // Get the bottom navigation bar view
        bottomNavBar = findViewById(R.id.bottom_bar)
        //pass navController to the bottom navigation to be able to navigate between fragments
        bottomNavBar.setNavController(navController)

        //on every screen change, check if bottom bar should be visible
        navController.addOnDestinationChangedListener { _, destination, _ ->
            this.updateBottomNavBarVisibility(destination.id)
        }


/*        val register_button: Button = findViewById(R.id.register_button_main)
        register_button.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }
        val login_button: Button = findViewById(R.id.login_button_main)
        login_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
*/  }

    private fun updateBottomNavBarVisibility(destinationId: Int){
        if (Utils.fragmentsWithBottomBar.contains(destinationId)){
            if (!bottomBarVisible){
                bottomNavBar.postDelayed({
                    bottomNavBar.visibility = View.VISIBLE
                }, 200)
            }
            bottomBarVisible = true
        }
        else{
            bottomNavBar.visibility = View.GONE
            bottomBarVisible = false
        }
    }
}