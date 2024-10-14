package eu.mcomputng.mobv.zadanie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MapFragment : Fragment() {
    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapView = view.findViewById<MapView>(R.id.mapView)
        val bottomBar = requireActivity().findViewById<ConstraintLayout>(R.id.bottom_bar)
        //adjust bottom of map to top of bottom nav bar
        val layoutParams = mapView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin = bottomBar.height
        mapView.layoutParams = layoutParams
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapView: MapView = view.findViewById(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

    }


}