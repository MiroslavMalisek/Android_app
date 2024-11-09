package eu.mcomputng.mobv.zadanie.fragments.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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
            ItemModel(1, R.drawable.map_foreground,"Mapa"),
            ItemModel(2, R.drawable.file_foreground,"File"),
            ItemModel(3, R.drawable.person_foreground,"Person"),
            ItemModel(4, R.drawable.ic_add_white_foreground,"Launcher"),
            ItemModel(4, R.drawable.map_foreground,"Launcher"),
            ItemModel(6, R.drawable.map_foreground,"Launcher"),
            ItemModel(7, R.drawable.map_foreground,"Launcher"),
            ItemModel(8, R.drawable.map_foreground,"Launcher"),
            ItemModel(9, R.drawable.map_foreground,"Launcher"),
            ItemModel(10, R.drawable.map_foreground,"Launcher"),
            ItemModel(11, R.drawable.map_foreground,"Launcher"),
        ))

        val updateListButton: Button = view.findViewById(R.id.updateRVListButton)
        updateListButton.setOnClickListener{
            feedAdapter.updateItems(listOf(
                ItemModel(1, R.drawable.map_foreground,"Mapa"),
                ItemModel(11, R.drawable.map_foreground,"Launcher"),
                ItemModel(2, R.drawable.file_foreground,"File"),
                ItemModel(15, R.drawable.person_foreground,"Person"),
            ))
        }

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            Log.d("fab", "click")
            feedAdapter.addItem(ItemModel(20, R.drawable.map_foreground,"New Item"),)
        }
        return view
    }
}