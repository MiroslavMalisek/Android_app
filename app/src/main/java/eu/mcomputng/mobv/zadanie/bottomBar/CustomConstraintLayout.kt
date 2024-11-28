package eu.mcomputng.mobv.zadanie.bottomBar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils

class CustomConstraintLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var navController: NavController
    private var activeIcon: ImageView
    private var bottomBarVisible = false
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_bar_layout, this, true)
        val mapIcon: ImageView = findViewById(R.id.map_icon)
        this.activeIcon = mapIcon
        mapIcon.setOnClickListener{
            this.navigateToFragment(this.navController.currentDestination?.id,
                R.id.navMapFragment, mapIcon)
        }
        val feedIcon: ImageView = findViewById(R.id.feed_icon)
        feedIcon.setOnClickListener{
            this.navigateToFragment(this.navController.currentDestination?.id,
                R.id.navFeedFragment, feedIcon)
        }
        val profileIcon: ImageView = findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener{
            this.navigateToFragment(this.navController.currentDestination?.id,
                R.id.navProfileFragment, profileIcon)
        }

    }

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun updateBottomNavBarVisibility(destinationId: Int){
        if (Utils.fragmentsWithBottomBar.contains(destinationId)){
            this.updateBarIcons()
            if (!bottomBarVisible){
                this.visibility = View.VISIBLE
            }
            bottomBarVisible = true
        }
        else{
            this.visibility = View.GONE
            bottomBarVisible = false
        }
    }

    private fun navigateToFragment(currentFragment: Int?, destinationFragment: Int, icon: ImageView){
        if (currentFragment != destinationFragment){
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

    private fun updateBarIcons(){
        if(this.navController.currentDestination?.id == R.id.navMapFragment){
            updateIconsColor(findViewById(R.id.map_icon))
        }else if (this.navController.currentDestination?.id == R.id.navFeedFragment){
            updateIconsColor(findViewById(R.id.feed_icon))
        }else if (this.navController.currentDestination?.id == R.id.navProfileFragment){
            updateIconsColor(findViewById(R.id.profile_icon))
        }
    }

    private fun updateIconsColor(icon: ImageView){
        this.activeIcon.setColorFilter(ContextCompat.getColor(context, R.color.icon_inactive))
        icon.setColorFilter(ContextCompat.getColor(context, R.color.icon_active))
        this.activeIcon = icon
    }

}