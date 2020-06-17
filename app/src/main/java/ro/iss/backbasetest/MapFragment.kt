package ro.iss.backbasetest

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ro.iss.backbasetest.utils.Constants.COORDINATES_KEY
import ro.iss.backbasetest.utils.Constants.MARKER_KEY

/**
 * The fragment needed for showing the map of a specific coordinates location
 *
 */
class MapFragment: SupportMapFragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var currentCoordinates: LatLng? = null
    private var currentMarkerTitle: String? = null

    companion object {
        fun newInstance(
            coordinates: LatLng,
            markerTitle: String?
        ): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putParcelable(
                COORDINATES_KEY,
                coordinates
            )
            args.putString(
                MARKER_KEY,
                markerTitle
            )
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
        currentCoordinates =
            arguments?.getParcelable(
                COORDINATES_KEY
            )
        currentMarkerTitle =
            arguments?.getString(
                MARKER_KEY
            )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard(view)
        (context as? MainActivity)?.supportActionBar?.hide()
        getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        currentCoordinates?.let {
            mMap = googleMap

            mMap?.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(currentMarkerTitle)
            )

            //Build camera position
            val cameraPosition = CameraPosition.Builder()
                .target(currentCoordinates)
                .zoom(17f).build()
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }


    private fun hideKeyboard(view: View) {
        val inputMethod =
            context?.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as? InputMethodManager

        inputMethod?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        (context as? MainActivity)?.supportActionBar?.show()
    }
}