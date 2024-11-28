package eu.mcomputng.mobv.zadanie

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.navOptions
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.fragments.profile.ProfileFragment
import java.io.File
import java.security.MessageDigest

object Utils {

    const val PHOTOBASEURI: String = "https://upload.mcomputing.eu/"

    val fragmentsWithBottomBar = setOf(R.id.navMapFragment, R.id.navFeedFragment, R.id.navProfileFragment)
    val options = navOptions {
        popUpTo(R.id.navIntroFragment) {
            inclusive = false  // Do not remove the main page from the stack
        }
    }
    val fragmentsWithFabVisible: Set<Class<out Fragment>> = setOf(
        ProfileFragment::class.java
    )

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun hideKeyboard(activity: FragmentActivity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isAfterResetPassword(context: Context): Boolean {
        val user = PreferenceData.getInstance().getUser(context)
        val emailResetPassword: String? =
            PreferenceData.getInstance().getResetPasswordUserEmail(context)
        user?.let { storedUser ->
            if (emailResetPassword != null && (storedUser.username == emailResetPassword)) {
                return true
            }
        }
        return false
    }

    fun displayPhotoFromFile(imageView: ImageView, file: File) {
        //val imageView = requireView().findViewById<ImageView>(R.id.photo_editor_preview)

        Picasso.get()
            .load(file)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            //.placeholder(R.drawable.ic_placeholder) // Show a placeholder while loading
            //.error(R.drawable.ic_error_placeholder) // Show an error image if loading fails
            .into(imageView)
    }

    fun displayPhotoFromUri(imageView: ImageView, uri: String) {
        //val imageView = requireView().findViewById<ImageView>(R.id.photo_editor_preview)

        Picasso.get()
            .load(uri)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            //.placeholder(R.drawable.ic_placeholder) // Show a placeholder while loading
            //.error(R.drawable.ic_error_placeholder) // Show an error image if loading fails
            .into(imageView)
    }
}
