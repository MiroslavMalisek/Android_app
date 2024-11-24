package eu.mcomputng.mobv.zadanie

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.navOptions
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.fragments.profile.ProfileFragment
import java.security.MessageDigest

object Utils {
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
}
