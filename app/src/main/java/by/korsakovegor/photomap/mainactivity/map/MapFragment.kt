package by.korsakovegor.photomap.mainactivity.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.R
import by.korsakovegor.photomap.databinding.ActivityMainBinding
import by.korsakovegor.photomap.databinding.FragmentMapLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.SignUserOutDto
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment(private val user: SignUserOutDto?) : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private lateinit var binding: FragmentMapLayoutBinding
    private lateinit var viewModel: PhotosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapLayoutBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView?.onResume()
        viewModel = ViewModelProvider(this)[PhotosViewModel::class.java]
        viewModel.setUserToken(user?.token ?: "")
        viewModel.getImages()

        viewModel.images.observe(viewLifecycleOwner){
            if(googleMap != null){
                if (it != null) {
                    for(image in it) {
                        googleMap!!.addMarker(MarkerOptions().position(LatLng(image.lat, image.lng)))
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}