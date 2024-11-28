package eu.mcomputng.mobv.zadanie.fragments.feed

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.viewModels.AuthViewModel
import eu.mcomputng.mobv.zadanie.viewModels.FeedViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel


class FeedFragment : Fragment() {

    private lateinit var viewModel: FeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(DataRepository.getInstance(requireContext()), requireContext()) as T
            }
        })[FeedViewModel::class.java]

    }

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

        val emptyMessage = view.findViewById<TextView>(R.id.empty_message)

        viewModel.updateItems()

        viewModel.feed_items.observe(viewLifecycleOwner) { users ->
            users?.let {
                if (users.isEmpty()) {
                    val feedEmptyMessageReason: String
                    if (!PreferenceData.getInstance().getSharing(requireContext())){
                        feedEmptyMessageReason = getString(R.string.emptyRVNotSharingLocation)
                    }else if (!PreferenceData.getInstance().getLocationAcquired(requireContext())){
                        feedEmptyMessageReason = getString(R.string.emptyRVLocationNotAcquired)
                    }else{
                        feedEmptyMessageReason = getString(R.string.emptyRVnoActiveUsers)
                    }
                    emptyMessage.text = feedEmptyMessageReason
                    recyclerView.visibility = View.GONE
                    emptyMessage.visibility = View.VISIBLE
                }else{
                    recyclerView.visibility = View.VISIBLE
                    emptyMessage.visibility = View.GONE
                    feedAdapter.updateItems(it.map { user ->
                        ItemModel(
                            id = user.id.toInt(),
                            //imageResId = R.drawable.person_foreground, // Replace with appropriate logic
                            image = user.photo,
                            name = user.name,
                            updated = user.updated // Replace with actual timestamp field
                        )
                    })
                }

            }
        }

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Trigger data update when user pulls to refresh
            viewModel.updateItems()
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (!it) {
                swipeRefreshLayout.isRefreshing = false
            }
        }


        /*
        feedAdapter.updateItems(listOf(
            ItemModel(1, R.drawable.map_foreground,"Mapa", "2023-10-28 16:24:34"),
            ItemModel(2, R.drawable.file_foreground,"File","2023-10-28 16:24:34"),
            ItemModel(3, R.drawable.person_foreground,"Person", "2023-10-28 16:24:34"),
            ItemModel(4, R.drawable.ic_add_white_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(4, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(6, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(7, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(8, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(9, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(10, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
            ItemModel(11, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
        ))

        val updateListButton: Button = view.findViewById(R.id.updateRVListButton)
        updateListButton.setOnClickListener{
            feedAdapter.updateItems(listOf(
                ItemModel(1, R.drawable.map_foreground,"Mapa", "2023-10-28 16:24:34"),
                ItemModel(11, R.drawable.map_foreground,"Launcher", "2023-10-28 16:24:34"),
                ItemModel(2, R.drawable.file_foreground,"File", "2023-10-28 16:24:34"),
                ItemModel(15, R.drawable.person_foreground,"Person", "2023-10-28 16:24:34"),
            ))
        }*/

        /*val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            Log.d("fab", "click")
            feedAdapter.addItem(ItemModel(20, R.drawable.map_foreground,"New Item"),)
        }*/
        return view
    }
}