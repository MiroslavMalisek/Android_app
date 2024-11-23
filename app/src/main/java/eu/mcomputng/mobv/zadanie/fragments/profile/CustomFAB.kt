package eu.mcomputng.mobv.zadanie.fragments.profile

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.mcomputng.mobv.zadanie.R

class CustomFAB(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    private lateinit var navController: NavController
    private lateinit var fab: FloatingActionButton
    private lateinit var fabEditImage: FloatingActionButton
    private lateinit var fabEditImageTextview: TextView
    private lateinit var fabChangePassword: FloatingActionButton
    private lateinit var fabChangePasswordTextview: TextView
    private var areFabsVisible: Boolean = false

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.fab_custom_layout, this, true)

        fab = view.findViewById(R.id.fab)
        fabEditImage = view.findViewById(R.id.fab_edit_image)
        fabEditImageTextview= view.findViewById(R.id.fab_edit_image_text)
        fabChangePassword = view.findViewById(R.id.fab_change_password)
        fabChangePasswordTextview = view.findViewById(R.id.fab_change_password_text)

        fabEditImage.visibility = View.GONE
        fabEditImageTextview.visibility = View.GONE
        fabChangePassword.visibility = View.GONE
        fabChangePasswordTextview.visibility = View.GONE


        fab.setOnClickListener{
            if (areFabsVisible){
                this.makeFabsInvisible()
            }else{
                this.makeFabsVisible()
            }
        }

        fabEditImage.setOnClickListener{
            Log.d("image", "clicked")
        }
        fabEditImageTextview.setOnClickListener{
            Log.d("imageView", "clicked")
        }
        fabChangePassword.setOnClickListener{
            Log.d("password", "clicked")
        }
        fabChangePasswordTextview.setOnClickListener{
            Log.d("passwordView", "clicked")
        }

    }

    private fun makeFabsVisible(){
        fabEditImage.show()
        fabChangePassword.show()
        fabEditImageTextview.visibility = View.VISIBLE
        fabChangePasswordTextview.visibility = View.VISIBLE
        areFabsVisible = true
    }

    fun makeFabsInvisible(){
        fabEditImage.hide()
        fabChangePassword.hide()
        fabEditImageTextview.visibility = View.GONE
        fabChangePasswordTextview.visibility = View.GONE
        areFabsVisible = false
    }
}