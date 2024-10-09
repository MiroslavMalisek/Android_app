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
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_bar_layout, this, true)
        val map_icon: ImageView = findViewById(R.id.map_icon)
        this.updateIconColor(this.navController.currentDestination?.id, R.id.navMapFragment, map_icon)
        val feed_icon: ImageView = findViewById(R.id.feed_icon)
        this.updateIconColor(this.navController.currentDestination?.id, R.id.navFeedFragment, feed_icon)
        val profile_icon: ImageView = findViewById(R.id.profile_icon)
        this.updateIconColor(this.navController.currentDestination?.id, R.id.navProfileFragment, profile_icon)



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

    private fun updateIconColor(currentDestId: Int?, iconNavFragment: Int, icon: ImageView){
        if (currentDestId == iconNavFragment){
            icon.imageTintList = ContextCompat.getColorStateList(context, R.color.icon_active)
        }
        else{
            icon.imageTintList = ContextCompat.getColorStateList(context, R.color.icon_inactive)
        }
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