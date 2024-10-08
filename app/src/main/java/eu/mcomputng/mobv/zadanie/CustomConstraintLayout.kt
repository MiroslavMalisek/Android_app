package eu.mcomputng.mobv.zadanie

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

class CustomConstraintLayout(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_bar_layout, this, true)
        val map_icon: ImageView = findViewById(R.id.map_icon)
        map_icon.setOnClickListener{
            Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction("Home"){
                    findNavController().navigate(R.id.action_map_to_feed)
                }.setAnchorView(R.id.fab)
                .show()
        }
        val profile_icon: ImageView = findViewById(R.id.profile_icon)
        profile_icon.setOnClickListener{
            Snackbar.make(view, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction("Home"){
                    findNavController().navigate(R.id.action_map_to_profile)
                }.setAnchorView(R.id.fab)
                .show()
        }
    }

    fun updateVisibilityAndColorForFragment(destinationId: Int) {
        when(destinationId){
            R.id.navMapFragment -> {
                postDelayed({
                    visibility = View.VISIBLE
                }, 200)
            }
            else -> {
                visibility = View.GONE
            }
        }
    }
}