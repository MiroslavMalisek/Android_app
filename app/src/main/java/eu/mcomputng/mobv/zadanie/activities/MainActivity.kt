package eu.mcomputng.mobv.zadanie.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import eu.mcomputng.mobv.zadanie.bottomBar.CustomConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils

class MainActivity : AppCompatActivity() {
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
            bottomNavBar.updateBottomNavBarVisibility(destination.id)
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

    /*
    private fun updateInitialBottomBarIconColor(){
        val mapIcon: ImageView = findViewById(R.id.map_icon)
        mapIcon.setColorFilter(ContextCompat.getColor(this, R.color.icon_active))
        val feedIcon: ImageView = findViewById(R.id.feed_icon)
        feedIcon.setColorFilter(ContextCompat.getColor(this, R.color.icon_inactive))
        val ProfileIcon: ImageView = findViewById(R.id.profile_icon)
        ProfileIcon.setColorFilter(ContextCompat.getColor(this, R.color.icon_inactive))

    }

    private fun updateBottomNavBarVisibility(destinationId: Int){
        if (Utils.fragmentsWithBottomBar.contains(destinationId)){
            if (!bottomBarVisible){
                this.updateInitialBottomBarIconColor()
                //set active icon to default (map)
                this.bottomNavBar.restoreActiveIcon()
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
    }*/

    /*private fun fabVisibilityListener() {
        // Add OnBackStackChangedListener to listen for fragment transactions
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.childFragmentManager.addOnBackStackChangedListener {
            val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()
            // Check if the current fragment is one with fab visible
            if (currentFragment != null && Utils.fragmentsWithFabVisible.contains(currentFragment::class.java)) {
                fab.visibility = View.VISIBLE
            } else {
                fab.visibility = View.GONE
            }
        }
    }*/
}