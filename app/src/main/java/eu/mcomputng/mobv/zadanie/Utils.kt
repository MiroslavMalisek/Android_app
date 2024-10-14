package eu.mcomputng.mobv.zadanie

import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import eu.mcomputng.mobv.zadanie.feed.FeedFragment

object Utils {
    val fragmentsWithBottomBar = setOf(R.id.navMapFragment, R.id.navFeedFragment, R.id.navProfileFragment)
    val options = navOptions {
        popUpTo(R.id.navLoginFragment) {
            inclusive = false  // Do not remove the main page from the stack
        }
    }
    val fragmentsWithFabVisible: Set<Class<out Fragment>> = setOf(
        RegisterFragment::class.java,
        MapFragment::class.java,
        FeedFragment::class.java,
        ProfileFragment::class.java
    )
}
