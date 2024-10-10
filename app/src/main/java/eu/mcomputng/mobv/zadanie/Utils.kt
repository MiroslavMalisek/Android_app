package eu.mcomputng.mobv.zadanie

import androidx.navigation.navOptions

object Utils {
    val fragmentsWithBottomBar = setOf(R.id.navMapFragment, R.id.navFeedFragment, R.id.navProfileFragment)
    val options = navOptions {
        popUpTo(R.id.navLoginFragment) {
            inclusive = false  // Do not remove the main page from the stack
        }
    }
}
