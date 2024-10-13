package eu.mcomputng.mobv.zadanie.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.mcomputng.mobv.zadanie.R


class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        val bottomBar = requireActivity().findViewById<ConstraintLayout>(R.id.bottom_bar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.feed_recyclerview)
        // Set bottom margin of RecyclerView to match top of Bottom Navigation Bar
        val layoutParams = recyclerView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin = bottomBar.height
        recyclerView.layoutParams = layoutParams

        //using inner constraint layout and bottom to top
        /*val fragmentRootView = view.findViewById<ConstraintLayout>(R.id.fragment_feed_root)
        Log.d("bar_id", bottomBar.id.toString())
        Log.d("layout_before", (fragmentRootView.layoutParams as ConstraintLayout.LayoutParams).bottomToTop.toString())
        val constraintSet = ConstraintSet()
        constraintSet.clone(fragmentRootView)
        constraintSet.connect(
            fragmentRootView.id, // The ID of the fragment's root view
            ConstraintSet.BOTTOM, // The edge of the fragment's root view to constrain
            bottomBar.id, // The ID of the bottom bar to constrain to
            ConstraintSet.TOP // The edge of the bottom bar to constrain to
        )
        constraintSet.applyTo(fragmentRootView)
        */
        //another way
        /*val layoutParams = fragmentRootView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomToTop = bottomBar.id
         */
        // straight to RV
        /*recyclerView.layoutParams = layoutParams
         */
        //Log.d("layout_after", (view.findViewById<ConstraintLayout>(R.id.fragment_feed_root).layoutParams as ConstraintLayout.LayoutParams).bottomToTop.toString())


        val layoutManager = LinearLayoutManager(context)
        //layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        val feedAdapter = FeedAdapter()
        recyclerView.adapter = feedAdapter
        feedAdapter.updateItems(listOf(
            ItemModel(R.drawable.map_foreground,"Mapa"),
            ItemModel(R.drawable.file_foreground,"File"),
            ItemModel(R.drawable.person_foreground,"Person"),
            ItemModel(R.drawable.ic_add_white_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
            ItemModel(R.drawable.map_foreground,"Launcher"),
        ))
        return view
    }
}