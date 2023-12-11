package by.korsakovegor.photomap.mainactivity.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.korsakovegor.photomap.databinding.FragmentMapLayoutBinding
import by.korsakovegor.photomap.mainactivity.photos.viewmodels.PhotosViewModel
import by.korsakovegor.photomap.models.SignUserOutDto
import by.korsakovegor.photomap.utils.MainDb
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapFragment(private val user: SignUserOutDto?) : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    private lateinit var binding: FragmentMapLayoutBinding
    private lateinit var db:MainDb

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
        db = MainDb.getInstance(requireContext())
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        CoroutineScope(Dispatchers.IO).launch {
            val images = db.getImagesDao().getImages()
            val markers = ArrayList<MarkerOptions>()
            for (image in images){
                markers.add(MarkerOptions().position(LatLng(image.lat, image.lng)))
            }
            CoroutineScope(Dispatchers.Main).launch {
                for (marker in markers)
                    googleMap?.addMarker(marker)
                googleMap?.moveCamera(
                    CameraUpdateFactory
                        .newLatLngZoom(LatLng(images[0].lat, images[0].lng), 2.5f)
                )
            }
        }
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