package eu.mcomputng.mobv.zadanie.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.databinding.FragmentMapBinding
import eu.mcomputng.mobv.zadanie.viewModels.MapViewModel
import eu.mcomputng.mobv.zadanie.viewModels.ProfileViewModel

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var mapViewModel: MapViewModel
    private var locationAcquired: Boolean = false
    private var sharingLocationAllowed: Boolean = false
    private var selectedPoint: CircleAnnotation? = null
    private var lastLocationMapPoint: Point? = null
    private var lastLocationAndroidLocation: Location? = null
    private lateinit var annotationManager: CircleAnnotationManager
    private var radius: Double = 100.0
    private var mapView: MapView? = null

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted && locationAcquired) {
                initLocationComponent()
                addLocationListeners()
            }
        }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        mapViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[MapViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()

            onMapReady()

            bnd.myLocation.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    locationAcquired = true
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } else {
                    if (locationAcquired){
                        lastLocationMapPoint?.let { centerViewOnLocation(it) }
                    }else{
                        locationAcquired = true
                        initLocationComponent()
                    }
                    addLocationListeners()
                }
            }
        }

        //load initial sharing from preference data
        profileViewModel.sharingLocation.postValue(
            PreferenceData.getInstance().getSharing(requireContext())
        )

        profileViewModel.sharingLocation.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    Log.d("switch sharing to", "true")
                    sharingLocationAllowed = true
                } else {
                    Log.d("switch sharing to", "false")
                    sharingLocationAllowed = false
                }
            }
        }

        mapViewModel.updateLocationResult.observe(viewLifecycleOwner){result ->
            //update of location failed
            if (!result.success){
                Snackbar.make(view, result.message, Snackbar.LENGTH_LONG).show()
            }else{
                Log.d("location updated: ", result.success.toString())
            }
        }


    }


    private fun onMapReady() {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(14.3539484, 49.8001304))
                .zoom(2.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS)

        binding.mapView.getMapboxMap().addOnMapClickListener {
            if (hasPermissions(requireContext())) {
                onCameraTrackingDismissed()
            }
            true
        }
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
        }

    }

    private fun addLocationListeners() {
        binding.mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        binding.mapView.gestures.addOnMoveListener(onMoveListener)

    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        //Log.d("MapFragment", "poloha je $it")
        this.refreshLocation(it)
    }

    private fun refreshLocation(point: Point) {
        // Convert the Mapbox Point to an Android Location
        val newLocation = Location("").apply {
            latitude = point.latitude()
            longitude = point.longitude()
        }

        lastLocationAndroidLocation?.let { previousLocation ->
            // Calculate the distance using Android's Location API
            val distance = newLocation.distanceTo(previousLocation)
            if (distance < 1) {
                //Log.d("MapFragment", "Distance is less than 1 meter, skipping refresh.")
                return
            }
        }
        this.centerViewOnLocation(point)
        lastLocationMapPoint = point
        lastLocationAndroidLocation = newLocation
        if (sharingLocationAllowed){
            Log.d("should update:", "yes")
            mapViewModel.updateLocation(requireContext(), point.latitude(), point.longitude(), radius)
            mapViewModel.getGeofence(requireContext())
        }else{
            Log.d("should update:", "no")
        }

    }

    private fun centerViewOnLocation(point: Point){
        binding.mapView.getMapboxMap()
            .setCamera(CameraOptions.Builder().center(point).zoom(14.0).build())
        binding.mapView.gestures.focalPoint =
            binding.mapView.getMapboxMap().pixelForCoordinate(point)

        addMarker(point)
        Log.d("MapFragment", "Location refreshed to $point")
    }

    private fun addMarker(point: Point) {

        if (selectedPoint == null) {
            annotationManager.deleteAll()
            val pointAnnotationOptions = CircleAnnotationOptions()
                .withPoint(point)
                .withCircleRadius(radius)
                .withCircleOpacity(0.2)
                .withCircleColor("#000")
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff")
            selectedPoint = annotationManager.create(pointAnnotationOptions)
        } else {
            selectedPoint?.let {
                it.point = point
                annotationManager.update(it)
            }
        }
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }


    private fun onCameraTrackingDismissed() {
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapView: MapView = view.findViewById(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)

    }*/


}