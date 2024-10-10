package eu.mcomputng.mobv.zadanie

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

class CustomConstraintLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var navController: NavController
    private lateinit var activeIcon: ImageView
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_bar_layout, this, true)
        val mapIcon: ImageView = findViewById(R.id.map_icon)
        this.activeIcon = mapIcon
        mapIcon.setOnClickListener{
            //this.updateIconColor(this.navController.currentDestination?.id, R.id.navMapFragment, mapIcon)
            this.navigateToFragment(this.navController.currentDestination?.id, R.id.navMapFragment, mapIcon)
        }
        val feedIcon: ImageView = findViewById(R.id.feed_icon)
        feedIcon.setOnClickListener{
            //this.updateIconColor(this.navController.currentDestination?.id, R.id.navFeedFragment, feedIcon)
            this.navigateToFragment(this.navController.currentDestination?.id, R.id.navFeedFragment, feedIcon)
        }
        val profileIcon: ImageView = findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener{
            //this.updateIconColor(this.navController.currentDestination?.id, R.id.navProfileFragment, profileIcon)
            this.navigateToFragment(this.navController.currentDestination?.id, R.id.navProfileFragment, profileIcon)
        }




        /*map_icon.setOnClickListener{
            Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction("Home"){
                    findNavController().navigate(R.id.action_map_to_feed)
                }.setAnchorView(R.id.fab)
                .show()
        }*/
        /*val profile_icon: ImageView = findViewById(R.id.profile_icon)
        profile_icon.setOnClickListener{
            if (navController.currentDestination?.id == R.id.navProfileFragment){
                Log.d("nav", "zhoda")

            }
            else{
                Log.d("nav", "rozne")
                navController.navigate(R.id.action_feed_to_profile)
            }

        }*/
    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    private fun navigateToFragment(currentFragment: Int?, destinationFragment: Int, icon: ImageView){
        if (currentFragment != destinationFragment){
            this.updateIconsColor(icon)
            val action: Int = this.decideAction(currentFragment, destinationFragment)
            //dont save fragments with bottom bar to back stash
            this.navController.navigate(action, null, Utils.options)
        }
    }

    private fun decideAction(currentFragment: Int?, destinationFragment: Int): Int{
        if ((currentFragment == R.id.navMapFragment) && (destinationFragment == R.id.navFeedFragment)){
            return R.id.action_map_to_feed
        }
        else if ((currentFragment == R.id.navMapFragment) && (destinationFragment == R.id.navProfileFragment)){
            return R.id.action_map_to_profile
        }
        else if ((currentFragment == R.id.navProfileFragment) && (destinationFragment == R.id.navMapFragment)){
            return R.id.action_profile_to_map
        }
        else if ((currentFragment == R.id.navProfileFragment) && (destinationFragment == R.id.navFeedFragment)){
            return R.id.action_profile_to_feed
        }
        else if ((currentFragment == R.id.navFeedFragment) && (destinationFragment == R.id.navMapFragment)){
            return R.id.action_feed_to_map
        }
        else{
            //feed to profile
            return R.id.action_feed_to_profile
        }
    }

    private fun updateIconsColor(icon: ImageView){
        this.activeIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_inactive))
        this.activeIcon = icon
        icon.setColorFilter(ContextCompat.getColor(context, R.color.icon_active))
    }

    fun restoreActiveIcon(){
        this.activeIcon = findViewById(R.id.map_icon)
    }
    /*fun updateVisibilityAndColorForFragment(destinationId: Int) {
        Log.d("update", destinationId.toString())
        if (Utils.fragmentsWithBottomBar.contains(destinationId)){
            postDelayed({
                visibility = View.VISIBLE
            }, 200)
        }
        else{
            visibility = View.GONE
        }
        /*when(destinationId){
            R.id.navMapFragment -> {
                postDelayed({
                    visibility = View.VISIBLE
                }, 200)
            }
            else -> {
                visibility = View.GONE
            }
        }*/
    }*/
}