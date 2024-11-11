package eu.mcomputng.mobv.zadanie

import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import eu.mcomputng.mobv.zadanie.fragments.feed.FeedFragment
import eu.mcomputng.mobv.zadanie.fragments.MapFragment
import eu.mcomputng.mobv.zadanie.fragments.ProfileFragment
import java.security.MessageDigest

object Utils {
    val fragmentsWithBottomBar = setOf(R.id.navMapFragment, R.id.navFeedFragment, R.id.navProfileFragment)
    val options = navOptions {
        popUpTo(R.id.navLoginFragment) {
            inclusive = false  // Do not remove the main page from the stack
        }
    }
    val fragmentsWithFabVisible: Set<Class<out Fragment>> = setOf(
        MapFragment::class.java,
        FeedFragment::class.java,
        ProfileFragment::class.java
    )

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
